package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.hashfunc.HashFunctionNumber;
import com.hashvis.hashfunc.HashFunctionString;
import com.hashvis.table.Table;

public class LinearProbing extends OpenAddressing {
  private HashFunction hashFunc;

  public LinearProbing(boolean isKeyString) {
    super(isKeyString);
  }

  @Override
  public ArrayList<HashFunction> getHashFunctionFields() {
    ArrayList<HashFunction> list = new ArrayList<HashFunction>();
    if (isKeyString)
      hashFunc = new HashFunctionString();
    else
      hashFunc = new HashFunctionNumber();
    list.add(hashFunc);
    return list;
  }

  @Override
  public SymbolTable getAlgorithmSymbolTable(Table table, SymbolTable parentTable) {
    SymbolTable symbolTable = super.getAlgorithmSymbolTable(table, parentTable);
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

  public ArrayList<String> getInsertAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("h := hash(key)");
    list.add("current := h");
    list.add("searchDone := 0");
    list.add("bucket = getBucket(current)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? i = getKey(bucket)");
    list.add("searching ? haveKey ? compareKeys(i, key) == 0 ? error()");
    list.add("searching ? (!haveKey) ? available := bucket");
    list.add("searching ? current = (current + 1) % tableSize");
    list.add("searching ? searchDone = current == h");
    list.add("searching ? searchDone ? searching = 0");
    list.add("searching ? loop()");
    list.add("searchDone ? available ?? 0 : error()");
    list.add("available ?? bucket = available");
    list.add("insertKey(bucket, key)");
    list.add("success()");
    return list;
  }

  public ArrayList<String> getSearchAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("h := hash(key)");
    list.add("current := h");
    list.add("bucket = getBucket(current)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? i = getKey(bucket)");
    list.add("searching ? haveKey ? compareKeys(i, key) == 0 ? success()");
    list.add("searching ? current = (current + 1) % tableSize");
    list.add("searching ? current == h ? error()");
    list.add("searching ? loop()");
    list.add("error()");
    return list;
  }

  public ArrayList<String> getDeleteAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("h := hash(key)");
    list.add("current := h");
    list.add("bucket = getBucket(current)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? i = getKey(bucket)");
    list.add("searching ? haveKey ? found = compareKeys(i, key) == 0");
    list.add("searching ? haveKey ? found ? deleteKey(i)");
    list.add("searching ? haveKey ? found ? success()");
    list.add("searching ? current = (current + 1) % tableSize");
    list.add("searching ? current == h ? error()");
    list.add("searching ? loop()");
    list.add("error()");
    return list;
  }
}
