/*
 * AppEventListener.java
 *
 * © Karvi Technology Inc.., 2009-2014
 * Confidential and proprietary.
 */
package com.karvitech.api.appTools;

/**
 * Event listener for an application
 */
public interface AppEventListener {
    /**
     * event handler for application event(within the application)
     * @param eventID: The id of the event
     * @param sourceObj: the originator of the event 
     * @param eventPackage: the event specific data
     * @return true if the event should be consumed, false if the event not consumed
     */
    public boolean appEventHappened(int eventID, Object sourceObj, Object eventPackage);
} 
