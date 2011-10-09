/*
 * FileExplorerJournalListener.java
 *
 */

package com.karvitech.apps.filelib;

import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * Listener to determine when files have been added to the file system.
 */
public final class FileExplorerJournalListener implements FileSystemJournalListener
{
    private FileExplorerScreen _screen;
    private long _lastUSN; // = 0;
    
    
    /**
     * Constructor.
     * 
     * @param screen The screen to update when events occur.
     */
    public FileExplorerJournalListener(FileExplorerScreen screen) 
    {
        _screen = screen;
    }

    
    /**
     * Notified when FileSystem event occurs.
     */
    public void fileJournalChanged() 
    {
        long nextUSN = FileSystemJournal.getNextUSN();
        String msg = null;
        
        for (long lookUSN = nextUSN - 1; lookUSN >= _lastUSN && msg == null; --lookUSN) 
        {
            FileSystemJournalEntry entry = FileSystemJournal.getEntry(lookUSN);
            
            // We didn't find an entry.
            if (entry == null) 
            { 
                break;
            }

            // Check if this entry was added or deleted.
            String path = entry.getPath();
            
            if (path != null) 
            {
                switch (entry.getEvent()) 
                {
                    case FileSystemJournalEntry.FILE_ADDED:
                        msg = "File was added.";
                        break;
                        
                    case FileSystemJournalEntry.FILE_DELETED:
                        msg = "File was deleted.";
                        break;
                }
            }
        }
        
        // _lastUSN must be updated before calling showMessage() because that method
        // pushes a modal screen onto the display stack, which blocks this thread.
        // If the modal screen's thread then processes a file journal event on this
        // application's behalf, the for loop above can end up processing the same
        // event that we are blocking on.  Updating _lastUSN before blocking prevents
        // the same file journal event from being processed twice, and thus prevents
        // the same dialog from being displayed twice.
        _lastUSN = nextUSN;
        
        if ( msg != null ) 
        {
            showMessage(msg);
            _screen.updateList();
        }
    }
    
    
    /**
     * Displays the provided message in a dialog box.
     * 
     * @param msg The message to display.
     */
    private void showMessage(String msg) 
    {
        synchronized (UiApplication.getApplication().getAppEventLock()) 
        {
            Dialog.alert(msg);
        }
    }
}
