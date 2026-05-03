package com.hashvis.codepane.parser.ast;

import com.hashvis.codepane.parser.SymbolTable;

/**
 * Id represents an identifier in the source code.
 */
public class Id extends Ast {
  private SymbolTable _scope;

  public Id(int begin, int end, String content, SymbolTable symtab) {
    super(begin, end, content);
    this._scope = symtab;
  }

  public SymbolTable scope() {
    return this._scope;
  }

  @Override
  public Object eval() {
    Object temp = this._scope.get(this.content());
    if (temp == null)
      throw new EvalException(this, "Undefined variable");
    return temp;
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (Id)" + this.content() + " = " + this._scope.get(this.content());
  }
}
