package com.hashvis.codepane.parser;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
  private Map<String, Object> symbolTable = new HashMap<String, Object>();
  private SymbolTable parent;

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable parent) {
    super();
    this.parent = parent;
  }

  // Set the value of key in current symbol table
  public void set(String key, Object value) {
    this.symbolTable.put(key, value);
  }

  // Return the value of key
  public Object get(String key) {
    SymbolTable current = this;
    while (current != null) {
      if (current.symbolTable.containsKey(key)) {
        return current.symbolTable.get(key);
      }
      current = current.parent;
    }
    return null;
  }

  // Return all entry that start with key
  public Map<String, Object> query(String key) {
    HashMap<String, Object> result = new HashMap<String, Object>();
    SymbolTable current = this;
    while (current != null) {
      for (Map.Entry<String, Object> entry : current.symbolTable.entrySet()) {
        if (entry.getKey().startsWith(key)) {
          result.put(entry.getKey(), entry.getValue());
        }
      }
      current = current.parent;
    }
    return result;
  }
}
