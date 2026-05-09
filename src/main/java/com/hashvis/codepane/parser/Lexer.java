package com.hashvis.codepane.parser;

import com.hashvis.codepane.parser.ast.*;
import com.hashvis.codepane.parser.ast.nonterm.Array;
import com.hashvis.codepane.parser.ast.nonterm.Op;

/**
 * The Lexer class is responsible for performing lexical analysis on a source
 * string.
 * It breaks the input code into a stream of tokens (represented as {@link Ast}
 * nodes),
 * identifying operators, integers, identifiers, and terminal symbols.
 */
public class Lexer {
  private String code;
  private int currentPosition = 0;

  private SymbolTable symbolTable;

  public Lexer(String code) {
    super();
    this.code = code;
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public void setSymbolTable(SymbolTable table) {
    this.symbolTable = table;
  }

  private static final String opsChars = "+*/-%^&|<>=!";
  private static final String multicharOpsChars = "*<>=!";
  private static final String termChars = ":()[]{}?,";

  private Ast currentToken = null;

  /**
   * The core logic of the lexer. Scans the input string to identify the next
   * valid token based on the defined character sets.
   * 
   * @return The next token as an {@link Ast} node, or null if the end of the
   *         input is reached.
   */
  private Ast nextToken() {
    // Skip whitespace
    while (currentPosition < code.length()
        && Character.isWhitespace(code.charAt(currentPosition))) {
      currentPosition++;
    }
    // If eof, return null
    if (currentPosition >= code.length()) {
      return null;
    }
    int begin = currentPosition;
    // Strings
    if (code.charAt(currentPosition) == '"') {
      currentPosition++;
      begin = currentPosition;
      while (currentPosition < code.length()
          && code.charAt(currentPosition) != '"') {
        currentPosition++;
      }
      int end = currentPosition;
      currentPosition++;
      String content = code.substring(begin, end);
      // Convert the string to an array
      Array temp_result = new Array(begin, end, content);
      for (char c : content.toCharArray()) {
        temp_result.pushBack(new Int(begin, end, String.valueOf((int) c)));
      }
      return temp_result;
    }
    // Terminal characters, return directly
    if (termChars.indexOf(code.charAt(currentPosition)) != -1) {
      currentPosition++;
      int end = currentPosition;
      String content = code.substring(begin, end);
      Ast temp_result = new Ast(begin, end, content);
      return temp_result;
    }
    // Check if it is an operator
    boolean isOp = opsChars.indexOf(code.charAt(currentPosition)) != -1;
    if (isOp) {
      // Get only the operator
      currentPosition++;
      // If it is a multicharacter operator
      if (currentPosition < code.length())
        if (multicharOpsChars.indexOf(code.charAt(currentPosition)) != -1) {
          // Check if it is a valid multicharacter operator
          switch (code.charAt(currentPosition - 1)) {
            case '*':
              if (code.charAt(currentPosition) == '*')
                currentPosition++;
              break;
            case '!':
              if (code.charAt(currentPosition) == '=')
                currentPosition++;
              break;
            case '=':
              if (code.charAt(currentPosition) == '=')
                currentPosition++;
              break;
            case '<':
              if (code.charAt(currentPosition) == '=')
                currentPosition++;
              if (code.charAt(currentPosition) == '<')
                currentPosition++;
              break;
            case '>':
              if (code.charAt(currentPosition) == '=')
                currentPosition++;
              if (code.charAt(currentPosition) == '>')
                currentPosition++;
              break;
            default:
              break;
          }
        }
    } else
      // Extend to the end of the token
      while (currentPosition < code.length()) {
        if (opsChars.indexOf(code.charAt(currentPosition)) != -1)
          break;
        if (termChars.indexOf(code.charAt(currentPosition)) != -1)
          break;
        currentPosition++;
      }
    int end = currentPosition;
    // Strip remaining whitespace
    while (end > begin && Character.isWhitespace(code.charAt(end - 1))) {
      end--;
    }
    String content = code.substring(begin, end);
    // Create token
    Ast temp_result = null;
    // Try to cast to apporiate type
    if (isOp)
      temp_result = new Op(begin, end, content);
    else
      try {
        temp_result = new Int(begin, end, content);
      } catch (Exception e) {
        temp_result = new Id(begin, end, content, this.symbolTable);
      }
    return temp_result;
  }

  /**
   * Safely extracts a substring from the source code, used for token content
   * rebuilding
   * 
   * @param begin The starting index.
   * @param end   The ending index.
   * @return The extracted string, clamped to the boundaries of the source code.
   */
  public String subString(int begin, int end) {
    end = Math.min(end, code.length());
    begin = Math.max(begin, 0);
    return code.substring(begin, end);
  }

  /**
   * Returns the next token in the stream without consuming it.
   * This allows the parser to "look ahead" to make branching decisions.
   * 
   * @return The next {@link Ast} token.
   */
  public Ast peek() {
    if (this.currentToken == null) {
      this.currentToken = nextToken();
    }
    return this.currentToken;
  }

  /**
   * Returns the next token and advances the lexer's position.
   * 
   * @return The next {@link Ast} token.
   */
  public Ast next() {
    peek(); // Make sure we have a token
    Ast result = this.currentToken;
    this.currentToken = null; // Consume
    return result;
  }

  /**
   * @return The current character index the lexer has reached in the source code.
   */
  public int getPosition() {
    return this.currentPosition;
  }
}
