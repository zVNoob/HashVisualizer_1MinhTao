package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.hashfunc.HashFunctionNumber;
import com.hashvis.hashfunc.HashFunctionString;
import com.hashvis.table.Item;
import com.hashvis.table.Row;
import com.hashvis.table.Table;

public class SeperateChaining implements CollisionResolver {
  private boolean isKeyString = false;

  private HashFunction hashFunc = null;

  public SeperateChaining(boolean isKeyString) {
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
    symbolTable.set("getNextKey", new BuiltinFunction((ar) -> {
      Row i = (Row) ar.get(0);
      Item current = i.nextItem();
      if (current == null)
        return null;
      table.scrollTo(current);
      return current;
    }, ""));
    symbolTable.set("stillHaveKeys", new BuiltinFunction((ar) -> {
      Row i = (Row) ar.get(0);
      Boolean result = i.currentIndex() < i.maxIndex() - 1;
      System.out.println(i.currentIndex() + " < " + (i.maxIndex() - 1));
      if (i.maxIndex() == 0)
        result = false;
      return BigInteger.valueOf(result ? 1 : 0);
    }, ""));
    symbolTable.set("compareKeys", new BuiltinFunction((ar) -> {
      ArrayList<BigInteger> key2 = (ArrayList<BigInteger>) ar.get(1);
      String key1Str = ((Item) ar.get(0)).getText();
      String key2Str = new String();
      for (int i = 0; i < key2.size(); i++) {
        key2Str += (char) key2.get(i).intValue();
      }
      return BigInteger.valueOf(key1Str.compareTo(key2Str));
    }, ""));
    symbolTable.set("insertKey", new BuiltinFunction((ar) -> {
      Row r = (Row) ar.get(0);
      ArrayList<BigInteger> key = (ArrayList<BigInteger>) ar.get(1);
      String keyStr = new String();
      for (int i = 0; i < key.size(); i++) {
        keyStr += (char) key.get(i).intValue();
      }
      r.addItem(keyStr);
      return "Key " + keyStr + " inserted to " + r;
    }, ""));
    symbolTable.set("deleteKey", new BuiltinFunction((ar) -> {
      Item r = (Item) ar.get(0);
      r.delete();
      return "Key " + r.getText() + " deleted from " + r;
    }, ""));
    symbolTable.set("hash", new BuiltinFunction((ar) -> {
      if (!isKeyString)
        return BigInteger.valueOf(hashFunc.compute(((BigInteger) ar.get(0)).toString(), table.tableSize()));
      else {
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) ar.get(0);
        String keyStr = new String();
        for (int i = 0; i < key.size(); i++) {
          keyStr += (char) key.get(i).intValue();
        }
        return BigInteger.valueOf(hashFunc.compute(keyStr, table.tableSize()));
      }
    }, ""));
    return symbolTable;
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

  public ArrayList<String> getInsertAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"bucket\",getBucket(getVar(\"h\")))");
    list.add("setVar(\"searchingBucket\",stillHaveKeys(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? setVar(\"i\", getNextKey(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? compareKeys(getVar(\"i\"), key) == 0 ? error()");
    list.add("getVar(\"searchingBucket\") ? loop()");
    list.add("insertKey(getVar(\"bucket\"), key)");
    list.add("success()");
    return list;
  }

  public ArrayList<String> getSearchAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"bucket\",getBucket(getVar(\"h\")))");
    list.add("setVar(\"searchingBucket\",stillHaveKeys(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? setVar(\"i\", getNextKey(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? compareKeys(getVar(\"i\"), key) == 0 ? success()");
    list.add("getVar(\"searchingBucket\") ? loop()");
    list.add("error()");
    return list;
  }

  public ArrayList<String> getDeleteAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"bucket\",getBucket(getVar(\"h\")))");
    list.add("setVar(\"searchingBucket\",stillHaveKeys(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? setVar(\"i\", getNextKey(getVar(\"bucket\")))");
    list.add("getVar(\"searchingBucket\") ? setVar(\"keyFound\", compareKeys(getVar(\"i\"), key) == 0)");
    list.add("getVar(\"searchingBucket\") ? getVar(\"keyFound\") ? deleteKey(getVar(\"i\"))");
    list.add("getVar(\"searchingBucket\") ? getVar(\"keyFound\") ? success()");
    list.add("getVar(\"searchingBucket\") ? loop()");
    list.add("error()");
    return list;
  }

}
