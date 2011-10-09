/*
 * OnlinHelpMenuItem.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */
package com.karvitech.api.appTools;
import net.rim.blackberry.api.browser.*;
import net.rim.device.api.ui.*;

/**
 * 
 */
public class OnlinHelpMenuItem extends MenuItem {
    String _url;
    public OnlinHelpMenuItem(String displayString,int ordinal, int priority, String url) {
        super(displayString, ordinal, priority);
        _url = url;
    }    

    public void run() {
        BrowserSession site = Browser.getDefaultSession();
        site.displayPage(_url);
    }
} 
