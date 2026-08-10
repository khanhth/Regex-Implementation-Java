public class RegExParser {
    private final String input;
    private int pos = 0;

    public RegExParser(String input) {
        this.input = input;
    }

    public static RegEx parse(String regex) {
        return new RegExParser(regex).parseExpression();
    }

    private RegEx parseExpression() {
        RegEx left = parseSequence();
        if (match('|')) {
            RegEx right = parseExpression();
            return new Alternation(left, right);
        }
        return left;
    }

    private RegEx parseSequence() {
        RegEx left = parseFactor();
        if (pos < input.length() && peek() != '|' && peek() != ')') {
            RegEx right = parseSequence();
            return new Concat(left, right);
        }
        return left;
    }

    private RegEx parseFactor() {
        RegEx base = parseBase();
        if (match('*')) return new Repetition(base, 0, -1);
        if (match('+')) return new Repetition(base, 1, -1);
        if (match('?')) return new Repetition(base, 0, 1);
        return base;
    }

    private RegEx parseBase() {
        if (pos >= input.length()) return new Empty();
        if (match('(')) {
            RegEx expr = parseExpression();
            if (!match(')')) throw new IllegalArgumentException("Missing closing parenthesis");
            return expr;
        }
        char c = peek();
        if (c == ')' || c == '|' || c == '*' || c == '+' || c == '?') return new Empty();
        pos++;
        return new Literal(c);
    }

    private char peek() { return input.charAt(pos); }
    private boolean match(char c) {
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }
}

