package com.singam;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileWrapper implements FileAccessor,HashChecker {



    public static ArrayList<Entry> entryList;
    MessageDigest md = null;
    int algorithm = 0;
    boolean shouldReplace = false;
    boolean shouldRemove = false;
    boolean onlyMeta = false;
    public FileWrapper(String fileName,int algorithm,boolean shouldReplace,boolean shouldRemove,boolean onlyMeta){
        this.shouldReplace = shouldReplace;
        this.shouldRemove = shouldRemove;
        //this.fileName = fileName;
        this.algorithm = algorithm;
        entryList = new ArrayList<>();
        String alg = "MD5";
        switch(algorithm){
            case 0:
                alg = "MD5";
                break;
            case 1:
                alg = "SHA1";
                break;
            case 2:
                alg = "SHA-256";
                break;
        }

        try {
            md = MessageDigest.getInstance(alg); //init SHA1 algorithm
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(!onlyMeta){
            loadDataFile();

            File file = new File(fileName);
            if(file.isDirectory()){
                produceDirHash(fileName);
            }else{
                readFile(fileName);
                String sha1 = produceFileHash(fileName);
                addHashDetails(fileName,sha1);
            }

            saveDataFile();
        }else{
            System.out.println(produceDirMetaHash(fileName));
        }




    }

    private byte[] readFile(String fileName){
        RandomAccessFile f = null;
        byte[] content = new byte[0];
        try {
            f = new RandomAccessFile(fileName, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            content = new byte[(int)f.length()];//init content size according to the file
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            f.readFully(content); //loaded into memory
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static ArrayList<Entry> getEntryList() {
        return entryList;
    }

    @Override
    public void loadDataFile() { //loads our conf file

        Scanner file = null;
        try {
            file = new Scanner(new File("hashbucket.txt"));
            while(file.hasNextLine()){
                String line = file.nextLine();
                String parts[] = line.split(" ");
                entryList.add(new Entry(parts[0],parts[1],Integer.parseInt(parts[2])));
            }

            file.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config");
        }

    }

    @Override
    public void saveDataFile() { //saves conf file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("hashbucket.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for(Entry item:entryList){
            writer.println(item.fileName+" "+item.hashValue+" "+item.algorithmID);
        }
        writer.close();
    }

    @Override
    public void addHashDetails(String fileName, String newHash) {
        boolean isNew = true;
        // entryList.add()adds that entry into arrayList
        for(int i=0;i<entryList.size();i++){
            if(entryList.get(i).getName().compareTo(fileName) == 0) {
                if (!shouldRemove) {
                if (entryList.get(i).getHash().compareTo(newHash) == 0) {
                    System.out.println(fileName + ": File unmodified, Keeping old");
                    isNew = false;
                } else {
                    System.out.println(fileName + ": File modified ");
                    isNew = false;
                    if (shouldReplace) {
                        System.out.println("Replacing old");
                        replaceHashDetails(fileName, newHash);
                    }

                }
            }else{
                    System.out.println(fileName +" : Removing from bucket");
                    removeDetails(fileName);
                    isNew= false;
                }
            }
        }
        if(isNew){
            System.out.println(fileName+": New File adding to the bucket");
            entryList.add(new Entry(fileName,newHash,this.algorithm));
        }
    }

    @Override
    public void replaceHashDetails(String fileName, String newHash) {
        removeDetails(fileName);
        String sha1 = produceFileHash(fileName);
        addHashDetails(fileName,sha1);
    }

    @Override
    public void removeDetails(String fileName) {
        for(int i=0;i<entryList.size();i++){
            if(entryList.get(i).getName().compareTo(fileName) == 0){
                entryList.remove(i);
            }
        }
    }

    @Override
    public String produceFileHash(String filename) {
        byte[] content = readFile(filename);
        String sha1 = Base64.getEncoder().encodeToString(md.digest(content));
        return sha1;
    }

    @Override
    public String produceDirHash(String path) {
        File file = new File(path);
        String hash = "";
        String fileName;
        File[] listOfFiles = file.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("Reading File " + listOfFiles[i].getName());
                fileName = file.getName()+"/"+listOfFiles[i].getName();
               // loadDataFile();
                String sha1 = produceFileHash(file.getName()+"/"+listOfFiles[i].getName());
                hash = hash + sha1;
               // addHashDetails(listOfFiles[i].getName(),sha1);
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        addHashDetails(path, Base64.getEncoder().encodeToString(md.digest(hash.getBytes())));

        return new String();
    }

    @Override
    public String produceDirMetaHash(String name) {

        Path path = Paths.get(name).toAbsolutePath();

        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        String attributes = "";
        try {
            for(String item:fileAttributeView.list()){
                attributes = attributes+item;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(md.digest(attributes.getBytes()));
    }
}
