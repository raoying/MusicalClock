//#preprocess
/*
 * AdScreen.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */
//
//#ifdef FREE_VERSION
package com.karvitech.apps.musicalclock;
import java.util.Vector;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.ui.*; 
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Dialog; 
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.MenuItem;
//import com.karvitech.apps.utilities.*;
import javax.microedition.content.*;
import net.rim.blackberry.api.browser.*;

import net.rim.device.api.ui.decor.*;
import net.rim.device.api.system.*;
import com.karvitech.api.appTools.*;
import com.karvitech.apps.alarmlib.*;

import com.inneractive.api.ads.InneractiveAd;
import net.rimlib.blackberry.api.advertising.app.Banner;



/**
 * 
 */
class AdScreen extends MainScreen implements  FieldChangeListener {
    private static String INNER_ACTIVE_AD_ID = "KarviTech_MusicalClock_BB";
    private ButtonField _moreAppsButton;   
    AdScreen() { 
    
        
        VerticalFieldManager vfm = new VerticalFieldManager(VerticalFieldManager.NO_VERTICAL_SCROLL
                                                            | VerticalFieldManager.NO_VERTICAL_SCROLLBAR
                                                            | VerticalFieldManager.USE_ALL_WIDTH); 
        

        Background background = BackgroundFactory.createSolidBackground(Color.BLACK);
        vfm.setBackground(background);
        _moreAppsButton = new ButtonField("More Apps", Field.FIELD_HCENTER|ButtonField.CONSUME_CLICK);
        _moreAppsButton.setChangeListener(this);
        vfm.add(_moreAppsButton);
        this.add(vfm);
        
        InneractiveAd.displayAd((MainScreen)(this), INNER_ACTIVE_AD_ID , InneractiveAd.FULL_SCREEN_AD_TYPE, null); 
        
    }
        /**
     * Implementing the FieldChangeListener
     * @param field <description>
     * @param context <description>
     */
    public void fieldChanged(Field field, int context) 
    {
        if(_moreAppsButton == field) {
           try {
              if(!Util.openAppWorldVendor(MusicalClockApp.APP_WORLD_VENDOR_ID, MusicalClockApp.class.getName())) { 
                   // BrowserSession browser = Browser.getDefaultSession();
                   // browser.displayPage(MusicalClockApp.MOBI_HAND_MORE_APP_URL); 
                   Dialog.alert("Failed to launch the Blackberry App World. Please make sure it is installed and available for your country.");
               }
            }
            catch(Exception e) {
                // BrowserSession browser = Browser.getDefaultSession();
                // browser.displayPage(MusicalClockApp.MOBI_HAND_MORE_APP_URL);                     
                Dialog.alert("Failed to launch the Blackberry App World. Please make sure it is installed and available for your country.");                
            }
        }
    }    

} 
//#endif