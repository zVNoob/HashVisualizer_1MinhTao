package com.hashvis.collision;

import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.table.Table;

public interface CollisionResolver {
  SymbolTable getAlgorithmSymbolTable(Table table);

  ArrayList<String> resolve();
}
