/*
 * FileUtil.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.filelib;
import javax.microedition.io.file.*;
import java.io.*;
import javax.microedition.io.*;
import java.util.*;

/**
 * 
 */
public final class FileUtil {
    private FileUtil() {    }
    
    public static boolean saveTextFile(String fileName, String filePath, String text, boolean overwriteExisting) {
        FileConnection fc = null;
        try {
            fc = (FileConnection)Connector.open("file:///" + filePath + "//" + fileName);      
            if (fc != null) 
            {
                if (!fc.exists())
                {
                    fc.create();  // create the file if it doesn't exist
                }           
                
                DataOutputStream dos = fc.openDataOutputStream();
                dos.writeUTF(text);
                dos.close();
                fc.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception when savingtext file: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Read a text file into a string
     * @param fileName <description>
     * @param filePath <description>
     * @return <description>
     */
    public static String readTextFile(String fileName, String filePath) {
        FileConnection fc = null;
        try {
            fc = (FileConnection)Connector.open("file:///" + filePath + "//" + fileName);      
            if (fc != null) 
            {
                if (!fc.exists())
                {
                   return null;  
                }           
                
                DataInputStream dis = fc.openDataInputStream();
                String text = dis.readUTF();
                dis.close();
                fc.close();
                return text;
            }
        }
        catch (Exception e) {
            System.out.println("Exception when reading text file: " + e.getMessage());
            return null;
        }
        return null;
    }    

    /**
     * Create a play list based on the file/path provided
     * if it is a path then go through it recursively and add all the files in to the list
     * @param file <description>
     */
    public static void createPlayList(String rootFile, Vector playList) {
        Enumeration rootEnum = null;
        FileConnection fc = null;
        try {
            fc = (FileConnection)Connector.open("file:///" + rootFile);      
        }
        catch (Exception e) {
        }
        
        if (fc != null) 
        {
            if(!fc.isDirectory()) {
                //it is a file, add it to list and return
                playList.addElement(rootFile);
                return;
            }
            
            // Open the file system and get the list of directories/files.
            try 
            {
                rootEnum = fc.list();
            } 
            catch (Exception ioex) 
            {
            } 
            finally 
            {                
                // Everything is read, make sure to close the connection.
                try 
                {
                    fc.close();
                    fc = null;
                } 
                catch (Exception ioex) 
                {
                }                
            }            
        } // if(fc!= null)

        // There was no root to read, so now we are reading the system roots.
        if (rootEnum == null) 
        {
            rootEnum = FileSystemRegistry.listRoots();
        }

        // Read through the list of directories/files.
        while (rootEnum.hasMoreElements()) 
        {
            String file = (String)rootEnum.nextElement();
            
            if (rootFile != null) 
            {
                file = rootFile + file;
            }            
            createPlayList(file, playList);
        }        
    }

} 
