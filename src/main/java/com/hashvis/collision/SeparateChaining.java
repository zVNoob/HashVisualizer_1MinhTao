package com.hashvis.collision;

import java.util.ArrayList;

import com.hashvis.hashfunc.*;
import com.hashvis.table.*;

public class SeparateChaining implements CollisionResolver {
  private boolean isKeyString = false;
  private Table table = null;

  private HashFunction hashFunc = null;

  public SeparateChaining(boolean isKeyString) {
    this.isKeyString = isKeyString;
  }

  public boolean isSeperateChaining() {
    return true;
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
  private Row currentRow = null;
  private Item currentItem = null;
  private HashAction action;

  @Override
  public ArrayList<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
    this.table = table;
    this.key = key;
    this.action = action;
    hashValue = null;
    currentRow = null;
    currentItem = null;
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
    if (currentRow == null) {
      currentRow = table.getRow(hashValue);
      table.scrollTo(currentRow);
      return new CollisionResolverResult("Choosing bucket " + currentRow, 0);
    }
    if (currentItem == null) {
      currentItem = currentRow.nextItem();
      if (currentItem != null) {
        table.scrollTo(currentItem);
        return new CollisionResolverResult("searching items in bucket " + currentRow, 0);
      }
    }
    if (currentItem != null) {
      Item item = currentItem;
      currentItem = null;
      if (item.getText().equals(key)) {
        if (action.equals(HashAction.INSERT))
          return new CollisionResolverResult("Error: duplicate key", -1);
        if (action.equals(HashAction.DELETE)) {
          item.delete();
          return new CollisionResolverResult("Deleted key " + key, -1);
        }
        return new CollisionResolverResult("Found key " + key, -1);
      }
      return new CollisionResolverResult("searching items in bucket " + currentRow, 0);
    }
    if (action.equals(HashAction.INSERT)) {
      currentRow.addItem(key);
      currentRow.getItem(currentRow.getItemsCount() - 1).glow();
      return new CollisionResolverResult("Inserted key " + key, -1);
    }
    return new CollisionResolverResult("Error: key not found", -1);
  }

}
