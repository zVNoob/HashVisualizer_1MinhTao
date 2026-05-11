package com.hashvis.collision;

import java.util.ArrayList;

import com.hashvis.hashfunc.HashFunction;
import com.hashvis.table.Table;

public interface CollisionResolver {

  public enum HashAction {
    INSERT, SEARCH, DELETE
  }

  public record CollisionResolverResult(String message, int currentLine) {
  }
  // SymbolTable getAlgorithmSymbolTable(Table table, SymbolTable parentTable);

  boolean isSeperateChaining();

  ArrayList<HashFunction> getHashFunctionFields();

  // ArrayList<String> getInsertAlgorithm();
  //
  // ArrayList<String> getSearchAlgorithm();
  //
  // ArrayList<String> getDeleteAlgorithm();
  ArrayList<String> getAlgorithmAndInitalize(HashAction action, String key, Table table);

  CollisionResolverResult nextStep();

}
