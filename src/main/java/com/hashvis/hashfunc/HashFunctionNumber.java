package com.hashvis.hashfunc;

import java.math.BigInteger;

public class HashFunctionNumber extends HashFunction {
  public HashFunctionNumber() {
    super("k % n");
    editableSymbolTable.set("k", BigInteger.ZERO);
  }

  public int compute(String key, int len) {
    if (!isValidHashFunc())
      return -1;
    editableSymbolTable.set("n", BigInteger.valueOf(len));
    try {
      editableSymbolTable.set("k", new BigInteger(key));
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid number");
    }
    BigInteger hash = (BigInteger) parseTree.eval();
    return hash.mod(BigInteger.valueOf(len)).intValue();
  }
}
