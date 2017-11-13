package com.singam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<Entry> entries = null;

        boolean shouldReplace = false;
        boolean shouldRemove = false;
        boolean onlyMeta = false;

        int algorithm = 0;
        for(int i=0;i<args.length;i++){ //parsing input
            if(args[i].compareTo("meta")==0){
                onlyMeta = true;
            }
            if(args[i].compareTo("replace")==0){
                shouldReplace = true;
            }
            if(args[i].compareTo("remove")==0){
                shouldRemove = true;
            }
            if(args[i].compareTo("md5")==0){
                algorithm = 0;
            }
            if(args[i].compareTo("sha1")==1){
                algorithm = 1;
            }
            if(args[i].compareTo("sha-256")==2){
                algorithm = 2;
            }

        }
        try{
            echo("Reading file/folder :"+args[args.length-1]);
            new FileWrapper(args[args.length-1],algorithm,shouldReplace,shouldRemove,onlyMeta);
            entries = FileWrapper.getEntryList();
            for(Entry item:entries){
                echo(item.fileName+" "+item.hashValue);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            echo("Give file/folder name");
        }



    }

    public static void echo(String msg)
    {
        System.out.println(msg);
    }

}
