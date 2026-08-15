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
        RegEx ast = RegExParser.parse(pattern);
        boolean result = RegExMatcher.matches(ast, text);
        System.out.printf("Regex: /%-10s/ | Text: %-8s | Match Result: %s\n", pattern, "\"" + text + "\"", result ? "✅ MATCH" : "❌ FAIL");
    }

    public static void main(String[] args) {
        System.out.println("Executing evaluation test paths:");
        System.out.println("--------------------------------------------------");

        test("a|b", "a");         // True
        test("a|b", "c");         // False
        test("a*", "");           // True (Zero repetitions)
        test("[a-z]+-\\d*", "id-99"); // True (Matches character range + digits)
        test("[a-z]+-\\d*", "ID-99"); // False (Uppercase fails bracket range)
        test("hello\\!", "hello!");   // True (Escaped literal verification)
    }
}
