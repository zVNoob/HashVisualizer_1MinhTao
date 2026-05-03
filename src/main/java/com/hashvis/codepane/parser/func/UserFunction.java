package com.hashvis.codepane.parser.func;

import java.util.List;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.ast.*;
import com.hashvis.codepane.parser.ast.Ast.EvalException;
import com.hashvis.codepane.parser.ast.nonterm.Lambda;

public class UserFunction implements Callable {
  private List<String> args = new java.util.ArrayList<String>();

  private Ast body;

  private SymbolTable scope;

  public UserFunction(Lambda ast) throws EvalException {
    super();
    this.scope = ast.scope();
    for (int i = 0; i < ast.children().size() - 1; i++) {
      Ast child = ast.children().get(i);
      if (!(child instanceof Id)) {
        throw new EvalException(child, "Function arguments must be identifiers");
      }
      this.args.add(((Id) child).content());
      this.scope.set(this.args.get(i), null);
    }
    if (ast.children().size() > 0)
      this.body = ast.children().get(ast.children().size() - 1);
    else
      throw new EvalException(ast, "Function has no body");
  }

  public Object call(List<Object> args) {
    for (int i = 0; i < this.args.size(); i++) {
      if (i >= args.size()) {
        throw new EvalException(body, "Not enough arguments");
      }
      this.scope.set(this.args.get(i), args.get(i));
    }
    return this.body.eval();
  }
}
