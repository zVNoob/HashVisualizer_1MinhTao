package com.hashvis.codepane.parser.ast.nonterm.haspreexec;

import com.hashvis.codepane.parser.ast.Ast;
import com.hashvis.codepane.parser.ast.nonterm.NonTerm;

/**
 * Represents a non-terminal node in the AST that has a prefix.
 *
 */
public class HasPreExec extends NonTerm {
  private Ast _preExec;

  public HasPreExec(int begin, int end, String content) {
    super(begin, end, content);
  }

  public Ast preExec() {
    return this._preExec;
  }

  // Helper function for building Navic of HasPreExec
  public String getNavicPreExec() {
    String preExecString = "";
    if (this._preExec != null)
      if (this._preExec instanceof HasPreExec)
        preExecString = ((HasPreExec) this._preExec).getNavicPreExec();
      else
        preExecString = this._preExec.content();
    return preExecString;
  }

  public void setPreExec(Ast preExec) {
    this._preExec = preExec;
    if (preExec != null) {
      if (preExec().begin() < this._begin)
        this._begin = preExec.begin();
    }
  }

  protected String preExecToString(String classname) {
    String base = (this._begin + ":" + this._end + " (" + classname + ")" + this.content());
    if (this._preExec != null) {
      base += " - ";
      if (children().size() > 0)
        toStringPrefix += "│";
      else
        toStringPrefix += " ";
      toStringPrefix += " ".repeat(base.length() - 1);
      String preExecString = this._preExec.toString();
      toStringPrefix = toStringPrefix.substring(0,
          toStringPrefix.length() -
              base.length() + 1 -
              (((children().size() > 0) ? "│" : " ").length()));
      base += preExecString;
    }
    return base + childrenToString();
  }

  @Override
  public String toString() {
    return preExecToString("HasPreExec");
  }
}
