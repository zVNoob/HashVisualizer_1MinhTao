package com.hashvis.codepane.parser.ast.nonterm;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;

public class Op extends NonTerm {
  private SymbolTable symtab;

  public Op(int begin, int end, String content, SymbolTable symtab) {
    super(begin, end, content);
    this.symtab = symtab;
  }

  @Override
  public Object eval() {
    if (this.children().size() == 0)
      throw new EvalException(this, "No operand");
    Object lhs = null;
    try {
      lhs = this.children().get(0).eval();
    } catch (EvalException e) {
      if (this.content().equals(":=") || this.content().equals("=")) {
        String name = this.children().get(0).content();
        if (this.children().size() != 2)
          throw new EvalException(this, "Invalid assignment");
        Object rhs = this.children().get(1).eval();
        this.symtab.set(name, rhs);
        return "Assigned " + name + " as " + rhs;

      }
    }
    if (this.content().equals("??")) {
      if (this.children().size() == 1)
        throw new EvalException(this, "Missing operand");
      if (this.children().size() == 2)
        return lhs != null ? this.children().get(1).eval() : null;
      return this.children().get(lhs != null ? 1 : 2).eval();
    }

    if (this.children().size() == 1) {
      if (!(lhs instanceof BigInteger))
        throw new EvalException(this.children().getFirst(), "Operand is not an integer");
      BigInteger ilhs = (BigInteger) lhs;
      switch (this.content()) {
        case "-":
          return ilhs.negate();
        case "+":
          return ilhs;
        case "!":
          return BigInteger.valueOf(ilhs.compareTo(BigInteger.ZERO) == 0 ? 1 : 0);
        default:
          throw new EvalException(this, "Unknown unary operator");
      }
    }
    if (this.content().equals("?")) {
      if (!(lhs instanceof BigInteger))
        throw new EvalException(this.children().getFirst(), "Operand is not an integer");
      BigInteger ilhs = (BigInteger) lhs;
      if (this.children().size() == 2)
        return ilhs.compareTo(BigInteger.ZERO) != 0 ? this.children().get(1).eval() : null;

      return this.children().get(ilhs.compareTo(BigInteger.ZERO) != 0 ? 1 : 2).eval();
    }
    Object rhs = this.children().get(1).eval();

    if (this.content().equals("=") || this.content().equals(":=")) {
      if (this.content().equals(":="))
        return null;
      String name = this.children().get(0).content();
      this.symtab.set(name, rhs);
      return "Assigned " + name + " as " + rhs;
    }

    switch (this.content()) {
      case "+":
        if (lhs instanceof BigInteger && rhs instanceof BigInteger) {
          return ((BigInteger) lhs).add((BigInteger) rhs);
        } else if (lhs instanceof ArrayList && rhs instanceof ArrayList) {
          ArrayList<Object> combined = new ArrayList<>((ArrayList<?>) lhs);
          combined.addAll((ArrayList<?>) rhs);
          return combined;
        }
        throw new EvalException(this, "Operator '+' requires two integers or two lists");

      case "-":
      case "*":
      case "/":
      case "%":
      case "**":
      case "&":
      case "|":
      case "^":
      case "<<":
      case ">>":
        if (!(lhs instanceof BigInteger && rhs instanceof BigInteger))
          throw new EvalException(this, "Operator " + this.content() + " requires two integers");

        BigInteger bLhs = (BigInteger) lhs;
        BigInteger bRhs = (BigInteger) rhs;
        switch (this.content()) {
          case "-":
            return bLhs.subtract(bRhs);
          case "*":
            return bLhs.multiply(bRhs);
          case "/":
            return bLhs.divide(bRhs);
          case "%":
            return bLhs.remainder(bRhs);
          case "**":
            return bLhs.pow(bRhs.intValue());
          case "&":
            return bLhs.and(bRhs);
          case "|":
            return bLhs.or(bRhs);
          case "^":
            return bLhs.xor(bRhs);
          case "<<":
            return bLhs.shiftLeft(bRhs.intValue());
          case ">>":
            return bLhs.shiftRight(bRhs.intValue());
          default:
            throw new EvalException(this, "Unknown binary operator");
        }

      case "==":
      case "!=":
      case "<":
      case ">":
      case "<=":
      case ">=":
        int cmp = compare(lhs, rhs);
        switch (this.content()) {
          case "==":
            return BigInteger.valueOf(cmp == 0 ? 1 : 0);
          case "!=":
            return BigInteger.valueOf(cmp != 0 ? 1 : 0);
          case "<":
            return BigInteger.valueOf(cmp < 0 ? 1 : 0);
          case ">":
            return BigInteger.valueOf(cmp > 0 ? 1 : 0);
          case "<=":
            return BigInteger.valueOf(cmp <= 0 ? 1 : 0);
          case ">=":
            return BigInteger.valueOf(cmp >= 0 ? 1 : 0);
          default:
            throw new EvalException(this, "Unknown comparison operator");
        }

      default:
        throw new EvalException(this, "Unknown binary operator");
    }
  }

  /**
   * Helper to compare two objects.
   * Supports BigInteger and ArrayList (lexicographical comparison).
   */
  private int compare(Object a, Object b) {
    if (a instanceof BigInteger && b instanceof BigInteger) {
      return ((BigInteger) a).compareTo((BigInteger) b);
    } else if (a instanceof ArrayList && b instanceof ArrayList) {
      ArrayList<?> listA = (ArrayList<?>) a;
      ArrayList<?> listB = (ArrayList<?>) b;
      int len = Math.min(listA.size(), listB.size());

      for (int i = 0; i < len; i++) {
        Object e1 = listA.get(i);
        Object e2 = listB.get(i);

        if (e1 instanceof Comparable && e1.getClass().isInstance(e2)) {
          @SuppressWarnings("unchecked")
          int res = ((Comparable<Object>) e1).compareTo(e2);
          if (res != 0)
            return res;
        } else if (e1 == null || e2 == null) {
          if (e1 == e2)
            continue;
          return e1 == null ? -1 : 1;
        } else {
          throw new EvalException(this, "List elements are not comparable or of different types");
        }
      }
      return Integer.compare(listA.size(), listB.size());
    }
    throw new EvalException(this, "Comparison requires both operands to be of the same type (Integer or List)");
  }

  @Override
  public String getNavic() {
    return "";
  }

  @Override
  public String toString() {
    return this._begin + ":" + this._end + " (Op)" + this.content() + " : " + this.childrenToString();
  }
}
