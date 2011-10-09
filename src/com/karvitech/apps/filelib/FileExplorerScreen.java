/*
 * FileExplorerScreen.java
 *
 */

package com.karvitech.apps.filelib;
import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.MainScreen;


/**
 * Main screen to show the listing of all directories/files.
 */
public  class FileExplorerScreen extends MainScreen
{
    protected UiApplication _uiApp;
    protected FileExplorerListFieldImpl _fileList;
    protected FileExplorerJournalListener _fileListener;
    protected String _parentRoot;
    protected String _uri;
    protected boolean _canSelectFolder; // can select the whole folder instead of just a file
    protected FileExplorerListener _explorerListener;
    
    /**
     * Constructor
     */
    public FileExplorerScreen()
    {
    }
    public FileExplorerScreen(String uri) 
    {
        this(uri, false, "Alarm Sound");
        /*setTitle("File Selector");
        _canSelectFolder = false;
        _uri = uri;
        
        _fileList = new FileExplorerListFieldImpl();
        add(_fileList);
        readRoots(null);

        _uiApp = UiApplication.getUiApplication();
        _fileListener = new FileExplorerJournalListener(this);        
        _uiApp.addFileSystemJournalListener(_fileListener);*/
    }

    /**
     * Constructor
     */
    public FileExplorerScreen(String uri, boolean canSelectFolder, String strTitle) 
    {
        //setTitle("File Selector");
        LabelField title = new LabelField(strTitle, Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        _canSelectFolder = canSelectFolder;
        _uri = uri;
        
        _fileList = new FileExplorerListFieldImpl();
        add(_fileList);
        readRoots(null);

        _uiApp = UiApplication.getUiApplication();
        _fileListener = new FileExplorerJournalListener(this);        
        _uiApp.addFileSystemJournalListener(_fileListener);
    }
    
    public void setListener(FileExplorerListener listener) {
        _explorerListener = listener;
    }
    /**
     * Overrides super. Removes listener before closing the screen.
     * 
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() 
    {
        _uiApp.removeFileSystemJournalListener(_fileListener);
        if(_explorerListener != null) {
            if(!_explorerListener.FileSelected(_uri)) {
                // if failed to set the file selected, then do not close the screen
                return;                    
            }            
        }
        super.close(); 
       
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
     /*
    public void makeMenu(Menu menu, int instance) 
    {
        // Only display the menu if no actions are performed.
        if (instance == Menu.INSTANCE_DEFAULT) 
        {
            menu.add(_selectItem);
            FileExplorerFileHolder fileholder = (FileExplorerFileHolder)_fileList.get(_fileList, _fileList.getSelectedIndex());

            if (fileholder != null) {
                if(_canSelectFolder && fileholder.isDirectory()) {
                    menu.add(_selectFolderItem);
                }
            }
            if (DeviceInfo.hasCamera()) 
            {
                menu.add(_cameraItem);
            }
            
            menu.add(_deleteItem);
            
            if ( _parentRoot != null ) 
            {
                menu.add( _backItem );
            }
        }

        super.makeMenu(menu, instance);
    }
    
    */
    /**
     * Overrides default implementation.  Performs the select action if the 
     * trackwheel was clicked; otherwise, the default action occurs.
     * 
     * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
     */
    public boolean navigationClick(int status, int time) 
    {
        if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) 
        {
            return selectAction(false);
        }
        
        return super.navigationClick(status, time);
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
     * Displays information on the selected file.
     * 
     * @return True.
     */
    private boolean selectAction(boolean wholeFolder) 
    {
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
                    this.close();
                }
            } 
            else 
            { 
                // It's a file so display information on it.
                //_uiApp.pushScreen(new FileExplorerScreenFileInfoPopup(fileholder));
                _uri = fileholder.getPath() + fileholder.getFileName();
                this.close();
            }
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
}
