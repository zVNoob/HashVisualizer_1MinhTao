package com.hashvis.collision;

import java.util.ArrayList;

import com.hashvis.hashfunc.*;
import com.hashvis.table.*;

public class LinearProbing implements CollisionResolver {
  private HashFunction hashFunc;
  private Table table = null;
  private boolean isKeyString = false;

  public LinearProbing(boolean isKeyString) {
    this.isKeyString = isKeyString;
  }

  public boolean isSeperateChaining() {
    return false;
  }

  public ArrayList<HashFunction> getHashFunctionFields() {
    ArrayList<HashFunction> list = new ArrayList<HashFunction>();
    if (isKeyString)
      hashFunc = new HashFunctionString();
    else
      hashFunc = new HashFunctionNumber();
    list.add(hashFunc);
    return list;
  }

  private String key;
  private Integer hashValue = null;
  private Integer probeValue = null;
  private Row currentRow = null;
  private Row availableRow = null;
  private HashAction action;

  @Override
  public ArrayList<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    this.table = table;
    this.key = key;
    this.action = action;
    hashValue = null;
    probeValue = null;
    currentRow = null;
    availableRow = null;
    ArrayList<String> list = new ArrayList<String>();
    list.add("dit me may");
    return list;
  }

  @Override
  public CollisionResolverResult nextStep() {
    if (hashValue == null) {
      hashValue = hashFunc.compute(key, table.tableSize());
      return new CollisionResolverResult("hash = " + hashValue, 0);
    }
    if (probeValue == null)
      probeValue = hashValue;
    if (currentRow == null) {
      currentRow = table.getRow(probeValue);
      table.scrollTo(currentRow);
      return new CollisionResolverResult("Choosing bucket " + currentRow, 0);
    }
    Item item = currentRow.nextItem();
    if (item != null) {
      table.scrollTo(item);
      if (item.isGhosted()) {
        probeValue = (probeValue + 1) % table.tableSize();
        if (probeValue == hashValue)
          return new CollisionResolverResult("Error: table is full", -1);
        if (availableRow == null) {
          availableRow = currentRow;
          currentRow = null;
          return new CollisionResolverResult("Marked bucket " + availableRow + " as available", 0);
        } else {
          currentRow = null;
          return new CollisionResolverResult("Skipping bucket", 0);
        }
      }
      if (item.getText().equals(key)) {
        if (action.equals(HashAction.INSERT))
          return new CollisionResolverResult("Error: duplicate key", -1);
        if (action.equals(HashAction.DELETE)) {
          item.ghost();
          return new CollisionResolverResult("Deleted key " + key, -1);
        }
        return new CollisionResolverResult("Found key " + key, -1);
      }
      currentRow = null;
      probeValue = (probeValue + 1) % table.tableSize();
      if (probeValue == hashValue)
        return new CollisionResolverResult("Error: table is full", -1);
      return new CollisionResolverResult("searching bucket " + probeValue, 0);
    }
    if (action.equals(HashAction.INSERT)) {
      currentRow.addItem(key);
      currentRow.getItem(currentRow.getItemsCount() - 1).glow();
      return new CollisionResolverResult("Inserted key " + key, -1);
    }
    return new CollisionResolverResult("Error: key not found", -1);
  }

}
