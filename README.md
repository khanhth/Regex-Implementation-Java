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

