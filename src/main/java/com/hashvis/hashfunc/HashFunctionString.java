package com.hashvis.hashfunc;

import java.math.BigInteger;
import java.util.ArrayList;

public class HashFunctionString extends HashFunction {
  public HashFunctionString() {
    super("sum(map(range(len(s)), {i: s[i]*256**i}))");
    editableSymbolTable.set("s", new ArrayList<BigInteger>());
    validateExpr();
  }

  public int compute(String key, int len) {
    if (!isValidHashFunc())
      return -1;
    editableSymbolTable.set("n", BigInteger.valueOf(len));
    ArrayList<BigInteger> s = new ArrayList<BigInteger>();
    for (char c : key.toCharArray())
      s.add(BigInteger.valueOf(c));
    editableSymbolTable.set("s", s);
    BigInteger hash = (BigInteger) parseTree.eval();
    return hash.mod(BigInteger.valueOf(len)).intValue();
  }
}
