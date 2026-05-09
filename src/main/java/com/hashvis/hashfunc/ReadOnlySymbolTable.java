package com.hashvis.hashfunc;

import com.hashvis.codepane.parser.SymbolTable;

public class ReadOnlySymbolTable extends SymbolTable {
  public ReadOnlySymbolTable(SymbolTable parent) {
    super(parent);
  }

  @Override
  public void set(String key, Object value) {
    throw new RuntimeException("Cannot assign value");
  }
}
