import java.util.Set;

sealed interface Token permits Token.Literal, Token.CharClass, Token.Operator, Token.EOF {
    record Literal(char value) implements Token {
    }

    record CharClass(Set<Character> characters) implements Token {
    }

    record Operator(Type type) implements Token {
        enum Type {
            LPAREN, RPAREN, LBRACK, RBRACK, PIPE, STAR, PLUS, QUESTION
        }
    }

    record EOF() implements Token {
    }
}
