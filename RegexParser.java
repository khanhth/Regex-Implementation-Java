import java.util.List;

class RegExParser {
    private final List<Token> tokens;
    private int tokenIdx = 0;

    public RegExParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static RegEx parse(String regex) {
        List<Token> tokens = new Lexer(regex).tokenize();
        return new RegExParser(tokens).parseExpression();
    }

    private RegEx parseExpression() {
        RegEx left = parseSequence();
        if (match(Token.Operator.Type.PIPE)) {
            return new Alternation(left, parseExpression());
        }
        return left;
    }

    private RegEx parseSequence() {
        RegEx left = parseFactor();
        if (!isAtBoundary() && !(left instanceof Empty)) {
            return new Concat(left, parseSequence());
        }
        return left;
    }

    private RegEx parseFactor() {
        RegEx base = parseBase();
        if (base instanceof Empty)
            return base;
        if (match(Token.Operator.Type.STAR))
            return new Repetition(base, 0, -1);
        if (match(Token.Operator.Type.PLUS))
            return new Repetition(base, 1, -1);
        if (match(Token.Operator.Type.QUESTION))
            return new Repetition(base, 0, 1);
        return base;
    }

    private RegEx parseBase() {
        Token current = peek();

        if (match(Token.Operator.Type.LPAREN)) {
            return parseGroupedExpression();
        }

        if (current instanceof Token.Literal lit) {
            return consumeLiteral(lit);
        }

        if (current instanceof Token.CharClass cc) {
            return consumeCharClass(cc);
        }

        if (isDanglingQuantifier(current)) {
            throw new IllegalArgumentException("Dangling quantifier operator has no preceding element to repeat");
        }

        return new Empty();
    }

    private RegEx parseGroupedExpression() {
        RegEx expr = parseExpression();
        if (!match(Token.Operator.Type.RPAREN)) {
            throw new IllegalArgumentException("Missing closing parenthesis ')'");
        }
        return expr;
    }

    private RegEx consumeLiteral(Token.Literal lit) {
        tokenIdx++; // consume token
        return new Literal(lit.value());
    }

    private RegEx consumeCharClass(Token.CharClass cc) {
        tokenIdx++; // consume token
        return new CharClass(cc.characters());
    }

    private boolean isDanglingQuantifier(Token token) {
        return token instanceof Token.Operator op && (op.type() == Token.Operator.Type.STAR
                || op.type() == Token.Operator.Type.PLUS || op.type() == Token.Operator.Type.QUESTION);
    }

    private Token peek() {
        return tokens.get(tokenIdx);
    }

    private boolean match(Token.Operator.Type type) {
        if (peek() instanceof Token.Operator op && op.type() == type) {
            tokenIdx++;
            return true;
        }
        return false;
    }

    private boolean isAtBoundary() {
        Token t = peek();
        if (t instanceof Token.EOF)
            return true;
        if (t instanceof Token.Operator op) {
            return op.type() == Token.Operator.Type.PIPE || op.type() == Token.Operator.Type.RPAREN;
        }
        return false;
    }
}
