/*
 * FileExplorerFileHolder.java
 *
 */

package com.karvitech.apps.filelib;

/**
 * Helper class to store information about directories and files that are being
 * read from the system.
 */
public  final class FileExplorerFileHolder
{
    private String _filename;
    private String _path;
    private boolean _isDir;
    
    
    /**
     * Constructor.  Pulls the path and file name from the provided string.
     * 
     * @param fileinfo The path and file name provided from the FileConnection.
     */
    public FileExplorerFileHolder(String fileinfo) 
    {
        // Pull the information from the URI provided from the original FileConnection.
        int slash = fileinfo.lastIndexOf('/');
        
        if ( slash == -1 ) 
        {
            throw new IllegalArgumentException( "fileinfo must have a slash" );
        }
        
        _path = fileinfo.substring(0, ++slash);
        _filename = fileinfo.substring(slash);
    }

    
    /**
     * Retrieves the file name.
     * 
     * @return Name of the file, or null if it's a directory.
     */
    public String getFileName() 
    {
        return _filename;
    }

    /**
     * Retrieves the path of the directory or file.
     * 
     * @return Fully qualified path.
     */
    public String getPath() 
    {
        return _path;
    }

    /**
     * Determins if the FileHolder is a directory.
     * @return true if FileHolder is directory, otherwise false.
     */
    public boolean isDirectory() 
    {
        return _isDir;
    }

    /**
     * Enables setting of directory for FileHolder.
     * @param isDir true if FileHolder should be a directory.
     */
    public void setDirectory(boolean isDir) 
    {
        _isDir = isDir;
    }
}
