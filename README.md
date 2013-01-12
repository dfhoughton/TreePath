TreePath
========

A library which compiles XPath-like expressions into objects that allow
one to perform queries on any tree data structure, obtaining a list of
nodes that match the query expression.

At this point nothing is fixed. The general idea is that this will look as
much as possible like XPath, and function as much as possible in the same
way, while not expecting the trees queried to have any XML-specific properties.

David Houghton
15 April 2012

Syntax
------

In general, TreePath syntax and semantics will be identical to XPath syntax and
semantics with the following exceptions:\*

1. In addition to `/` and `//` there is a `/>` path separator. The latter means "closest". To illustrate, consider the trees

          A      B
         /|\    /|\
        B C D  B C D
        |   |  |   |
        B   B  B   B
   The expression `/B` will return the empty collection for the first and the root node for the second.

   The expression `//B` will return three nodes from the first tree and 4 from the second.

   The expression `/>B` will return from the first tree the `B` node immediately under `A` and that immediately under `D`, skipping the left leaf `B`. From the second tree it will return only the root. Basically, the `/><test>` expression walks the tree from the context node. If it finds a node passing the test, it adds it to the collection and skips all descendants of this node.
2. There is a special syntax for pattern matching on strings: `~<characters>~`. The expression `A` will match nodes with the literal tag "A". `~A~` will match nodes which contain "A" in their tag. The expression between the tildes must compile to a regular expression.
3. Indexing is zero-based rather than 1-based simply because this is the convention in Java itself and in my experience switching between indexing conventions in the same language tends to lead to bugs.
4. `@` expressions are callbacks to functions (methods of the Forester object responsible for interpreting tree paths for the relevant variety of tree) that return some property of the current node and, optionally, a list of arguments. In predicates the return value is converted into a boolean according to theconventions typical for dynamically typed languages: `false`, `null`, `0`, `""`, and empty collections are all false; other values are true. So, for example, one might compose the expression

        //a[@greater(@length, 1)]

    for which one would have to provide the relevant callbacks in the interpreting Forester class.
    
   An attribute name, the identifier after `@`, can be anything. However, any character that violates the rules of Java identifiers\*\* must be escaped. The pattern for attributes is

        /@(?:[\p{L}_$]|\\.)(?:[\p{L}_$\p{N}]|-(?=[\p{L}_\p{N}])|\\.)*+/`

    The possible parameters to one of these "attributes" are strings, path expressions, other attributes, and numerals. Strings are delimited with single or double quotes.

5. No functions are provided for use in predicates. Some `@` expressions will be provided, but for the most part these must be written by the user.

6. There is no `namespace` or `attribute` axis. There are, however, some additional axes:

   * `leaf` : all childless nodes under the context node, potentially including the context node
   * `sibling` : all children of the parent of the context node other than the context node itself
   * `sibling-or-self` : all children of the parent of the context node

7. There are no restrictions on tag names. The actual tag name pattern is

        /(?:[\p{L}_]|\\.)(?:[\p{L}\p{N}_]|\\.)*+/

    so you see that non-word characters must be escaped, as must initial numerals. A node need not have any tag at all, and it may have several. These are implementation details for the relevant Forester class. To match nodes without tags one must use the wildcard character.

8. The logical operators that may be used in a predicate are

   `(` ... `)` : grouping
   `!`  `not`  : not
   `||` `or`   : or
   `&`  `and`  : and
   `^`  `xor`  : exclusive or

   Spaces may optionally occur between operators and operands, but the alphanumeric forms of the operators cannot occur immediately adjacent to forward slashes. This could cause ambiguity were  it allowed: `//*[not/foo]`, for instance, could mean either "any node so long as the root isn't foo" or "any node so long as it is a not node with a foo child". Given that alphanumeric logical operators cannot be adjacent to forward slashes, the first interpretation is ruled out. One must use an expression like `//*[not(/foo)]`, `//*[not /foo]`, or `//*[!/foo]` if one wishes this interpretation. The double pipe is used to prevent ambiguity -- the single pipe can be used in path expressions, which can also be operands in logical expressions in predicates. A sequence of operands joined by the exclusive or operator is true if one and only one of the operands is true. The usual rules of precedence obtain, so `!A || B ^ C & D` is equivalent to `(!A) || (B ^ (C & D))`. Operands in a logical expression must be attributes or paths.

\* This description of the semantics of tree path expressions has rapidly grown out-of-date. Hopefully things will settle down in the near future and I'll document the syntax properly.

\*\* There is one modification to this rule: unescaped hyphens may be used word-medially so long as they are followed by a  regular word character. So `@foo-bar` is acceptable but `@foo--bar` must be written as `@foo\--bar`.

License
-------
This software is distributed under the terms of the FSF Lesser Gnu
Public License (see lgpl.txt).

