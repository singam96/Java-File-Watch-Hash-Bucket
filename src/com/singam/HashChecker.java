package com.singam;

public interface HashChecker {

    String produceFileHash(String filename);

    String produceDirHash(String path);

    String produceDirMetaHash(String path);

}