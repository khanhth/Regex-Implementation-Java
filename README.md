# Sources: https://share.google/aimode/Oc1cB6U5eKpVtTqZV 

The RegExParser class converts a flat string of regex characters into a hierarchical Abstract Syntax Tree (AST). It uses a technique called Recursive Descent Parsing, which processes tokens from left to right by following specific grammar rules. [1, 2, 3] 
Here is the structural logic and sequence of how this parser operates. [4, 5, 6] 
## Parser Grammar and Precedence
To ensure operators are processed in the correct mathematical order (Precedence), the code divides expressions into four cascading tiers. A higher tier automatically wraps inside a lower tier: [7] 
$$\text{Expression (Alternation: } \vert{} \text{)} \rightarrow \text{Sequence (Concat)} \rightarrow \text{Factor (Repetition: } *, +, ? \text{)} \rightarrow \text{Base (Literal, Parentheses, Empty)}$$ 
------------------------------
## Execution Flow Diagram
The diagram below shows how the methods call each other to parse a complex regex string like a|bc*. It tracks the structural decisions made by the state pointer (pos).

                    ┌─────────────────────────┐
                    │   RegExParser.parse()   │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │    parseExpression()    │◄─────────────────────────┐
                    └────────────┬────────────┘                          │
                                 │                                       │
                                 ▼                                       │ If '|' found,
                    ┌─────────────────────────┐                          │ parses right
                    │     parseSequence()     │◄──────────────┐          │ side recursively
                    └────────────┬────────────┘               │          │
                                 │                            │          │
                                 ▼                            │ Chains   │
                    ┌─────────────────────────┐               │ next     │
                    │      parseFactor()      │               │ factor   │
                    └────────────┬────────────┘               │ via      │
                                 │                            │ Concat   │
                                 ▼                            │          │
                    ┌─────────────────────────┐               │          │
                    │       parseBase()       │               │          │
                    └────────────┬────────────┘               │          │
                                 │                            │          │
        ┌────────────────────────┼────────────────────────┐   │          │
        ▼                        ▼                        ▼   │          │
 ┌──────────────┐         ┌──────────────┐         ┌──────────┴───┐      │
 │  '(' found   │         │ Char Literal │         │ Empty / EOF  │      │
 └──────┬───────┘         └──────┬───────┘         └──────┬───────┘      │
        │                        │                        │              │
        ▼ Recursively            ▼ Returns                ▼ Returns      │
  [Parse Inside]          [Literal Node]            [Empty Node]         │
        │                        │                        │              │
        └────────────────────────┼────────────────────────┘              │
                                 │                                       │
                                 ▼                                       │
                    ┌─────────────────────────┐                          │
                    │   Check for Multiplier  │                          │
                    │      (*, +, or ?)       │                          │
                    └────────────┬────────────┘                          │
                                 │                                       │
                                 ▼                                       │
                    ┌─────────────────────────┐                          │
                    │   Is there more text?   ├──────────────────────────┘
                    └─────────────────────────┘

------------------------------
## Detailed Method Breakdown## 1. parseExpression() (Tier 1: Alternation |)

* Purpose: Handles the lowest precedence operator: the pipe/OR symbol (|).
* How it works: It first calls parseSequence() to grab the left side. If it discovers a | character immediately after, it consumes it, calls parseExpression() recursively to capture everything on the right, and links them with an Alternation node. [8, 9, 10] 

## 2. parseSequence() (Tier 2: Concatenation)

* Purpose: Glues adjacent characters together (e.g., b followed by c in "bc").
* How it works: It calls parseFactor() to read a single unit. It then looks ahead. If the next character is not a structural boundary (like | or )), it assumes another character is coming. It recursively reads the rest of the sequence and returns a Concat tree node linking them. [11] 

## 3. parseFactor() (Tier 3: Repetition *, +, ?) [12] 

* Purpose: Binds multipliers to the item right before them.
* How it works: It requests a core item from parseBase(). Once it has that base item, it checks the next character. If it sees *, +, or ?, it modifies that element by wrapping it inside a Repetition node containing the appropriate min/max bounds.

## 4. parseBase() (Tier 4: Building Blocks)

* Purpose: The foundation method that evaluates what the current text actually represents.
* How it works:
* Groupings: If it encounters an open parenthesis (, it pauses and calls parseExpression() to parse everything inside as an independent sub-tree until it reaches the closing ).
   * Literals: If it reads a standard text character (like a or b), it steps past it and outputs a basic Literal node.
   * Boundaries / Structural Characters: If it sees symbols out of context or reaches the end of the text string, it safely returns an Empty node so the upper tiers do not crash. [13, 14] 

Would you like to explore how to add bracketed character sets (like [a-z]) to this parsing structure, or should we look into adding backslash escape sequences next?

[1] [https://eecs390.github.io](https://eecs390.github.io/project-scheme-parser/)
[2] [https://www.scaler.com](https://www.scaler.com/topics/what-is-parsing-in-nlp/)
[3] [https://www.geeksforgeeks.org](https://www.geeksforgeeks.org/compiler-design/recursive-descent-parser/)
[4] [https://www.scribd.com](https://www.scribd.com/document/574828909/TDP)
[5] [https://ruslandzhafarov.medium.com](https://ruslandzhafarov.medium.com/part-2-how-to-build-your-own-programming-language-language-syntax-0d9089642695)
[6] [https://medium.com](https://medium.com/@karlapudisol/from-sql-to-parse-trees-f1d793691737)
[7] [https://www.ayomideoyekanmi.com](https://www.ayomideoyekanmi.com/posts/explanck-an-expression-evaluator-part-3/)
[8] [https://levelup.gitconnected.com](https://levelup.gitconnected.com/javascript-design-pattern-interpreter-pattern-10-examples-c1795250998d)
[9] [https://journal.stuffwithstuff.com](https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/)
[10] [https://craftinginterpreters.com](https://craftinginterpreters.com/global-variables.html)
[11] [https://levelup.gitconnected.com](https://levelup.gitconnected.com/javascript-design-pattern-interpreter-pattern-10-examples-c1795250998d)
[12] [https://levelup.gitconnected.com](https://levelup.gitconnected.com/javascript-design-pattern-interpreter-pattern-10-examples-c1795250998d)
[13] [https://gamedevacademy.org](https://gamedevacademy.org/what-are-regular-expressions/)
[14] [https://levelup.gitconnected.com](https://levelup.gitconnected.com/javascript-design-pattern-interpreter-pattern-10-examples-c1795250998d)

# Source: https://share.google/aimode/aoZ7O5N1Gssah3tlq

In the context of computer science and compiler design, these terms represent different structural tiers (or levels of nesting) in a grammar. They define the exact rules for operator precedence—meaning they decide which parts of a regular expression stick together tightly, and which parts are separate. [1] 
Think of it like standard math rules (PEMDAS): Expressions are like addition/subtraction, Factors are like multiplication, and Bases are like numbers inside parentheses. [2, 3] 
Here is what each term means specifically for regular expressions, ordered from the largest structural unit down to the smallest element:
------------------------------
## 1. Expression (The Entire Choice / Alternation)
An Expression is the highest-level concept. It represents a complete option, which may include the choice operator (|).

* What it means: "Match this entire sequence, OR match that entire sequence."
* Precedence: It has the lowest priority (it binds loosely). The | splitter cuts the regex into the biggest possible left and right halves. [4] 
* Examples:
* a|b (An expression containing two choices)
   * cat|dog (An expression split down the middle into two choices)

## 2. Sequence (The Chain / Concatenation)
A Sequence is a chain of items appearing right next to each other that must happen in order. [5, 6] 

* What it means: "Match item A, and then match item B, and then match item C."
* Precedence: Medium priority. It binds items tighter than |, but looser than multipliers like *.
* Examples:
* cat (The letter c, followed by a, followed by t)
   * a*b+ (The sequence of an a repetition followed by a b repetition)

## 3. Factor (The Multiplier / Repetition)
A Factor is a single unit combined with its repetition modifier (like *, +, or ?).

* What it means: "Take this single item and repeat it $X$ times."
* Precedence: High priority. Multipliers only apply to the single element immediately to their left, not to the whole chain.
* Examples:
* a* (Match character a zero or more times)
   * (a|b)+ (Match the group (a|b) one or more times)
   * Note on Precedence: In the sequence ab*, only b is the Factor being repeated (b*). The a is a separate item because the factor operator binds tighter than the sequence.

## 4. Base (The Atomic Building Block)
A Base is the smallest independent unit that can stand on its own. It is an atom of text that cannot be split apart by operators. [7] 

* What it means: "Match exactly this character, or match whatever is trapped inside these parentheses." [8] 
* Precedence: Highest priority. Parentheses force everything inside them to be treated as a single, protected Base unit. [9] 
* Examples:
* a (A literal character base)
   * (a|b) (An entire expression wrapped in parentheses, forcing it to behave as a single Base unit)
   * ε (An empty base, representing nothing) [10, 11] 

------------------------------
## Visualizing the Hierarchy
When the parser reads the regex a|bc*, it breaks it down layer by layer using these definitions:

          [Expression]  -->  a | bc*  (Split by the '|' operator)
          /          \
    [Sequence]     [Sequence] -->  bc* (A chain of 'b' and 'c*')
        |           /      \
     [Factor]   [Factor]  [Factor] --> c* (The multiplier locks onto 'c')

        |          |          |
      [Base]     [Base]     [Base] --> 'a', 'b', 'c' (Atomic characters)

        |          |          |
       'a'        'b'        'c'

If you would like, I can show you how to expand this hierarchy to support character classes (like [a-z]) or show how escape sequences (like \d) fit into the definition of a Base.

[1] [https://www.inf.ed.ac.uk](https://www.inf.ed.ac.uk/teaching/courses/inf1/cl/notes/Comp4.pdf)
[2] [https://www.vaia.com](https://www.vaia.com/en-us/textbooks/math/prealgebra-6-edition/chapter-2/problem-103-write-each-of-the-following-statements-in-symbol/)
[3] [https://www.reddit.com](https://www.reddit.com/r/learnmath/comments/1dvonc4/why_are_mathematical_terms_separated_by_and_and/)
[4] [https://www.scribd.com](https://www.scribd.com/document/560711906/unit-i-basic-syntactical-constructs-in-java-1)
[5] [https://unacademy.com](https://unacademy.com/content/jee/study-material/mathematics/a-short-note-on-sequences-and-series/)
[6] [https://www.vaia.com](https://www.vaia.com/en-us/textbooks/math/college-algebra-2015-edition/chapter-9/problem-36-write-a-recursive-formula-for-each-sequence-24124/)
[7] [https://www.reddit.com](https://www.reddit.com/r/learnmath/comments/2o45kh/vector_space_vs_subspace_vs_basis/)
[8] [https://upload.wikimedia.org](https://upload.wikimedia.org/wikipedia/commons/8/8c/SWT-Regexp-introduction.pdf)
[9] [https://congpu.github.io](https://congpu.github.io/course/cs300/cs300_lecture_12.pdf)
[10] [https://www.cs.montana.edu](https://www.cs.montana.edu/webworks/projects/oldjunk/theory/contents/chapter002/section004/green/page002.xhtml)
[11] [https://www.vaia.com](https://www.vaia.com/en-us/textbooks/math/elementary-and-intermediate-algebra-5-edition/chapter-1/problem-11-a-in-the-expression-52-what-is-the-base-b-in-the-/)

