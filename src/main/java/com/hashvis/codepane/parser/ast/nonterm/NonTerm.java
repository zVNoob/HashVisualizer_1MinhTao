package com.hashvis.codepane.parser.ast.nonterm;

import java.util.LinkedList;
import java.util.List;

import com.hashvis.codepane.parser.ast.Ast;

/**
 * Represents a non-terminal node in the AST.
 */
public class NonTerm extends Ast {
  private List<Ast> _children = new LinkedList<Ast>();

  public NonTerm(int begin, int end, String content) {
    super(begin, end, content);
  }

  public List<Ast> children() {
    return this._children;
  }

  public void pushBack(Ast child) {
    if (child == null) {
      return;
    }
    this._children.add(child);
    if (child.end() > this._end) {
      this._end = child.end();
    }
    if (child.begin() < this._begin) {
      this._begin = child.begin();
    }
  }

  public Ast popFront() {
    if (this._children.size() == 0) {
      return null;
    }
    Ast result = this._children.remove(0);
    if (this._children.size() == 0) {
      this._begin = this._end - 1;
    } else if (result.begin() < this._children.get(0).begin()) {
      this._begin = this._children.get(0).begin();
    }
    return result;
  }

  public String getNavic() {
    return "";
  }

  protected static String toStringPrefix = "";

  protected String childrenToString() {
    String result = "";
    String prefix = toStringPrefix + "├─";
    toStringPrefix += "│ ";
    for (int i = 0; i < _children.size(); i++) {
      if (i == _children.size() - 1) {
        prefix = prefix.substring(0, prefix.length() - "├─".length());
        prefix += "└─";
        toStringPrefix = toStringPrefix.substring(0, toStringPrefix.length() - "│ ".length());
        toStringPrefix += "  ";
      }
      result += "\n" + prefix + children().get(i).toString();
    }
    toStringPrefix = toStringPrefix.substring(0, toStringPrefix.length() - "│ ".length());
    return result;
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (NonTerm)" + this.content() + this.childrenToString();
  }
}
