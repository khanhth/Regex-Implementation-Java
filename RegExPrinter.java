class RegExPrinter implements RegExVisitor<String> {
    public String visit(Literal n) { return String.valueOf(n.value()); }
    public String visit(Concat n) { return "(" + n.left().accept(this) + n.right().accept(this) + ")"; }
    public String visit(Alternation n) { return "(" + n.left().accept(this) + "|" + n.right().accept(this) + ")"; }
    public String visit(Repetition n) { 
        String suf = n.max() == -1 ? (n.min() == 0 ? "*" : "+") : "?";
        return n.expr().accept(this) + suf; 
    }
    public String visit(Empty n) { return ""; }
}

