import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Lexer {
    private final String source;
    private int cursor = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (cursor < source.length()) {
            char c = advance();
            switch (c) {
                case '(' -> tokens.add(new Token.Operator(Token.Operator.Type.LPAREN));
                case ')' -> tokens.add(new Token.Operator(Token.Operator.Type.RPAREN));
                case '|' -> tokens.add(new Token.Operator(Token.Operator.Type.PIPE));
                case '*' -> tokens.add(new Token.Operator(Token.Operator.Type.STAR));
                case '+' -> tokens.add(new Token.Operator(Token.Operator.Type.PLUS));
                case '?' -> tokens.add(new Token.Operator(Token.Operator.Type.QUESTION));
                case '\\' -> tokens.add(handleEscape());
                case '[' -> tokens.add(handleBracketClass());
                default -> tokens.add(new Token.Literal(c));
            }
        }
        tokens.add(new Token.EOF());
        return tokens;
    }

    private char advance() {
        return source.charAt(cursor++);
    }

    private char peek() {
        return source.charAt(cursor);
    }

    private boolean hasNext() {
        return cursor < source.length();
    }

    private Token handleEscape() {
        if (!hasNext())
            throw new IllegalArgumentException("Dangling backslash escape at end of pattern");
        char escaped = advance();
        return switch (escaped) {
            case 'd' -> new Token.CharClass(createDigitSet());
            case 'w' -> new Token.CharClass(createWordCharSet());
            default -> new Token.Literal(escaped); // \* becomes a literal character '*'
        };
    }

    private Token handleBracketClass() {
        Set<Character> chars = new HashSet<>();
        while (hasNext() && peek() != ']') {
            char current = advance();
            // Look ahead for a range syntax like 'a-z'
            if (hasNext() && peek() == '-' && cursor + 1 < source.length() && source.charAt(cursor + 1) != ']') {
                advance(); // consume '-'
                char end = advance();
                for (char c = current; c <= end; c++)
                    chars.add(c);
            } else {
                chars.add(current);
            }
        }
        if (!hasNext() || advance() != ']')
            throw new IllegalArgumentException("Missing closing bracket ']'");
        return new Token.CharClass(chars);
    }

    private Set<Character> createDigitSet() {
        Set<Character> digits = new HashSet<>();
        for (char c = '0'; c <= '9'; c++)
            digits.add(c);
        return digits;
    }

    private Set<Character> createWordCharSet() {
        Set<Character> wordChars = new HashSet<>();
        for (char c = 'a'; c <= 'z'; c++)
            wordChars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            wordChars.add(c);
        for (char c = '0'; c <= '9'; c++)
            wordChars.add(c);
        wordChars.add('_');
        return wordChars;
    }
}
