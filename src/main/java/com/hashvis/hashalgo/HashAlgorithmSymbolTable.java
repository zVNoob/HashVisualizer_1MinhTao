package com.hashvis.hashalgo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;

// Global functions for the interpreter
public class HashAlgorithmSymbolTable extends SymbolTable {
  private HashMap<String, Object> variables = new HashMap<String, Object>();
  private int instructionCount = -1;

  public HashAlgorithmSymbolTable(HashMap<String, Object> variables) {
    super();
    this.variables = variables;
    prepareEnvironment();
  }

  private void prepareEnvironment() {
    set("loop", new BuiltinFunction(Object -> {
      instructionCount = -1;
      return null;
    }, ""));
    set("error", new BuiltinFunction(Object -> {
      instructionCount = -2;
      return "Failed";
    }, ""));
    set("success", new BuiltinFunction(Object -> {
      instructionCount = -2;
      return "Success";
    }, ""));
    set("setVar", new BuiltinFunction((ar) -> {
      ArrayList<BigInteger> varName = (ArrayList<BigInteger>) ar.get(0);
      String varNameStr = new String();
      for (int i = 0; i < varName.size(); i++) {
        varNameStr += (char) varName.get(i).intValue();
      }
      variables.put(varNameStr, ar.get(1));
      return new String("Assigned variable " + varNameStr + " with value " + ar.get(1));
    }, ""));
    set("setVarIfNotExist", new BuiltinFunction((ar) -> {
      ArrayList<BigInteger> varName = (ArrayList<BigInteger>) ar.get(0);
      String varNameStr = new String();
      for (int i = 0; i < varName.size(); i++) {
        varNameStr += (char) varName.get(i).intValue();
      }
      if (variables.containsKey(varNameStr))
        return "";
      variables.put(varNameStr, ar.get(1));
      return new String("Assigned variable " + varNameStr + " with value " + ar.get(1));
    }, ""));
    set("getVar", new BuiltinFunction((ar) -> {
      ArrayList<BigInteger> varName = (ArrayList<BigInteger>) ar.get(0);
      String varNameStr = new String();
      for (int i = 0; i < varName.size(); i++) {
        varNameStr += (char) varName.get(i).intValue();
      }
      return variables.get(varNameStr);
    }, ""));
    set("hasVar", new BuiltinFunction((ar) -> {
      ArrayList<BigInteger> varName = (ArrayList<BigInteger>) ar.get(0);
      String varNameStr = new String();
      for (int i = 0; i < varName.size(); i++) {
        varNameStr += (char) varName.get(i).intValue();
      }
      return BigInteger.valueOf(variables.containsKey(varNameStr) ? 1 : 0);
    }, ""));
  }

  public int getInstructionCount() {
    return instructionCount;
  }

  public void resetInstructionCount() {
    instructionCount = 0;
  }

  public void incrementInstructionCount() {
    ++instructionCount;
  }
}
