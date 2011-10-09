/*
 * LogHistory.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;
import java.util.*;
import net.rim.device.api.system.*;
/**
 * 
 */
class LogHistory {
    static LogHistory _instance = null;
    private Vector _logs;
    private int _key = -1;
    
    private LogHistory(long key) {
        _logs = (Vector)Configuration.getInstance().getKeyValue(_key);
    }
        
    public static LogHistory getInstance() {        
        return _instance;
    }    

    /**
     * Create an instance with the specified key
     * @param key <description>
     * @return <description>
     */
    public static LogHistory init(int key) {
        if(_instance == null || _instance.getKey() != key) {
            _instance = new LogHistory(key);
        }
        return _instance;
    }
       
    int getKey() {
        return _key;
    }
    
    public Vector getAllEntrys() {        
        return _logs;
    }
        
    /**
     * Add a log entry
     * @param logEntry <description>
     */
    public void addEntry(String logEntry) { 
        _logs.addElement(logEntry);
    }
    
    /**
     * Add a entry for a reason, developers should override this method to handle reasons
     * specific to their application
     * @param reason <description>
     */
    public void addEntry(int reason) {

    }
    
    public void clearHistory() {
        _logs.removeAllElements();
        Configuration.getInstance().setKeyValue(_key, _logs);
        Configuration.getInstance().saveSettings(); // save the settings      
    }   
    
    /**
     * Save logs, this calls the persistence object to be scheduled to save
     */
    public void saveLogs() {
        Configuration.getInstance().setKeyValue(_key, _logs);
        Configuration.getInstance().saveSettings(); // save the logs        
    }
    
    /**
     * Save logs immediately, this force the persistence object to be saved, it is more costly
     */    
    public void saveLogsImediately() {
        Configuration.getInstance().setKeyValue(_key, _logs);
        Configuration.getInstance().forceSaveSettings(); // save the logs immediately
    }    
} 
