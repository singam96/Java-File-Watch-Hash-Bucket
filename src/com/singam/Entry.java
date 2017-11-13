package com.singam;

public class Entry {

    String fileName;

    String hashValue;

    int algorithmID;

    public Entry(String fileName, String newHash, int algorithmID) {
        this.fileName = fileName;
        this.hashValue = newHash;
        this.algorithmID = algorithmID;
    }

    public String getName(){
        return fileName;
    }
    public String getHash(){
        return hashValue;
    }

    public int getAlgorithm(){
        return algorithmID;
    }
}
