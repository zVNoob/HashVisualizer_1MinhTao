package com.hashvis.codepane.parser;

import java.util.*;

import com.hashvis.codepane.parser.ast.*;
import com.hashvis.codepane.parser.ast.Ast.EvalException;
import com.hashvis.codepane.parser.ast.nonterm.*;
import com.hashvis.codepane.parser.ast.nonterm.haspreexec.*;

/**
 * ParseTree is responsible for converting a stream of tokens from the
 * {@link Lexer}
 * into a hierarchical Abstract Syntax Tree (AST).
 * 
 * It utilizes the Pratt Parsing algorithm to handle operator precedence and
 * associativity,
 * and manages symbol table scoping for variables and lambdas.
 */
public class ParseTree {
  private SymbolTable localSymbolTable = null;
  private Lexer lexer;
  private Ast _tree;

  private static final String endChars = ":,)]}";

  /**
   * Constructs a ParseTree by lexing the provided code and parsing it into an
   * AST.
   * 
   * @param code             The source code string to be parsed.
   * @param localSymbolTable The symbol table to use for this parse tree.
   */
  public ParseTree(String code, SymbolTable localSymbolTable) {
    super();
    // Create a local scope for this specific parse tree that inherits from global
    this.localSymbolTable = localSymbolTable;
    this.lexer = new Lexer(code);
    this.lexer.setSymbolTable(localSymbolTable);
    // Start parsing the main expression with the lowest precedence (0)
    this._tree = parseExpr(0);
    // Error Recovery: If the lexer has remaining tokens, the input was malformed.
    // Instead of crashing, we wrap the valid tree and the remaining tokens into a
    // 'malformed' node.
    if (lexer.peek() != null) {
      // Some sort of multi-token error
      NonTerm malformedTree = new NonTerm(0, code.length(), code);
      malformedTree.pushBack(this._tree);
      this._tree = malformedTree;
      while (lexer.peek() != null) {
        Ast temp = lexer.peek();
        malformedTree.pushBack(parseExpr(0));
        if (temp == lexer.peek()) // Force consumption
          malformedTree.pushBack(lexer.next());
      }
    }
  }

  /** @return The root node of the generated Abstract Syntax Tree. */
  public Ast tree() {
    return this._tree;
  }

  /** @return The symbol table associated with this parse tree. */
  public SymbolTable symbolTable() {
    return localSymbolTable;
  }

  /**
   * Evaluates the AST and returns the result as a BigInteger.
   * 
   * @return The result of the expression evaluation.
   * @throws EvalException if the tree is null or the result is not a BigInteger.
   */
  public Object eval() {
    if (this._tree == null)
      throw new EvalException(this._tree, "Unrecognized expression");
    return this._tree.eval();
  }

  /**
   * Finds all AST nodes that encompass a specific character index (cursor).
   * This is typically used for IDE features like highlighting an expression
   * under the mouse cursor.
   * 
   * @param cursor The character index in the original source code.
   * @return A list of AST nodes from the root down to the leaf that contains the
   *         cursor.
   */
  public List<Ast> getContextAt(int cursor) {
    Ast current = _tree;
    List<Ast> result = new ArrayList<Ast>();
    while (current != null) {
      Ast temp = current;
      while (temp instanceof HasPreExec) {
        temp = ((HasPreExec) temp).preExec();
        if (temp.begin() <= cursor && temp.end() > cursor) {
          current = temp;
          break;
        }
      }
      result.add(current);
      if (!(current instanceof NonTerm))
        break;
      NonTerm parent = (NonTerm) current;
      boolean found = false;
      for (Ast child : parent.children()) {
        if (child.end() > cursor && child.begin() <= cursor) {
          current = child;
          found = true;
          break;
        }
      }
      if (!found)
        break;
    }
    return result;
  }

  // =========================================================================
  // PRATT PARSING LOGIC
  // =========================================================================

  /**
   * The core Pratt parsing function.
   * 
   * @param pred The "binding power" (precedence) of the operator that called this
   *             expression.
   * @return The parsed AST node for this expression.
   */
  private Ast parseExpr(int pred) {
    Ast next = lexer.peek();
    if (next == null)
      return next;
    // End of expression
    if (endChars.indexOf(next.content().charAt(0)) != -1) {
      return null;
    }
    // 1. Handle Prefix (Nud)
    Ast left = parsePrefix(lexer.next());
    // 2. Handle Infix (Led) loop
    while (true) {
      Ast op = lexer.peek();
      if (op == null)
        break;
      if (endChars.indexOf(op.content().charAt(0)) != -1)
        break;
      if (getBindingPower(op) < pred) {
        break;
      }
      lexer.next(); // consume the operator
      left = parseInfix(left, op);
    }

    return left;
  }

  private Ast parsePrefix(Ast token) {
    String content = token.content();

    // Grouping: ( expr )
    if (content.equals("(")) {
      Ast expr = parseExpr(0);
      lexer.next(); // consume ')'
      return expr;
    }

    // Array Literal: [ 1, 2 ]
    if (content.equals("["))
      return parseArrayLiteral();
    // Lambda Literal: {x,y : x + y}
    if (content.equals("{"))
      return parseLambda();

    // Unary Operators (e.g., -5)
    if (token instanceof Op) {
      Ast operand = parseExpr(getBindingPower(token));
      Op unaryOp = (Op) token;
      unaryOp.pushBack(operand);
      return unaryOp;
    }

    return token;

  }

  private Ast parseInfix(Ast left, Ast op) {
    String content = op.content();

    // Function Call: func(arg)
    if (content.equals("(")) {
      return parseCall(left);
    }

    // Indexing: arr[0]
    if (content.equals("[")) {
      return parseIndex(left);
    }

    // Ternary Operator: a ? b : c
    if (content.equals("?")) {
      Op ternaryOp = new Op(op.begin(), op.end(), content);
      Ast middle = parseExpr(getBindingPower(op));
      ternaryOp.pushBack(left);
      ternaryOp.pushBack(middle);
      if (lexer.peek() != null && lexer.peek().content().equals(":")) {
        lexer.next();
        Ast right = parseExpr(getBindingPower(op));
        ternaryOp.pushBack(right);
      }
      return ternaryOp;
    }

    // Binary Operators: a + b
    if (op instanceof Op) {
      Op binaryOp = (Op) op;
      Ast right = parseExpr(getBindingPower(op));
      binaryOp.pushBack(left);
      binaryOp.pushBack(right);
      return binaryOp;
    }

    HasPreExec malformedResult = new HasPreExec(op.begin(), op.end(), content);
    malformedResult.setPreExec(left);
    return malformedResult;
  }

  /**
   * Defines the precedence levels for operators.
   * Higher numbers = tighter binding (calculated first).
   */
  private int getBindingPower(Ast token) {
    if (token.content().equals("("))
      return 7;
    if (token.content().equals("["))
      return 7;
    if (!(token instanceof Op))
      return 0;
    Op op = (Op) token;
    switch (op.content()) {
      case "**":
        return 6; // exponent
      case "*":
      case "/":
      case "%":
        return 5; // multiplication
      case "+":
      case "-":
        return 4; // addition
      case "<":
      case ">":
      case "==":
      case "!=":
      case "<=":
      case ">=":
        return 3; // comparison
      case "&":
      case "|":
      case "^":
        return 2; // bitwise
      default:
        return 1; // lowest
    }
  }
  // =========================================================================
  // HELPER PARSING METHODS
  // =========================================================================

  /**
   * Parses a Lambda expression {args : body}.
   * Creates a new SymbolTable scope for the lambda arguments.
   */
  private Ast parseLambda() {
    SymbolTable prevSymtab = this.lexer.getSymbolTable();
    SymbolTable symtab = new SymbolTable(prevSymtab);
    this.lexer.setSymbolTable(symtab);
    List<Ast> args = parseList(new ArrayList<>(), '}');
    this.lexer.setSymbolTable(prevSymtab);
    int begin;
    int end;
    if (args.size() != 0) {
      begin = args.getFirst().begin() - 1;
      end = args.getLast().end() + 1;
    } else {
      begin = lexer.getPosition() - 1;
      end = lexer.getPosition();
      if ((lexer.peek() != null) && (lexer.peek().content().equals("}")))
        begin--;
    }
    // Comsume the '}'
    lexer.next();
    Lambda lambda = new Lambda(begin, end, lexer.subString(begin, end), symtab);
    for (Ast arg : args)
      lambda.pushBack(arg);
    return lambda;
  }

  /** Parses an array literal [a, b, c]. */
  private Ast parseArrayLiteral() {
    List<Ast> args = parseList(new ArrayList<>(), ']');
    int begin;
    int end;
    if (args.size() != 0) {
      begin = args.getFirst().begin() - 1;
      end = args.getLast().end() + 1;
    } else {
      begin = lexer.getPosition() - 1;
      end = lexer.getPosition();
      if ((lexer.peek() != null) && (lexer.peek().content().equals("]")))
        begin--;
    }
    // Comsume the ']'
    lexer.next();
    Array array = new Array(begin, end, lexer.subString(begin, end));
    for (Ast arg : args)
      array.pushBack(arg);
    return array;
  }

  /** Parses a function call target(args). */
  private Call parseCall(Ast target) {
    List<Ast> args = parseList(new ArrayList<>(), ')');
    int begin;
    int end;
    if (args.size() != 0) {
      begin = args.getFirst().begin() - 1;
      end = args.getLast().end() + 1;
    } else {
      if (target != null)
        begin = target.end();
      else
        begin = lexer.getPosition() - 1;
      end = lexer.getPosition();
    }
    // Comsume the ')'
    lexer.next();
    Call call = new Call(begin, end, lexer.subString(begin, end));
    call.setPreExec(target);
    for (Ast arg : args)
      call.pushBack(arg);
    return call;
  }

  /** Parses an array index access target[index]. */
  private Index parseIndex(Ast target) {
    List<Ast> args = parseList(new ArrayList<>(), ']');
    int begin;
    int end;
    if (args.size() != 0) {
      begin = args.getFirst().begin() - 1;
      end = args.getLast().end() + 1;
    } else {
      if (target != null)
        begin = target.end();
      else
        begin = lexer.getPosition() - 1;
      end = lexer.getPosition();

    }
    // Comsume the ']'
    lexer.next();
    Index idx = new Index(begin, end, lexer.subString(begin, end));
    idx.setPreExec(target);
    for (Ast arg : args)
      idx.pushBack(arg);
    return idx;
  }

  /**
   * Utility to parse a comma-separated or colon-separated list of expressions
   * until a specific character is reached.
   */
  private List<Ast> parseList(List<Ast> list, char endChar) {
    while (true) {
      Ast next = lexer.peek();
      if (next == null)
        break;
      if (next.content().charAt(0) == endChar)
        break;

      if (next.content().equals(",") || next.content().equals(":")) {
        lexer.next();
        continue;
      }
      if (endChars.indexOf(next.content().charAt(0)) != -1) {
        list.add(lexer.next());
        continue;
      }
      Ast temp = parseExpr(0);
      if (temp != null)
        list.add(temp);
    }
    return list;
  }

  public static void main(String[] args) {
    ParseTree tree = new ParseTree("getVar(\"searchDone\") ? !hasVar(\"available\") ? error()",
        new SymbolTable());
    System.out.println(tree.tree());
  }
}
