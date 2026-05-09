package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;
import com.hashvis.table.Item;
import com.hashvis.table.Row;
import com.hashvis.table.Table;

public abstract class OpenAddressing implements CollisionResolver {
  protected boolean isKeyString;

  public OpenAddressing(boolean isKeyString) {
    this.isKeyString = isKeyString;
  }

  public SymbolTable getAlgorithmSymbolTable(Table table, SymbolTable parentTable) {
    SymbolTable symbolTable = new SymbolTable(parentTable);
    symbolTable.set("getBucket", new BuiltinFunction((ar) -> {
      BigInteger i = (BigInteger) ar.get(0);
      Row result = table.getRow(i.intValue());
      table.scrollTo(result);
      return result;
    }, ""));
    symbolTable.set("hasOccupied", new BuiltinFunction((ar) -> {
      Row i = (Row) ar.get(0);
      return BigInteger.valueOf(i.maxIndex());
    }, ""));
    symbolTable.set("hasKey", new BuiltinFunction((ar) -> {
      Row i = (Row) ar.get(0);
      if (i.maxIndex() == 0)
        return BigInteger.valueOf(0);
      return i.getItem(0).isGhosted() ? BigInteger.valueOf(0) : BigInteger.valueOf(1);
    }, ""));
    symbolTable.set("getKey", new BuiltinFunction((ar) -> {
      Row i = (Row) ar.get(0);
      i.reset();
      i.choose();
      Item current = i.nextItem();
      if (current == null)
        return null;
      table.scrollTo(current);
      return current;
    }, ""));
    symbolTable.set("compareKeys", new BuiltinFunction((ar) -> {
      String key1Str = ((Item) ar.get(0)).getText();
      String key2Str = new String();
      if (!isKeyString)
        key2Str = ((BigInteger) ar.get(1)).toString();
      else {
        ArrayList<BigInteger> key2 = (ArrayList<BigInteger>) ar.get(1);
        for (int i = 0; i < key2.size(); i++) {
          key2Str += (char) key2.get(i).intValue();
        }
      }
      return BigInteger.valueOf(key1Str.compareTo(key2Str));
    }, ""));
    symbolTable.set("insertKey", new BuiltinFunction((ar) -> {
      Row r = (Row) ar.get(0);
      String keyStr = new String();
      if (!isKeyString)
        keyStr = ((BigInteger) ar.get(1)).toString();
      else {
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) ar.get(1);
        for (int i = 0; i < key.size(); i++) {
          keyStr += (char) key.get(i).intValue();
        }
      }
      if (r.maxIndex() == 1)
        r.getItem(0).delete();
      r.addItem(keyStr);
      r.reset();
      r.choose();
      r.nextItem();
      return "Key " + keyStr + " inserted to " + r;
    }, ""));
    symbolTable.set("deleteKey", new BuiltinFunction((ar) -> {
      Item r = (Item) ar.get(0);
      r.ghost();
      return "Key " + r.getText() + " deleted from " + r;
    }, ""));
    symbolTable.set("tableSize", BigInteger.valueOf(table.tableSize()));

    return symbolTable;
  }

  public boolean isSeperateChaining() {
    return false;
  }
}
