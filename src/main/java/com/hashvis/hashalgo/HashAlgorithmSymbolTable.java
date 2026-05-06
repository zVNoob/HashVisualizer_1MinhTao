package com.hashvis.hashalgo;

import java.util.HashMap;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.table.Table;

// Global functions for the interpreter
public class HashAlgorithmSymbolTable extends SymbolTable {
  private HashMap<String, Object> variables = new HashMap<String, Object>();
  private Table table;
  private int instructionCount = -1;

  public HashAlgorithmSymbolTable(HashMap<String, Object> variables, Table table) {
    super();
    this.variables = variables;
    this.table = table;
    prepareEnvironment();
  }

  private void prepareEnvironment() {

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
