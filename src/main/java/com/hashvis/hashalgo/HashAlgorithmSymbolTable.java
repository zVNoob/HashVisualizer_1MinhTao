package com.hashvis.hashalgo;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;

// Global functions for the interpreter
public class HashAlgorithmSymbolTable extends SymbolTable {
  private int instructionCount = -1;

  public HashAlgorithmSymbolTable() {
    super();
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
