// AST Interfaces & Derivatives Architecture
// ==========================================
// 1. ABSTRACT SYNTAX TREE (AST) DEFINITION
// ==========================================
// See "RegEx.java" file.

// ==========================================
// 2. PARSER IMPLEMENTATION
// ==========================================
// See "RegExParser.java" file.

// ==========================================
// 3. VISITOR IMPLEMENTATION
// ==========================================
// See "RegExPrinter.java" file.

// ==========================================
// 4. RUNNER (MAIN EXECUTION)
// ==========================================
public class RegExRunner {
    public static void test(String pattern, String text) {
        try {
            RegEx ast = RegExParser.parse(pattern);
            boolean result = RegExMatcher.matches(ast, text);
            System.out.printf("Regex: /%-10s/ | Text: %-8s | Match Result: %s\n", pattern, "\"" + text + "\"", result ? "✅ MATCH" : "❌ FAIL");
        } catch (IllegalArgumentException e) {
            System.out.printf("Regex: /%-10s/ | Text: %-8s | Parser Error: %s\n", pattern, "\"" + text + "\"", e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Executing evaluation with dedicated Lexer layer:");
        System.out.println("------------------------------------------------------------------");
        test("[a-z]+-\\d+", "abc-123");  // True
        test("(\\*|a)+", "*a*aa");       // True (Evaluates escaped * correctly)
        test("*fail", "text");           // Triggers Token syntax boundary error
    }
}
