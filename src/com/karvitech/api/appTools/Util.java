/*
 * Util.java
 *
 * © Karvi Technologies, 2009-2014
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;
import java.util.*;
import java.io.*;
import javax.microedition.content.*;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.*; 
/**
 * 
 */
public final class Util {
    private Util() {    }
    
    public static String convertStringVectorToText(Vector stringVector, char delimitor) {
        if(stringVector == null) {
            return null;
        }
        
        StringBuffer strBuff = new StringBuffer();        
        for(int i=0;i<stringVector.size();i++) {
            strBuff.append(stringVector.elementAt(i).toString());
            strBuff.append(delimitor);
        }
        return strBuff.toString();
    }   
    
    public static Vector covertStringToTokens(String text, char delimitor) {
        Vector strVector = new Vector(4);
        int startIndex = 0;        
        
        if(text == null) {
            return null;
        }
        
        int delimitorIndex = text.indexOf(delimitor, startIndex);
        
        while(delimitorIndex != startIndex && delimitorIndex < text.length()) {
            String str = text.substring(startIndex, delimitorIndex);
            strVector.addElement(str);
            startIndex = delimitorIndex;
            delimitorIndex = text.indexOf(delimitor, startIndex);
        }
        if(strVector.size() == 0) {
            return null;
        }
        else {
            return strVector;
        }
    }
    
 /***
     * openAppWorld
     * <p>
     * Opens the App World pointing to a specific application. <p>
     * Note: This method takes advantage of
     * javax.microedition.content, which was introduced in 4.3.
     * There is no way to open the BlackBerry App World on an older OS.
     * <p>
     * @param myContentId App World ID of application to open.
     * @throws IllegalArgumentException if myContentId is invalid
     * @throws ContentHandlerException if App World is not installed
     * @throws SecurityException if access is not permitted
     * @throws IOException if access to the registry fails
     */

    public static boolean openAppWorld( String myContentId, String className) throws IllegalArgumentException, ContentHandlerException,
            SecurityException, IOException {
        Registry registry = Registry.getRegistry( className );
        Invocation invocation = new Invocation( null, null, "net.rim.bb.appworld.Content", true, ContentHandler.ACTION_OPEN );
        invocation.setArgs( new String[] { myContentId } );
        boolean mustExit = registry.invoke( invocation );
       /* if( mustExit ) // For strict compliance - this should never happen on a BlackBerry
            screen.close();
        */
        // Please note that this response won't be generated
        // until the BlackBerry App World exits
        // This method will block until that time
        Invocation response = registry.getResponse( true );
        if( response.getStatus() != Invocation.OK ) {
            return false;
        }
        return true;
        /*
        else
            Dialog.inform( "BlackBerry App World successfully opened." );*/
    }     
    
    public static boolean openAppWorldVendor( String myVendorId, String className) throws IllegalArgumentException, ContentHandlerException,
            SecurityException, IOException {
        Registry registry = Registry.getRegistry( className );
        Invocation invocation = new Invocation( null, null, "net.rim.bb.appworld.Vendor", true, ContentHandler.ACTION_OPEN );
        invocation.setArgs( new String[] { myVendorId } );
        boolean mustExit = registry.invoke( invocation );
       /* if( mustExit ) // For strict compliance - this should never happen on a BlackBerry
            screen.close();
        */
        // Please note that this response won't be generated
        // until the BlackBerry App World exits
        // This method will block until that time
        Invocation response = registry.getResponse( true );
        if( response.getStatus() != Invocation.OK ) {
            return false;
        }
        return true;
        /*
        else
            Dialog.inform( "BlackBerry App World successfully opened." );*/
    } 
    
    /**
     * Convert a vector to an object array, mainly used for creating object list for dialogs
     * @param vec <description>
     * @return <description>
     */
    public static Object[] convertVectorToArray(Vector vec) {
        if(vec == null) {
            return null;
        }
        else {
            int size = vec.size();
            Object[] objList = new Object[size];
            for(int i=0; i<size; i++) {
                objList[i] = vec.elementAt(i);
            }
            return objList;
        }
    }
    
    /**
     * Construct a int list with the value from 0 to (size -1)
     * this is a quick way to constrcut a array of index, mainly used for Dialog 
     * values for selection 
     * @param size : the size of the int array
     * @return <description>
     */
    static public int[] constructIntArray(int size) {
        if(size <=0) {
            return null;
        }
        int[] intList = new int[size];
        for(int i=0; i< size; i++) {
            intList[i] = i;
        }
        return intList;
    }
    
    /**
     * Retrieves a list of all the video encodings available on the current device
     * 
     * @return Newly created array of Strings whose elements are the video encodings supported by this device. Returns <code>null</code> if this device does not support video encoding.
     */
    public static String[] getVideoEncodings()
    {
        // Retrieve the supported video encodings available on this device
        String encodingsString = System.getProperty("video.encodings");
        
        // Return null if this device does not support video encoding
        if( encodingsString == null )
        {
            return null;
        }

        // Split the whitespace delimited encodingsString into a 
        // String array of encodings.
        Vector encodings = new Vector();       
        int start = 0;
        int space = encodingsString.indexOf(' ');
        while( space != -1 )
        {
            encodings.addElement(encodingsString.substring(start, space));          
            start = space + 1;
            space = encodingsString.indexOf(' ', start);
        }        
        encodings.addElement(encodingsString.substring(start, encodingsString.length())); 
        
        // Copy the encodings into a String array
        String[] encodingArray = new String[encodings.size()];
        encodings.copyInto(encodingArray);
        return encodingArray;
    }

    /**
     * The utility to an ApplicationDescriptor with module name
     * @return <description>
     */
    public static  ApplicationDescriptor getAppDescWithModuleName(String moduleName) {
        int handle = CodeModuleManager.getModuleHandle(moduleName);
        if(handle <=0) {
            return null;
        }
        ApplicationDescriptor ad = CodeModuleManager.getApplicationDescriptors(handle)[0];
        ApplicationDescriptor ad2 = new ApplicationDescriptor(ad, null);
        return ad2;
    } 

    
    public static ApplicationDescriptor[] getAppDescriptorWithName(String moduleName) {
         int handle = CodeModuleManager.getModuleHandle(moduleName);
         if(handle > 0) {
             ApplicationDescriptor[] appDesc = CodeModuleManager.getApplicationDescriptors(handle);
             return appDesc;
         }
         return null;
    }
    /**
     * run an application with ApplicationDescriptor
     * @param ad <description>
     */
    public static boolean runApp(String moduleName) {
        try {
            ApplicationDescriptor ads[] = getAppDescriptorWithName(moduleName);
            if(ads != null) {
                for(int i=0; i< ads.length;i++) {
                    // do not run the system module
                    if((ads[i].getFlags() & ApplicationDescriptor.FLAG_SYSTEM) == 0) {
                        ApplicationManager.getApplicationManager().runApplication(ads[i], true);
                    }
                }
            }
            return true;
        }
        catch (ApplicationManagerException e){
            //throw e;
        }
        return false;
    }  
        
    /**
     * run an application with ApplicationDescriptor
     * @param ad <description>
     */
    public static boolean runApp(ApplicationDescriptor ad) {
        try {
            ApplicationManager.getApplicationManager().runApplication(ad, true);
            return true;
        }
        catch (ApplicationManagerException e){
            //throw e;
        }
        return false;
    }   
    /**
     * The utility to get all the apps installed on the phone
     * @return <description>
     */
    public static Vector getApplist() {
        // Retrieve an array of handles for existing modules on a BlackBerry device
        int handles[] = CodeModuleManager.getModuleHandles();
        Vector appVect = new Vector();
        for(int i=0;i<handles.length;i++) {
             // Retrieve specific information about a module.
           /* String name = CodeModuleManager.getModuleName( handle );
            String vendor = CodeModuleManager.getModuleVendor( handle );
            String description = CodeModuleManager.getModuleDescription( handle );
          
            int version = CodeModuleManager.getModuleVersion( handle );
            int size = CodeModuleManager.getModuleCodeSize( handle );
            int timestamp = CodeModuleManager.getModuleTimestamp( handle );*/
            String description = CodeModuleManager.getModuleDescription( handles[i] );
            System.out.println("App description is:" + description);
            ApplicationDescriptor[] appDesc = CodeModuleManager.getApplicationDescriptors(handles[i]);
            if(appDesc != null) {
                for(int j=0;j<appDesc.length;j++) {
                    if(appDesc[j] != null) {
                        if(appDesc[j].getEncodedIcon() != null && (appDesc[j].getFlags() & ApplicationDescriptor.FLAG_SYSTEM) == 0) {
                            appVect.addElement(appDesc[j]);
                        }
                        System.out.println("App Name is:" + appDesc[j].getLocalizedName() + " position:" + appDesc[j].getPosition());
                        if((appDesc[j].getFlags() & ApplicationDescriptor.FLAG_SYSTEM) != 0) {
                            System.out.println("App is system");
                        }
                    }
                }
            }
            
        }
        for(int i=0;i<appVect.size();i++) {
            ApplicationDescriptor appDesc= (ApplicationDescriptor)appVect.elementAt(i);
            System.out.println("App Name is:" + appDesc.getLocalizedName() + " Module name:" + appDesc.getModuleName());
        }
        return appVect;
    }
    
    public static String[] split(String strString, String strDelimiter)	{
    	int iOccurrences = 0;		
    	int iIndexOfInnerString = 0;		
    	int iIndexOfDelimiter = 0;		
    	int iCounter = 0;		
    	
    	// Check for null input strings.		
    	if (strString == null)		
    	{			
    		throw new NullPointerException("Input string cannot be null.");		
    	}
    	// Check for null or empty delimiter		
    	// strings.		
    	if (strDelimiter.length() <= 0 || strDelimiter == null)		
    	{			
    		throw new NullPointerException("Delimeter cannot be null or empty.");		
    	}		
    	
    	// If strString begins with delimiter		
    	// then remove it in		
    	// order		
    	// to comply with the desired format.		
    	if (strString.startsWith(strDelimiter))		
    	{			
    		strString = strString.substring(strDelimiter.length());		
    	}		
    	
    	// If strString does not end with the		
    	// delimiter then add it		
    	// to the string in order to comply with		
    	// the desired format.		
    	if (!strString.endsWith(strDelimiter))		
    	{			
    		strString += strDelimiter;		
    	}		
    	// Count occurrences of the delimiter in		
    	// the string.		
    	// Occurrences should be the same amount		
    	// of inner strings.		
    	while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)		
    	{			
    		iOccurrences += 1;			
    		iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();		
    	}		
    	
    	// Declare the array with the correct		
    	// size.		
    	String[] strArray = new String[iOccurrences];		
    	
    	// Reset the indices.		
    	iIndexOfInnerString = 0;		
    	iIndexOfDelimiter = 0;		
    	
    	// Walk across the string again and this		
    	// time add the		
    	// strings to the array.		
    	while((iIndexOfDelimiter= strString.indexOf(strDelimiter,iIndexOfInnerString))!=-1)		
    	{			
    		// Add string to			
    		// array.			
    		strArray[iCounter] = strString.substring(iIndexOfInnerString, iIndexOfDelimiter);			
    		// Increment the			
    		// index to the next			
    		// character after			
    		// the next			
    		// delimiter.			
    		iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();			
    		
    		// Inc the counter.			
    		iCounter += 1;		
    	}            
    	return strArray;	
    }
    
    public static Bitmap getScaledBitmapImage(String imagename, double scaleX, double scaleY)
    {
        
        EncodedImage image = EncodedImage.getEncodedImageResource(imagename); 
        
        int currentWidthFixed32 = Fixed32.toFP(image.getWidth());
        int currentHeightFixed32 = Fixed32.toFP(image.getHeight());
        
        int width =(int)(image.getWidth() * scaleX);
        int height = (int)(image.getHeight() * scaleY);
        
        
        int requiredWidthFixed32 = Fixed32.toFP(width);
        int requiredHeightFixed32 = Fixed32.toFP(height);
        
        int scaleXFixed32 = Fixed32.div(currentWidthFixed32, requiredWidthFixed32);
        int scaleYFixed32 = Fixed32.div(currentHeightFixed32, requiredHeightFixed32);
        
        image = image.scaleImage32(scaleXFixed32, scaleYFixed32);
        
        return image.getBitmap();
    }
} 
