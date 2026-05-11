package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.hashfunc.*;
import com.hashvis.table.*;

public class DoubleHashing implements CollisionResolver {
  private boolean isKeyString = false;
  private HashFunction hashFunc1;
  private HashFunction hashFunc2;
  private Table table;

  public DoubleHashing(boolean isKeyString) {
    this.isKeyString = isKeyString;
  }

  public boolean isSeperateChaining() {
    return false;
  }

  @Override
  public ArrayList<HashFunction> getHashFunctionFields() {
    ArrayList<HashFunction> list = new ArrayList<HashFunction>();
    if (isKeyString) {
      hashFunc1 = new HashFunctionString();
      hashFunc2 = new HashFunctionString();
    } else {
      hashFunc1 = new HashFunctionNumber();
      hashFunc2 = new HashFunctionNumber();
    }
    list.add(hashFunc1);
    list.add(hashFunc2);
    return list;
  }

  private String key;
  private Integer hashValue1 = null;
  private Integer hashValue2 = null;
  private Integer probeValue = null;
  private Row currentRow = null;
  private Row availableRow = null;
  private HashAction action;

  @Override
  public ArrayList<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    this.table = table;
    this.key = key;
    this.action = action;
    hashValue1 = null;
    hashValue2 = null;
    probeValue = null;
    currentRow = null;
    availableRow = null;
    ArrayList<String> list = new ArrayList<String>();
    list.add("dit me may");
    return list;
  }

  @Override
  public CollisionResolverResult nextStep() {
    if (hashValue1 == null) {
      hashValue1 = hashFunc1.compute(key, table.tableSize());
      return new CollisionResolverResult("hash1 = " + hashValue1, 0);
    }
    if (hashValue2 == null) {
      hashValue2 = hashFunc2.compute(key, table.tableSize());
      return new CollisionResolverResult("hash2 = " + hashValue2, 0);
    }
    if (probeValue == null)
      probeValue = 0;
    if (currentRow == null) {
      currentRow = table.getRow((hashValue1 + (probeValue * hashValue2)) % table.tableSize());
      table.scrollTo(currentRow);
      return new CollisionResolverResult("Choosing bucket " + currentRow, 0);
    }
    Item item = currentRow.nextItem();
    if (item != null) {
      table.scrollTo(item);
      if (item.isGhosted()) {
        probeValue = probeValue + 1;
        if (probeValue == table.tableSize())
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
      probeValue = probeValue + 1;
      if (probeValue == hashValue1)
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
