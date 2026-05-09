package com.hashvis.collision;

import java.math.BigInteger;
import java.util.ArrayList;

import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.codepane.parser.func.BuiltinFunction;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.hashfunc.HashFunctionNumber;
import com.hashvis.hashfunc.HashFunctionString;
import com.hashvis.table.Table;

public class QuadraticProbing extends OpenAddressing {
  private boolean isKeyString = false;
  private HashFunction hashFunc;

  public QuadraticProbing(boolean isKeyString) {
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
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"i\", 0)");
    list.add("setVarIfNotExist(\"searchDone\", 0)");
    list.add("setVar(\"bucket\",getBucket((getVar(\"h\") + getVar(\"i\") ** 2) % tableSize))");
    list.add("setVar(\"searching\", hasOccupied(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? setVar(\"haveKey\", hasKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? setVar(\"k\", getKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? compareKeys(getVar(\"k\"), key) == 0 ? error()");
    list.add("getVar(\"searching\") ? !getVar(\"haveKey\") ? setVarIfNotExist(\"available\", getVar(\"bucket\"))");
    list.add("getVar(\"searching\") ? setVar(\"i\", getVar(\"i\") + 1)");
    list.add("getVar(\"searching\") ? setVar(\"searchDone\", getVar(\"i\") == tableSize)");
    list.add("getVar(\"searching\") ? getVar(\"searchDone\") ? setVar(\"searching\", 0)");
    list.add("getVar(\"searching\") ? loop()");
    list.add("getVar(\"searchDone\") ? !hasVar(\"available\") ? error()");
    list.add("hasVar(\"available\") ? setVar(\"bucket\", getVar(\"available\"))");
    list.add("insertKey(getVar(\"bucket\"), key)");
    list.add("success()");
    return list;
  }

  public ArrayList<String> getSearchAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"i\", 0)");
    list.add("setVar(\"bucket\",getBucket((getVar(\"h\") + getVar(\"i\") ** 2) % tableSize))");
    list.add("setVar(\"searching\", hasOccupied(getVar(\"bucket\")))");

    list.add("getVar(\"searching\") ? setVar(\"haveKey\", hasKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? setVar(\"k\", getKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? compareKeys(getVar(\"k\"), key) == 0 ? success()");

    list.add("getVar(\"searching\") ? setVar(\"i\", getVar(\"i\") + 1)");
    list.add("getVar(\"searching\") ? getVar(\"i\") == tableSize ? error()");
    list.add("getVar(\"searching\") ? loop()");
    list.add("error()");
    return list;
  }

  public ArrayList<String> getDeleteAlgorithm() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("setVarIfNotExist(\"h\",hash(key))");
    list.add("setVarIfNotExist(\"i\", 0)");
    list.add("setVar(\"bucket\",getBucket((getVar(\"h\") + getVar(\"i\") ** 2) % tableSize))");
    list.add("setVar(\"searching\", hasOccupied(getVar(\"bucket\")))");

    list.add("getVar(\"searching\") ? setVar(\"haveKey\", hasKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? setVar(\"k\", getKey(getVar(\"bucket\")))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? setVar(\"found\", compareKeys(getVar(\"k\"), key) == 0)");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? getVar(\"found\") ? deleteKey(getVar(\"k\"))");
    list.add("getVar(\"searching\") ? getVar(\"haveKey\") ? getVar(\"found\") ? success()");

    list.add("getVar(\"searching\") ? setVar(\"i\", getVar(\"i\") + 1)");
    list.add("getVar(\"searching\") ? getVar(\"i\") == tableSize ? error()");
    list.add("getVar(\"searching\") ? loop()");
    list.add("error()");
    return list;
  }
}
