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
