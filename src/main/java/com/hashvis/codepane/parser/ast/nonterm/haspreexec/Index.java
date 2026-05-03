package com.hashvis.codepane.parser.ast.nonterm.haspreexec;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Represents a array indexing target[index].
 */
public class Index extends HasPreExec {
  public Index(int begin, int end, String content) {
    super(begin, end, content);
  }

  private int warpIndex(int value, int len) {
    while (value < 0)
      value += len;
    while (value >= len)
      value -= len;
    return value;
  }

  @Override
  public String getNavic() {
    return getNavicPreExec() + "[";
  }

  @Override
  public Object eval() {
    if (this.preExec() == null)
      throw new EvalException(this, "Index has no preExec");
    Object temp = this.preExec().eval();
    if (!(temp instanceof ArrayList))
      throw new EvalException(this.preExec(), "Index preExec is not an ArrayList");
    ArrayList<?> list = (ArrayList<?>) temp;
    if (this.children().size() == 0) {
      throw new EvalException(this, "Index has no index");
    }
    temp = this.children().get(0).eval();
    if (!(temp instanceof BigInteger))
      throw new EvalException(this.children().get(0), "Index is not a integer");
    int base = warpIndex(((BigInteger) temp).intValue(), list.size());
    if (this.children().size() == 1)
      return list.get(base);
    temp = this.children().get(1).eval();
    if (!(temp instanceof BigInteger))
      throw new EvalException(this.children().get(1), "End position is not a integer");
    int end = warpIndex(((BigInteger) temp).intValue(), list.size());
    int step = 1;
    if (this.children().size() >= 3) {
      temp = this.children().get(2).eval();
      if (!(temp instanceof BigInteger))
        throw new EvalException(this.children().get(2), "Index step is not a integer");
      step = ((BigInteger) temp).intValue();
    }
    ArrayList<Object> result = new ArrayList<>();
    if (step > 0)
      for (int i = base; i < Math.min(list.size(), end); i += step) {
        result.add(list.get(i));
      }
    if (step < 0) {
      for (int i = base; i > Math.max(-1, end); i += step) {
        result.add(list.get(i));
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return preExecToString("Index");
  }
}
