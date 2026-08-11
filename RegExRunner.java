
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// ==========================================
// 1. ABSTRACT SYNTAX TREE (AST) DEFINITION
// ==========================================
sealed interface RegEx permits Literal, Concat, Alternation, Repetition, Empty, CharClass {

    <R> R accept(RegExVisitor<R> visitor);
}

interface RegExVisitor<R> {

    R visit(Literal node);

    R visit(Concat node);

    R visit(Alternation node);

    R visit(Repetition node);

    R visit(Empty node);

    R visit(CharClass node);
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

// ==========================================
// 2. PARSER IMPLEMENTATION
// ==========================================
class RegExParser {

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
        if (pos < input.length() && peek() != '|' && peek() != ')' && !(left instanceof Empty)) {
            RegEx right = parseSequence();
            return new Concat(left, right);
        }
        return left;
    }

    private RegEx parseFactor() {
        RegEx base = parseBase();
        if (base instanceof Empty) {
            return base;
        }
        if (match('*')) {
            return new Repetition(base, 0, -1);
        }
        if (match('+')) {
            return new Repetition(base, 1, -1);
        }
        if (match('?')) {
            return new Repetition(base, 0, 1);
        }
        return base;
    }

    private RegEx parseBase() {
        if (pos >= input.length()) {
            return new Empty();
        }

        // 1. Handle Escape Sequences (\d, \w, \s, \* etc.)
        if (match('\\')) {
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Dangling backslash escape at end of pattern");
            }
            char escapedChar = peek();
            pos++;

            return switch (escapedChar) {
                case 'd' ->
                    new CharClass(createDigitSet());
                case 'w' ->
                    new CharClass(createWordCharSet());
                default ->
                    new Literal(escapedChar); // Treats specialized or standard punctuation as a literal
            };
        }

        // 2. Handle Parentheses Groups
        if (match('(')) {
            RegEx expr = parseExpression();
            if (!match(')')) {
                throw new IllegalArgumentException("Missing closing parenthesis ')'");
            }
            return expr;
        }

        // 3. Handle Character Classes [a-z0-9]
        if (match('[')) {
            Set<Character> chars = new HashSet<>();
            while (pos < input.length() && peek() != ']') {
                char current = peek();
                pos++;

                // Track ranges like a-z
                if (pos < input.length() && peek() == '-' && pos + 1 < input.length() && input.charAt(pos + 1) != ']') {
                    pos++; // Consume '-'
                    char end = input.charAt(pos);
                    pos++; // Consume range end
                    for (char c = current; c <= end; c++) {
                        chars.add(c);
                    }
                } else {
                    chars.add(current);
                }
            }
            if (!match(']')) {
                throw new IllegalArgumentException("Missing closing bracket ']'");
            }
            return new CharClass(chars);
        }

        // 4. Strict Syntax Error Checking for Dangling Quantifiers
        char c = peek();
        if (c == '*' || c == '+' || c == '?') {
            throw new IllegalArgumentException("Dangling quantifier '" + c + "' has no preceding element to repeat");
        }

        // 5. Normal Structural Boundaries
        if (c == ')' || c == '|' || c == ']') {
            return new Empty();
        }

        // 6. Standard Literal Characters
        pos++;
        return new Literal(c);
    }

    private char peek() {
        return input.charAt(pos);
    }

    private boolean match(char c) {
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private Set<Character> createDigitSet() {
        Set<Character> digits = new HashSet<>();
        for (char c = '0'; c <= '9'; c++) {
            digits.add(c);
        }
        return digits;
    }

    private Set<Character> createWordCharSet() {
        Set<Character> wordChars = new HashSet<>();
        for (char c = 'a'; c <= 'z'; c++) {
            wordChars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            wordChars.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            wordChars.add(c);
        }
        wordChars.add('_');
        return wordChars;
    }
}

// ==========================================
// 3. VISITOR IMPLEMENTATION
// ==========================================
class RegExPrinter implements RegExVisitor<String> {

    public String visit(Literal n) {
        return String.valueOf(n.value());
    }

    public String visit(Concat n) {
        return "(" + n.left().accept(this) + " followed by " + n.right().accept(this) + ")";
    }

    public String visit(Alternation n) {
        return "(" + n.left().accept(this) + " OR " + n.right().accept(this) + ")";
    }

    public String visit(Repetition n) {
        String suf = n.max() == -1 ? (n.min() == 0 ? "*" : "+") : "?";
        return "[" + n.expr().accept(this) + "]" + suf;
    }

    public String visit(Empty n) {
        return "ε";
    }

    public String visit(CharClass n) {
        // Use a TreeSet to print characters in clean, sorted order
        TreeSet<Character> sortedChars = new TreeSet<>(n.characters());
        return "any-of" + sortedChars.toString();
    }
}

// ==========================================
// 4. RUNNER (MAIN EXECUTION)
// ==========================================
public class RegExRunner {

    public static void main(String[] args) {
        // Array of unique patterns to demonstrate execution pathways
        String[] testPatterns = {
            "a|b",
            "\\d+-[a-z]*",
            "(\\*|a)?",
            "*invalid" // This will trigger our syntax error guard!
        };

        RegExPrinter printer = new RegExPrinter();

        for (String regexPattern : testPatterns) {
            System.out.println("----------------------------------------");
            System.out.println("Parsing regex pattern: " + regexPattern);
            try {
                RegEx ast = RegExParser.parse(regexPattern);
                String astStructure = ast.accept(printer);
                System.out.println("Generated AST structure visualization:");
                System.out.println(astStructure);
            } catch (IllegalArgumentException e) {
                System.out.println("Parser Syntax Error: " + e.getMessage());
            }
        }
    }
}
