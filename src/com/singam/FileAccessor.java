package com.singam;

public interface FileAccessor {

    void loadDataFile(); // called to load the contents of the data file

    void saveDataFile(); // called to save the contents of the data file

    void addHashDetails(String fileName, String newHash);

    void replaceHashDetails(String fileName, String newHash);

    void removeDetails(String fileName);

}