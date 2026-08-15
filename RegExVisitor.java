interface RegExVisitor<R> {

    R visit(Literal node);

    R visit(Concat node);

    R visit(Alternation node);

    R visit(Repetition node);

    R visit(Empty node);

    R visit(CharClass node);

    R visit(Null node);
}
