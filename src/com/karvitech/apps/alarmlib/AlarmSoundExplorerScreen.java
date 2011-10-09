/*
 * AlarmSoundExplorerScreen.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;


import java.util.*;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import com.karvitech.api.appTools.*;
import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import com.karvitech.apps.filelib.*;

/**
 * Main screen to show the listing of all directories/files.
 */
public final class AlarmSoundExplorerScreen extends FileExplorerScreen implements FieldChangeListener
{
    public static final String STR_NO_RADIO_SOFTWARE = "No radio software found, please install one to use this feature.";
    private AlarmItem _alarmItem;
  /*  private UiApplication _uiApp;
    private FileExplorerListFieldImpl _fileList; // file list

    private FileExplorerJournalListener _fileListener;
    private String _parentRoot;
    private String _uri;
    private boolean _canSelectFolder; // can select the whole folder instead of just a file
    private FileExplorerListener _explorerListener;*/

    private AlarmToneListField _toneList; // build in tones 
    private AlarmToneListField _radioSoftwareList; // Radio software, reuse the AlarmToneListField class
        
    private ObjectChoiceField _toneTypeField;
    private String[]          _toneTypes = {"Tones", "Music", "Launch Radio"};    
    private Vector _filteredVec = new Vector();
    private VerticalFieldManager _vfm = new VerticalFieldManager();
    /**
     * Constructor
     */
    public AlarmSoundExplorerScreen(String uri) 
    {
        this(uri, false, "Alarm Sound", null);

    }

    /**
     * Constructor
     */
    public AlarmSoundExplorerScreen(String uri, boolean canSelectFolder, String strTitle, AlarmItem alarmItem) 
    {
        //setTitle("File Selector");
       // super(uri, canSelectFolder, strTitle);
        _alarmItem = alarmItem;
        LabelField title = new LabelField(strTitle, Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        _canSelectFolder = canSelectFolder;
        _uri = uri;
        
        _toneTypeField = new ObjectChoiceField("Alarm Sound Type", _toneTypes);
        int index;
        
        // set the initial type
        if(alarmItem._alarmToneType == AlarmItem.TONE_MUSIC) {
            index = 1;
        }
        else if(alarmItem._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
            index = 2;
        }
        else {
            index = 0;
        }
        _toneTypeField.setSelectedIndex(index);
        _toneTypeField.setChangeListener(this);
        createFileList();
        createToneList();
        createRadioSoftwareList();
        
        // add the tone type selection field
        add(this._toneTypeField);
        add(new SeparatorField());
        // if it is music, then set the file list
        if(_alarmItem._alarmToneType == AlarmItem.TONE_MUSIC) {
            _toneTypeField.setSelectedIndex(String.valueOf(_alarmItem._volume));        
            _vfm.add(_fileList);
        }
        else if(alarmItem._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
            if(_radioSoftwareList != null) {
               _vfm.add(_radioSoftwareList);
            }
            else {
                _vfm.add(new LabelField(STR_NO_RADIO_SOFTWARE));
            }
        }
        else {
            _vfm.add(_toneList);
        }
        this.add(_vfm);
    }   
    
    /**
     * Create the file list to browse the file system for music files
     */
    private void createFileList() {
        _fileList = new FileExplorerListFieldImpl();
        readRoots(null);

        _uiApp = UiApplication.getUiApplication();
        _fileListener = new FileExplorerJournalListener(this);        
        _uiApp.addFileSystemJournalListener(_fileListener);        
    }
    
    /**
     * create the list for the buoild in tones
     */
    private void createToneList() {
        _toneList = new AlarmToneListField(AlarmTuneManager.getInstance().getToneNameList());
        int selectedIndex = AlarmTuneManager.getInstance().getToneNameIndexByFileName(_alarmItem._toneFile);
        if(selectedIndex == -1) {
            selectedIndex = 0;
        }
        _toneList.setSelectedIndex(selectedIndex);
        
    }

    /**
     * create the list for the buoild in tones
     */
    private void createRadioSoftwareList() {
        Vector appDescVec = Util.getApplist();
        if(appDescVec == null) {
            _radioSoftwareList = null;
              return;
        }
        
        _filteredVec.removeAllElements();
        for(int i=0;i< appDescVec.size();i++) {
            ApplicationDescriptor ad = (ApplicationDescriptor)appDescVec.elementAt(i);
            if(ad.getModuleName().toLowerCase().indexOf("radio") >= 0 || ad.getName().toLowerCase().indexOf("radio") >=0) {
            //if(ad.getModuleName().toLowerCase().indexOf("mail") >= 0 || ad.getName().toLowerCase().indexOf("mail") >=0) {
                _filteredVec.addElement(ad);
            }
        }
        // check if there are radio software
        if(_filteredVec.size() <=0 ) {
            _radioSoftwareList = null;
            return;
        }
        
        // reated the name list
        String[] nameList = new String[_filteredVec.size()];
        int selectedIndex = 0;
        for(int i=0;i<_filteredVec.size();i++) {
            ApplicationDescriptor ad = (ApplicationDescriptor)_filteredVec.elementAt(i);
            nameList[i] = ad.getLocalizedName();
            if(nameList[i].toLowerCase().equals(_alarmItem._toneFile.toLowerCase())) {
                selectedIndex = i;
            }
        } 
        _radioSoftwareList = new AlarmToneListField(nameList);
        _radioSoftwareList.setSelectedIndex(selectedIndex);
        
    }
        
    public void setListener(FileExplorerListener listener) {
        _explorerListener = listener;
    }
    
    /**
     * Deletes the selected file or directory.
     */
    private void deleteAction() 
    {
        int index = _fileList.getSelectedIndex();
        FileExplorerFileHolder fileholder = (FileExplorerFileHolder) _fileList.get(_fileList, index);
        
        if (fileholder != null) 
        {
            String filename = fileholder.getPath() + fileholder.getFileName();
            
            if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) 
            {
                FileConnection fc = null;
                
                try 
                {
                    fc = (FileConnection)Connector.open("file:///" + filename);
                    fc.delete();
                    _fileList.remove(index);
                } 
                catch (Exception ex) 
                {
                    Dialog.alert("Unable to delete file or directory: " + filename);
                } 
                finally 
                {
                    try 
                    {
                        if (fc != null) 
                        {
                            fc.close();
                            fc = null;
                        }
                    } 
                    catch (Exception ioex) 
                    {
                    }
                }
            }
        }
    }

    
    /**
     * Overrides default.  Enter key will take action on directory/file.
     * Escape key will go up one directory or close application if at top level.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     * 
     */
    public boolean keyChar(char c, int status, int time) 
    {
        switch (c) 
        {
            case Characters.ENTER:
                return selectAction(false);
                
            case Characters.DELETE:
            
            case Characters.BACKSPACE:
                deleteAction();
                return true;
                
            case Characters.ESCAPE:
                if ( goBack() ) 
                {
                    return true;
                }
                
            default:
                return super.keyChar(c, status, time);
        }
    }

    
    /**
     * Creates the menu to be used in the application.
     * 
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
     
    public void makeMenu(Menu menu, int instance) 
    {
        // Only display the menu if no actions are performed and the focus is not on the tone type field.
        if (instance == Menu.INSTANCE_DEFAULT && !_toneTypeField.isFocus()) 
        {
            menu.add(_selectItem);
            if(this._toneTypeField.getSelectedIndex() == 1 ) {
                // Tone type is music
                FileExplorerFileHolder fileholder = (FileExplorerFileHolder)_fileList.get(_fileList, _fileList.getSelectedIndex());
    
                if (fileholder != null) {
                    if(_canSelectFolder && fileholder.isDirectory()) {
                        menu.add(_selectFolderItem);
                    }
                }
                
                if ( _parentRoot != null ) 
                {
                    menu.add( _backItem );
                }
            }
        }

        super.makeMenu(menu, instance);
    }
    
    
    /**
     * Overrides default implementation.  Performs the select action if the 
     * trackwheel was clicked; otherwise, the default action occurs.
     * 
     * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
     */
    public boolean navigationClick(int status, int time) 
    {
        // select the tone/song/app
        if(!this._toneTypeField.isFocus())
        {
            if(this._toneTypeField.getSelectedIndex() == 1 ) {
                // Tone type is music
                if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) 
                {
                    return selectAction(false);
                }
            }
            else if(this._toneTypeField.getSelectedIndex() == 2) {
                // Tone type is launch app
                if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) 
                {
                    return selectRdioSoftware();
                }
            }
            else {
                // tone type is build in tones
                if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) {
                    return selectTone();
                }
            }
            
        }
        return false;
        //return super.navigationClick(status, time);
    }

    
    /**
     * Reads the path that was passed in and enumerates through it.
     * 
     * @param root Path to be read.
     */
    private void readRoots(String root)
    {
        _parentRoot = root;
        
        // Clear whats in the list.
        _fileList.removeAll();

        FileConnection fc = null;
        Enumeration rootEnum = null;

        if (root != null) 
        {
            // Open the file system and get the list of directories/files.
            try 
            {
                fc = (FileConnection)Connector.open("file:///" + root);
                rootEnum = fc.list();
            } 
            catch (Exception ioex) 
            {
            } 
            finally 
            {
                
                if (fc != null) 
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
            }
        }

        // There was no root to read, so now we are reading the system roots.
        if (rootEnum == null) 
        {
            rootEnum = FileSystemRegistry.listRoots();
        }

        // Read through the list of directories/files.
        while (rootEnum.hasMoreElements()) 
        {
            String file = (String)rootEnum.nextElement();
            
            if (root != null) 
            {
                file = root + file;
            }
            
            readSubroots(file);
        }
    }

    /**
     * Reads all the directories and files from the provided path.
     * 
     * @param file Upper directory to be read.
     */
    private void readSubroots(String file) 
    {
        FileConnection fc = null;
        
        try 
        {
            fc = (FileConnection)Connector.open("file:///" + file);

            // Create a file holder from the FileConnection so that the connection is not left open.
            FileExplorerFileHolder fileholder = new FileExplorerFileHolder(file);
            fileholder.setDirectory(fc.isDirectory());
            _fileList.add(fileholder);
        } 
        catch (Exception ioex) 
        {
        } 
        finally 
        {
            if (fc != null) 
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
        }
    }
    
    /**
     * handle the selection of a build in alarm tone
     */
    private boolean selectTone() {
        int selectedIndex = _toneList.getSelectedIndex();
        _alarmItem._alarmToneType = AlarmItem.TONE_BUILD_IN;
        _alarmItem._toneFile = AlarmTuneManager.getInstance().getFileNameByIndex(selectedIndex);
        _uri = _alarmItem._toneFile;
        this.close();
        //_toneList.get
        return true;
    }
    
    /**
    */
    private boolean selectRdioSoftware() {
        int selectedIndex = _radioSoftwareList.getSelectedIndex();
        _alarmItem._alarmToneType = AlarmItem.TONE_RADIO_SOFTWARE;
        ApplicationDescriptor ad = (ApplicationDescriptor)_filteredVec.elementAt(selectedIndex);
        
        // save the module name and show the localized app name
        _alarmItem._toneFile = ad.getModuleName();
        //_uri = ad.getLocalizedName();
        _uri = ad.getModuleName();
        this.close();
        //_toneList.get
        return true;
    } 
       
    /**
     * Displays information on the selected file.
     * 
     * @return True.
     */
    private boolean selectAction(boolean wholeFolder) 
    {
        if(this._toneTypeField.getSelectedIndex() == 1 ) {
            // Tone type is music
            FileExplorerFileHolder fileholder = (FileExplorerFileHolder)_fileList.get(_fileList, _fileList.getSelectedIndex());
    
            if (fileholder != null) 
            {
                // If it's a directory then show what's in the directory if not select the whole folder.
                if (fileholder.isDirectory() ) 
                {
                    if(!wholeFolder) {
                        readRoots(fileholder.getPath());
                    }
                    else {
                        // return the folder path if selecting the whole folder
                        _uri = fileholder.getPath();
                        _alarmItem._alarmToneType = AlarmItem.TONE_MUSIC;
                        this.close();
                    }
                } 
                else 
                { 
                    // It's a file so display information on it.
                    //_uiApp.pushScreen(new FileExplorerScreenFileInfoPopup(fileholder));
                    _uri = fileholder.getPath() + fileholder.getFileName();
                    _alarmItem._alarmToneType = AlarmItem.TONE_MUSIC;
                    this.close();
                }
            }
        }
        else if(this._toneTypeField.getSelectedIndex() == 2) {
            this.selectRdioSoftware();
        }
        else 
        {
            // select the current tone item in focus
            this.selectTone();
        }
        
        return true;
    }
    
    
    /**
     * Updates the list of files.
     */
    /*package*/ void updateList() 
    {
        synchronized (_uiApp.getAppEventLock()) 
        {
            readRoots(_parentRoot);
        };
    }
    
    
    /**
     * Goes back one directory in the directory hierarchy, if possible.
     * 
     * @return True if went back a directory; false otherwise.
     */
    private boolean goBack() 
    {
        if ( _parentRoot != null ) 
        {
            String backParentRoot = _parentRoot.substring(0, _parentRoot.lastIndexOf('/'));
            backParentRoot = backParentRoot.substring(0, backParentRoot.lastIndexOf('/') + 1);
            
            if (backParentRoot.length() > 0) 
            {
                readRoots(backParentRoot);
            } 
            else 
            {
                readRoots(null);
            }
            
            return true;
        }
        
        return false;
    }

    
    /////////////////////////////////////////////////////////////
    //                    Menu Items                           //
    /////////////////////////////////////////////////////////////
    
    
    /**
     * Menu item for invoking the camera application. This provides a convenient 
     * method of adding a file to the device file system in order to nstrate 
     * the FileSystemJournalListener.
     */
    private MenuItem _cameraItem = new MenuItem("Camera" , 500, 500) 
    {
        public void run() 
        {
            Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA, new CameraArguments());
        }
    };
    
    /**
     * Menu item for deleting the selected file.
     */
    private MenuItem _deleteItem = new MenuItem("Delete" , 500, 500) 
    {
        public void run() 
        {
            deleteAction();
        }
    };
    
    /**
     * Menu item for displaying information on the selected file.
     */
    private MenuItem _selectFolderItem = new MenuItem("Select Folder" , 500, 500) 
    {
        public void run() 
        {
            selectAction(true);
        }
    };
    
    /**
     * Menu item for displaying information on the selected file.
     */
    private MenuItem _selectItem = new MenuItem("Select" , 500, 500) 
    {
        public void run() 
        {
            selectAction(false);
        }
    };
    
    
    /**
     * Menu item for going back one directory in the directory hierarchy.
     */
    private MenuItem _backItem = new MenuItem("Go Back" , 500, 500 ) 
    {
        public void run() 
        {
            goBack();
        }
    };
    
    // implement the FieldChangeListener
    public void fieldChanged(Field field, int context) {   
        if(field == this._toneTypeField) {
            if(_toneTypeField.getSelectedIndex() == 0) {
                // selected the tone type
                _vfm.deleteAll();
                //.delete(this._fileList);
                _vfm.add(this._toneList);
                _toneList.invalidate();
            }
            else if(_toneTypeField.getSelectedIndex() == 2) {
                _vfm.deleteAll();
                if(_radioSoftwareList != null) {
                    _vfm.add(this._radioSoftwareList);
                    _radioSoftwareList.invalidate();
                }
                else { 
                    Dialog.inform(STR_NO_RADIO_SOFTWARE);
                }
            }
            else {
                // selected the Music type
                _vfm.deleteAll();
                //this.delete(this._toneList);
                _vfm.add(this._fileList);            
            }
        }
    }
}
