package com.hashvis.codepane.parser.ast;

import java.math.BigInteger;

/**
 * Int represents an integer literal in the source code.
 */
public class Int extends Ast {
  private BigInteger _value;

  public Int(int begin, int end, String content) {
    super(begin, end, content);
    int radix = 10;
    int multiplier = 0;
    String actualContent = content;
    if (content.startsWith("0x")) {
      radix = 16;
      actualContent = content.substring(2);
    } else if (content.startsWith("0b")) {
      radix = 2;
      actualContent = content.substring(2);
    } else if (content.startsWith("0o")) {
      radix = 8;
      actualContent = content.substring(2);
    } else if (content.contains("e")) {
      int e_index = content.indexOf("e");
      multiplier = Integer.parseInt(content.substring(e_index + 1));
      actualContent = content.substring(0, e_index);
    }
    this._value = new BigInteger(actualContent, radix).multiply(new BigInteger("10").pow(multiplier));
  }

  @Override
  public Object eval() {
    return this._value;
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (Int)" + this.eval();
  }
}
