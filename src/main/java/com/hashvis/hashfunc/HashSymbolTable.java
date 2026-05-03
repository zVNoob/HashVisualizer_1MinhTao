package com.hashvis.hashfunc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.*;

// Global functions for the interpreter
public class HashSymbolTable {
  private HashSymbolTable() {
  };

  private static SymbolTable symbolTable = buildGlobalSymbolTable();

  public static SymbolTable getGlobalSymbolTable() {
    return symbolTable;
  }
  // Built-in functions

  // Sum: take an array of integers and return the sum
  private static Object sum(List<Object> args) {
    BigInteger sum = BigInteger.ZERO;
    if (args.size() == 0)
      throw new RuntimeException("Not enough arguments");
    Object temp = args.get(0);
    if (!(temp instanceof ArrayList))
      throw new RuntimeException("Argument is not a array");
    ArrayList<?> arg = (ArrayList<?>) temp;
    for (int i = 0; i < arg.size(); i++) {
      if (!(arg.get(i) instanceof BigInteger))
        throw new RuntimeException("Element " + i + " is not a integer");
      sum = sum.add((BigInteger) arg.get(i));
    }
    return sum;
  }

  // Map: take an array and a function, apply the function to each element
  private static Object map(List<Object> args) {
    if (args.size() < 2)
      throw new RuntimeException("Not enough arguments");
    Object temp = args.get(0);
    if (!(temp instanceof ArrayList))
      throw new RuntimeException("Argument 1 is not a array");
    ArrayList<?> arg = (ArrayList<?>) temp;
    Object func = args.get(1);
    if (!(func instanceof Callable))
      throw new RuntimeException("Argument 2 is not a function");
    Callable function = (Callable) func;
    ArrayList<Object> result = new ArrayList<Object>();
    for (int i = 0; i < arg.size(); i++) {
      result.add(function.call(List.of(arg.get(i))));
    }
    return result;
  }

  // Len: take an array and return the length
  private static Object len(List<Object> args) {
    if (args.size() == 0)
      throw new RuntimeException("Not enough arguments");
    Object temp = args.get(0);
    if (!(temp instanceof ArrayList))
      throw new RuntimeException("Argument is not a array");
    return BigInteger.valueOf(((ArrayList<?>) temp).size());
  }

  // Range: take start, end, step and return the range array
  private static Object range(List<Object> args) {
    if (args.size() == 0)
      throw new RuntimeException("Not enough arguments");
    BigInteger end = BigInteger.ONE;
    end = (BigInteger) args.get(0);
    ArrayList<Object> result = new ArrayList<Object>();
    for (BigInteger i = BigInteger.ZERO; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
      result.add(i);
    }
    return result;
  }

  private static SymbolTable buildGlobalSymbolTable() {
    SymbolTable symbolTable = new SymbolTable();
    symbolTable.set("sum", new BuiltinFunction(HashSymbolTable::sum, "(a): Sum of a"));
    symbolTable.set("map", new BuiltinFunction(HashSymbolTable::map, "(a, f): Apply f to each element of a"));
    symbolTable.set("len", new BuiltinFunction(HashSymbolTable::len, "(a): Length of a"));
    symbolTable.set("range", new BuiltinFunction(HashSymbolTable::range, "(end): Array of [0..end]"));
    return symbolTable;
  }

}
