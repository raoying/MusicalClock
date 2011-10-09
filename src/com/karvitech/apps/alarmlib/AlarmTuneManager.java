/*
 * AlarmTuneManager.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;

/**
 * 
 */
public class AlarmTuneManager {
    private String[] _displayNames = {"Blue", "Country", "Funk", "Jazz", "Latin", "Reg", "Rock", "Slow Rock"};
    private String[] _fileNames = {"Blue.mp3", "Country.mp3", "Funk.mp3", "Jazz.mp3", "Latin.mp3", "Reg.mp3", "Rock.mp3", "SlowRock.mp3"};
    
    private static AlarmTuneManager _instance;
    
    public static AlarmTuneManager getInstance() {
        if(_instance == null) {
            _instance = new AlarmTuneManager();
        }
        return _instance;
    }
    private AlarmTuneManager() {    }

    public String getDefaultTone() {
        return _fileNames[0];
    }
    public String[] getToneNameList() {
        return _displayNames;
    }
    
    public String[] getToneFileList() {
        return _fileNames;
    }
    
    public String getToneNameByFileName(String fileName) {
        for(int i=0; i< _fileNames.length; i++) {
            if(_fileNames[i].equals(fileName)) {
                return _displayNames[i];
            }
        }
        return null;       
    }
    
    public int getToneNameIndexByFileName(String fileName) {
         for(int i=0; i< _fileNames.length; i++) {
            if(_fileNames[i].equals(fileName)) {
                return i;
            }
        }
        return -1; // nit found       
    }
        
    public String getFileNameByToneName(String toneName) {
        for(int i=0; i< _fileNames.length; i++) {
            if(_fileNames[i].equals(toneName)) {
                return _fileNames[i];
            }
        }
        return null;
    }
    
    public String getFileNameByIndex(int index) {
        if(index >= 0 && index < _fileNames.length) {
            return _fileNames[index];
        }
        else {
            return null;
        } 
    }
}

 
