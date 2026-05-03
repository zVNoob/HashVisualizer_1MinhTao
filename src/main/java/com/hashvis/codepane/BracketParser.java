package com.hashvis.codepane;

import java.util.Stack;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * BracketParser analyzes a string of code to identify and pair matching
 * brackets.
 * It supports curly braces {}, square brackets [], and parentheses ().
 * 
 * This is primarily used for UI features like "Jump to Matching Bracket" or
 * "Highlight Matching Pair."
 */
public class BracketParser {
  /**
   * Data holder representing a single bracket and its relationship to its pair.
   */
  public static class BracketPosition {
    /** The character of the bracket (e.g., '(', '{', '[') */
    public char value;
    /** The index of the bracket in the original source code string */
    public int position;
    /** Reference to the matching counterpart bracket. Null if no match exists. */
    public BracketPosition counterpart;
  }

  private Map<Integer, BracketPosition> _data = new HashMap<Integer, BracketPosition>();

  /**
   * Constructs a BracketParser and processes the provided code to map all bracket
   * pairs.
   * 
   * @param code The source code string to be analyzed.
   */
  BracketParser(String code) {
    super();
    Stack<BracketPosition> stack = new Stack<BracketPosition>();
    for (int i = 0; i < code.length(); i++) {
      // Handle opening brackets
      if (code.charAt(i) == '{' || code.charAt(i) == '[' || code.charAt(i) == '(') {
        BracketPosition bp = new BracketPosition();
        bp.value = code.charAt(i);
        bp.position = i;
        stack.push(bp);
        _data.put(i, bp);
      }
      // Handle closing brackets
      if (code.charAt(i) == '}' || code.charAt(i) == ']' || code.charAt(i) == ')') {
        boolean foundPair = false;
        if (stack.size() > 0) {
          BracketPosition bp = stack.peek();
          if (bp.value == '{' && code.charAt(i) == '}')
            foundPair = true;
          if (bp.value == '(' && code.charAt(i) == ')')
            foundPair = true;
          if (bp.value == '[' && code.charAt(i) == ']')
            foundPair = true;
        }
        BracketPosition bp2 = new BracketPosition();
        bp2.value = code.charAt(i);
        bp2.position = i;
        if (foundPair) { // Pair found
          BracketPosition bp = stack.pop();
          bp.counterpart = bp2;
          bp2.counterpart = bp;
        }
        _data.put(i, bp2);
      }
    }
  }

  public Map<Integer, BracketPosition> data() {
    return _data;
  }

  /**
   * Retrieves the bracket at the specified cursor position and its counterpart.
   * 
   * @param cursor The character index to check.
   * @return A list containing the bracket at the cursor and its matching
   *         counterpart.
   *         Returns an empty list if no bracket exists at the given index.
   */
  public ArrayList<BracketPosition> get(int cursor) {
    BracketPosition p1 = _data.get(cursor);
    if (p1 == null)
      return new ArrayList<BracketPosition>();

    ArrayList<BracketPosition> result = new ArrayList<BracketPosition>();
    result.add(p1);
    if (p1.counterpart != null)
      result.add(p1.counterpart);
    return result;
  }
}
