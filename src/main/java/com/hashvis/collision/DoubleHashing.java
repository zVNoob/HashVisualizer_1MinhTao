package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.hashfunc.HashFunctionNumber;
import com.hashvis.hashfunc.HashFunctionString;
import com.hashvis.table.Table;

public class DoubleHashing extends OpenAddressing {
  private boolean isKeyString = false;
  private HashFunction hashFunc1;
  private HashFunction hashFunc2;

  public DoubleHashing(boolean isKeyString) {
    super(isKeyString);
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

  @Override
  public SymbolTable getAlgorithmSymbolTable(Table table, SymbolTable parentTable) {
    SymbolTable symbolTable = super.getAlgorithmSymbolTable(table, parentTable);
    symbolTable.set("hash1", new BuiltinFunction((ar) -> {
      if (!isKeyString)
        return BigInteger.valueOf(hashFunc1.compute(((BigInteger) ar.get(0)).toString(), table.tableSize()));
      else {
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) ar.get(0);
        String keyStr = new String();
        for (int i = 0; i < key.size(); i++) {
          keyStr += (char) key.get(i).intValue();
        }
        return BigInteger.valueOf(hashFunc1.compute(keyStr, table.tableSize()));
      }
    }, ""));
    symbolTable.set("hash2", new BuiltinFunction((ar) -> {
      if (!isKeyString)
        return BigInteger.valueOf(hashFunc2.compute(((BigInteger) ar.get(0)).toString(), table.tableSize()));
      else {
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) ar.get(0);
        String keyStr = new String();
        for (int i = 0; i < key.size(); i++) {
          keyStr += (char) key.get(i).intValue();
        }
        return BigInteger.valueOf(hashFunc2.compute(keyStr, table.tableSize()));
      }
    }, ""));
    return symbolTable;
  }

  public ArrayList<String> getInsertAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("h1 := hash1(key)");
    list.add("h2 := hash2(key)");
    list.add("i := 0");
    list.add("searchDone := 0");
    list.add("bucket = getBucket((h1 + i * h2) % tableSize)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? k = getKey(bucket)");
    list.add("searching ? haveKey ? compareKeys(k, key) == 0 ? error()");
    list.add("searching ? (!haveKey) ? available := bucket");
    list.add("searching ? i = i + 1");
    list.add("searching ? searchDone = (i == tableSize)");
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
    list.add("h1 := hash1(key)");
    list.add("h2 := hash2(key)");
    list.add("i := 0");
    list.add("bucket = getBucket((h1 + i * h2) % tableSize)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? k = getKey(bucket)");
    list.add("searching ? haveKey ? compareKeys(k, key) == 0 ? success()");
    list.add("searching ? i = i + 1");
    list.add("searching ? i == tableSize ? error()");
    list.add("searching ? loop()");
    list.add("error()");
    return list;
  }

  public ArrayList<String> getDeleteAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("h1 := hash1(key)");
    list.add("h2 := hash2(key)");
    list.add("i := 0");
    list.add("bucket = getBucket((h1 + i * h2) % tableSize)");
    list.add("searching = hasOccupied(bucket)");
    list.add("searching ? haveKey = hasKey(bucket)");
    list.add("searching ? haveKey ? k = getKey(bucket)");
    list.add("searching ? haveKey ? found = compareKeys(k, key) == 0");
    list.add("searching ? haveKey ? found ? deleteKey(k)");
    list.add("searching ? haveKey ? found ? success()");
    list.add("searching ? i = i + 1");
    list.add("searching ? i == tableSize ? error()");
    list.add("searching ? loop()");
    list.add("error()");
    return list;
  }
}
