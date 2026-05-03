package com.hashvis.codepane.parser.ast.nonterm;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.UserFunction;

/**
 * Lambda represents a lambda expression, repesented as {args,args,... : body}.
 */
public class Lambda extends NonTerm {
  private SymbolTable _scope;

  public Lambda(int begin, int end, String content, SymbolTable scope) {
    super(begin, end, content);
    _scope = scope;

  }

  public SymbolTable scope() {
    return _scope;
  }

  @Override
  public String getNavic() {
    String argList = "{";
    if (children().size() < 2)
      return argList;
    for (int i = 0; i < children().size() - 1; i++) {
      argList += children().get(i).content() + ", ";
    }
    return argList.substring(0, argList.length() - 2) + ":";
  }

  @Override
  public Object eval() {
    return new UserFunction(this);
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (Lambda)" + this.content() + this.childrenToString();
  }
}
