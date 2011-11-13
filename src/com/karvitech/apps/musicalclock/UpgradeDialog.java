/*
 * UpgradeDialog.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;
import net.rim.device.api.ui.component.*;
import net.rim.blackberry.api.invoke.*;
//import com.karvitech.apps.utilities.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*; 
import net.rim.device.api.system.*;
import net.rim.blackberry.api.browser.*;
import com.karvitech.api.appTools.*;

/**
 * 
 */
class UpgradeDialog  {
	static String STR_TRIAL_EXPIRED = "Trial expired for the weather feature.\n";
    static String STR_UPGRADE = "Please check out the full version, which supports:\n  Weather forecasts\n  Unlimited Alarms\n  Ability to set playlist for alarms\n  5 LED colors\n Use radio such as Pandora for alarms. \nNotes: Please delete the free version if you install the full version, otherwise it will not work.";
    static String STR_APPWORLD = "App World";
    static String STR_MOBIHAND = "Mobihand Store";
    static String STR_NO_THANKS = "No, thanks";
    
    static public void show(boolean trialExpired) {

            Object[] list = new Object[3];
            list[0] = STR_APPWORLD;
            list[1] = STR_MOBIHAND;
            list[2] = STR_NO_THANKS;
             //list[2] = doNotShowAgain;
                    
            int[] indexList = new int[3];
            indexList[0] = 0;
            indexList[1] = 1;
            indexList[2] = 2;
            
            String message = trialExpired?(STR_TRIAL_EXPIRED + STR_UPGRADE):STR_UPGRADE;
            Dialog dlg = new Dialog(message, list, indexList, 0, null);
            int displayFontSize = 15;
            if(Display.getHeight()*Display.getWidth() >= 480*320) {
                displayFontSize = 25;
            }

            dlg.setFont(Font.getDefault().derive( Font.PLAIN, displayFontSize));          
            dlg.doModal();  
            int choice = dlg.getSelectedValue();                
            if(choice == 0) { 
                try {
                    if(!Util.openAppWorld(MusicalClockApp.APP_WORLD_UPGRADE_ID, MusicalClockApp.class.getName())) {
                    //if(!Util.openAppWorldVendor("5010", MusicalClockApp.class.getName())) {    
                        Dialog.alert("Failed to launch the Blackberry App World. Please make sure it is installed and available for your country.");
                    }
                }
                catch(Exception e) {
                    Dialog.alert("Failed to launch the Blackberry App World. Please make sure it is installed and available for your country.");                
                }
            }
            else if(choice == 1) {
                BrowserSession browser = Browser.getDefaultSession();
                browser.displayPage(MusicalClockApp.MOBI_HAND_UPGRADE_URL);    
                //   Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, new MessageArguments(MessageArguments.ARG_NEW,"productsupport@karvitech.com","" + " Feedback",""));
            }
    }

    private UpgradeDialog() {    }
} 
