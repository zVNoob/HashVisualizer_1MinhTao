package com.hashvis.hashfunc;

import java.math.BigInteger;

import com.hashvis.codepane.CodePane;
import com.hashvis.codepane.parser.SymbolTable;

abstract public class HashFunction extends CodePane {
  protected SymbolTable editableSymbolTable;

  public HashFunction(String code) {
    super(new ReadOnlySymbolTable(
        new SymbolTable(
            HashSymbolTable.getGlobalSymbolTable())),
        code, false);
    editableSymbolTable = symbolTable.parent();
    editableSymbolTable.set("n", BigInteger.ONE);
  }

  public boolean isValidHashFunc() {
    return evalException == null;
  }

  @Override
  protected void validateResultType(Object obj) {
    if (!(obj instanceof BigInteger))
      throw new RuntimeException("Not an integer");
  }

  abstract public int compute(String key, int len);
}
