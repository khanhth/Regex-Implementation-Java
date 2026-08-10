// 1. ABSTRACT SYNTAX TREE (AST) DEFINITION
// ==========================================
sealed interface RegEx permits Literal, Concat, Alternation, Repetition, Empty {
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

// ==========================================
// 2. PARSER IMPLEMENTATION (FIXED RECURSION)
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
        // Added a check: only chain Concat if the left element is NOT Empty 
        // and we aren't at an structural boundary.
        if (pos < input.length() && peek() != '|' && peek() != ')' && !(left instanceof Empty)) {
            RegEx right = parseSequence();
            return new Concat(left, right);
        }
        return left;
    }

    private RegEx parseFactor() {
        RegEx base = parseBase();
        if (base instanceof Empty) return base; // Do not attach multipliers to Empty
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
        // if (c == ')' || c == '|' || c == '*' || c == '+' || c == '?') return new Empty();
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

// ==========================================
// 3. VISITOR IMPLEMENTATION
// ==========================================
class RegExPrinter implements RegExVisitor<String> {
    public String visit(Literal n) { return String.valueOf(n.value()); }
    public String visit(Concat n) { return "(" + n.left().accept(this) + " followed by " + n.right().accept(this) + ")"; }
    public String visit(Alternation n) { return "(" + n.left().accept(this) + " OR " + n.right().accept(this) + ")"; }
    public String visit(Repetition n) { 
        String suf = n.max() == -1 ? (n.min() == 0 ? "*" : "+") : "?";
        return "[" + n.expr().accept(this) + "]" + suf; 
    }
    public String visit(Empty n) { return "ε"; }
}

// ==========================================
// 4. RUNNER (MAIN CLASS)
// ==========================================
public class RegExRunner {
    public static void main(String[] args) {
        String regexPattern = "ca?(xyz)+(a|b)*";
        // String regexPattern = "*a"; 
        System.out.println("**** Parsing regex pattern: " + regexPattern);
        
        try {
            // Build AST via parser
            RegEx ast = RegExParser.parse(regexPattern);
            
            // Print the structural tree using our Visitor
            RegExPrinter printer = new RegExPrinter();
            String astStructure = ast.accept(printer);
            
            System.out.println("Generated AST structure visualization:");
            System.out.println(astStructure);
        } catch (Exception e) {
            System.err.println("Parser Error: " + e.getMessage());
        }
    }
}

