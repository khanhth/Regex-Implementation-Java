import java.util.Set;

sealed interface RegEx permits Literal, Concat, Alternation, Repetition, Empty, CharClass {

    <R> R accept(RegExVisitor<R> visitor);
}

record Literal(char value) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

record Concat(RegEx left, RegEx right) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

record Alternation(RegEx left, RegEx right) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

record Repetition(RegEx expr, int min, int max) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

record Empty() implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}

record CharClass(Set<Character> characters) implements RegEx {

    public <R> R accept(RegExVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
