package com.hashvis.codepane.parser.func;

import java.util.List;
import java.util.function.Function;

public class BuiltinFunction implements Callable {
  private Function<List<Object>, Object> func;
  private String _docs;

  public BuiltinFunction(Function<List<Object>, Object> func, String docs) {
    this.func = func;
    this._docs = docs;
  }

  public String docs() {
    return _docs;
  }

  public Object call(List<Object> args) {
    return func.apply(args);
  }
}
