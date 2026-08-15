import java.util.Set;

sealed interface RegEx permits Literal, Concat, Alternation, Repetition, Empty, CharClass, Null {

    <R> R accept(RegExVisitor<R> visitor);

    boolean isNullable();

    RegEx derive(char c);
}

record Literal(char value) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return false;
    }

    public RegEx derive(char c) {
        return (this.value == c) ? new Empty() : new Null();
    }
}

record Concat(RegEx left, RegEx right) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        // TODO: @ktr to construct a usecase where left is nullable but right is not, and vice versa, to ensure this logic is correct.
        return left.isNullable() && right.isNullable();
    }

    public RegEx derive(char c) {
        // Derivative rule: d(AB)/dc = d(A)/dc * B  +  (if A is nullable, then also try matching B directly)
        RegEx derivedLeft = new Concat(left.derive(c), right);
        if (left.isNullable()) {
            return new Alternation(derivedLeft, right.derive(c));
        }
        return derivedLeft;
    }
}

record Alternation(RegEx left, RegEx right) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return left.isNullable() || right.isNullable();
    }

    public RegEx derive(char c) {
        return new Alternation(left.derive(c), right.derive(c));
    }
}

record Repetition(RegEx expr, int min, int max) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return min == 0 || expr.isNullable();
    }

    public RegEx derive(char c) {
        // Simple case for * or + repetitions
        RegEx derivedExpr = expr.derive(c);
        return new Concat(derivedExpr, this); // Match once, then continue the loop
    }
}

record Empty() implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return true;
    }

    public RegEx derive(char c) {
        // Empty string can't consume characters
        return new Null();
    }
}

record CharClass(Set<Character> characters) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return false;
    }

    public RegEx derive(char c) {
        return characters.contains(c) ? new Empty() : new Null();
    }
}

// A helper node representing a completely dead match path
record Null() implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public boolean isNullable() {
        return false;
    }

    public RegEx derive(char c) {
        return this; // TODO: @ktr to check when this is used in the derivative computation.
    }
}
