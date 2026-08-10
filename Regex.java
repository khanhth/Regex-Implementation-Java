import java.util.List;

public sealed interface RegEx permits Literal, Concat, Alternation, Repetition, Empty {
    <R> R accept(RegExVisitor<R> visitor);
}

interface RegExVisitor<R> {
    R visit(Literal node);
    R visit(Concat node);
    R visit(Alternation node);
    R visit(Repetition node);
    R visit(Empty node);
}

record Literal(char value) implements RegEx {
    public <R> R accept(RegExVisitor<R> visitor) { return visitor.visit(this); }
}

record Concat(RegEx left, RegEx right) implements RegEx {
    public <R> R accept(RegExVisitor<R> visitor) { return visitor.visit(this); }
}

record Alternation(RegEx left, RegEx right) implements RegEx {
    public <R> R accept(RegExVisitor<R> visitor) { return visitor.visit(this); }
}

record Repetition(RegEx expr, int min, int max) implements RegEx {
    public <R> R accept(RegExVisitor<R> visitor) { return visitor.visit(this); }
}

record Empty() implements RegEx {
    public <R> R accept(RegExVisitor<R> visitor) { return visitor.visit(this); }
}

