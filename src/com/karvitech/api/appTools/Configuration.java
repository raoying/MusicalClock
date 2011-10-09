//#preprocess
package com.karvitech.api.appTools;


/*
 * Configuration.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */


import java.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.Persistable;
/**
 * 
 */
public class Configuration {    

    private Hashtable _hashData;
    private PersistentObject _store;
    private Vector _listenerList = new Vector();
    
    private static long _uid = -1;
    private static Configuration _instance;
    
    public static Configuration getInstance() {
        if(_instance == null ) {
            // if the init has not been called then it does not create
            // the instance.
            if(_uid == -1) {
                return null;
            }           
            _instance = new Configuration();
        }
        return _instance;
    }
    
    // 
    public static void init(long uid) {
        // if instance already exists, then this method does nothing 
        // it is supposed to be called only once before the instance is created
        if(_instance == null) {
            _uid = uid;
        }
    }
    
    public static boolean isFreeVersion() {
//#ifdef FREE_VERSION
        return true;
//#else 
        return false;
//#endif
    }
    private Configuration() {
        _store = PersistentStore.getPersistentObject( _uid ); 
        _hashData = (Hashtable)_store.getContents();
        synchronized (_store) {
            if (_hashData == null) {
                _hashData = initTable();
                _store.setContents(_hashData);
                _store.commit();
            }
        }
    }
    
    public Hashtable initTable() {
        Hashtable hasTbl = new Hashtable();
        return hasTbl;
    }
    
    public Object getKeyValue(int key) {
        return _hashData.get(new Integer(key));
    }
    
    public void setKeyValue(int key, Object value) {
        _hashData.put(new Integer(key), value);
    }
    
    public void saveSettings() {
        try {
            commit(false);
            notifyListeners();
        }
        catch(Exception e) {
            System.out.println("Saving settings failed");
        }
    }

    /**
     * Save the setttings immediately 
     */
    public void forceSaveSettings() {
        commit(true);
        notifyListeners();
    }
    
    private void commit(boolean forced)
    {
        synchronized (_store) {
            _store.setContents(_hashData);
            if(forced) {
                _store.forceCommit();
            }
            else {
                _store.commit();
            }
        }
    }
        
    public void addListener(ConfigurationListener listener) {
        _listenerList.addElement(listener);
    }
    
    public void removeListener(ConfigurationListener listener) {
        _listenerList.removeElement(listener);
    }

    public void removeAllListener() {
        _listenerList.removeAllElements();
    }    
    private void notifyListeners() {
        for(int i=0;i<_listenerList.size();i++) {
            ((ConfigurationListener)_listenerList.elementAt(i)).configurationChanged();
        }
    }
} 

