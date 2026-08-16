class RegExMatcher {
    public static boolean matches(RegEx ast, String text) {
        RegEx currentAst = ast;
        for (int i = 0; i < text.length(); i++) {
            currentAst = currentAst.derive(text.charAt(i));
            if (currentAst instanceof Null) {
                return false;
            }
        }
        return currentAst.isNullable();
    }
}