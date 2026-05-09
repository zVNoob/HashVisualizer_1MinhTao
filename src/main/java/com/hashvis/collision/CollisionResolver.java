package com.hashvis.collision;

import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.table.Table;

public interface CollisionResolver {
  SymbolTable getAlgorithmSymbolTable(Table table, SymbolTable parentTable);

  boolean isSeperateChaining();

  ArrayList<HashFunction> getHashFunctionFields();

  ArrayList<String> getInsertAlgorithm();

  ArrayList<String> getSearchAlgorithm();

  ArrayList<String> getDeleteAlgorithm();
}
