package com.hashvis.hashfunc;

import java.math.BigInteger;

import com.hashvis.codepane.CodePane;

abstract public class HashFunction extends CodePane {
  public HashFunction(String code) {
    super(HashSymbolTable.getGlobalSymbolTable(), code, false);
    symbolTable.set("n", BigInteger.ONE);
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
