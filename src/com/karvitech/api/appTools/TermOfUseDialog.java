package com.karvitech.api.appTools;

/*
 * DontAskAgainDialog.java
 *
 * ? Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */

import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*; 
import net.rim.device.api.system.*;


public class TermOfUseDialog extends Dialog {
       public static int ACCEPTED = 1;
       public static int DECLINED = 2;

       private TermOfUseDialog(String message, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap) {                
               super(message, choices,values,defaultChoice, bitmap);       
       }
       
        public static int showDialog()
        {
            String termOfUse = loadTermOfUse();
            Object[] list = new Object[2];
            list[0] = "Accept";
            list[1] = "Decline";
             //list[2] = doNotShowAgain;
                    
            int[] indexList = new int[2];
            indexList[0] = 0;
            indexList[1] = 1;
            
            TermOfUseDialog dlg = new TermOfUseDialog(termOfUse, list, indexList, 0, null);
            int displayFontSize = 10;
            if(Display.getHeight()*Display.getWidth() >= 480*320) {
                displayFontSize = 20;
            }

            dlg.setFont(Font.getDefault().derive( Font.PLAIN, displayFontSize));            
            dlg.doModal();  
            int choice = dlg.getSelectedValue();                
            if(choice == 1) { 
                return DECLINED;
            }
            else if(choice == 0) {
                return ACCEPTED;
            }
            return DECLINED;
        }

        public static void showDialogWithOkButton()
        {
            String termOfUse = loadTermOfUse();
            Object[] list = new Object[1];
            list[0] = "Ok";
             //list[2] = doNotShowAgain;
                    
            int[] indexList = new int[1];
            indexList[0] = 0;
            
            TermOfUseDialog dlg = new TermOfUseDialog(termOfUse, list, indexList, 0, null);
            int displayFontSize = 10;
            if(Display.getHeight()*Display.getWidth() >= 480*320) {
                displayFontSize = 20;
            }

            dlg.setFont(Font.getDefault().derive( Font.PLAIN, displayFontSize));            
            dlg.doModal();  
        }        
        static String loadTermOfUse() {
    
            InputStream is = null;
            InputStreamReader isr = null;
            StringBuffer sb = null;
            try {
    
                //The class name is the fully qualified package name followed
    
                //by the actual name of this class
    
                Class classs = Class.forName("com.karvitech.api.appTools.TermOfUseDialog");
    
    
                //to actually retrieve the resource prefix the name of the file with a "/"
    
                is = classs.getResourceAsStream("/Legalterms.txt");
    
    
                //we now have an input stream. Create a reader and read out
    
                //each character in the stream.
    
                isr = new InputStreamReader(is);
    
                // Step 3:
                sb = new StringBuffer();
                char[] buff = new char[1024];
                int len;
                while ((len = isr.read(buff)) > 0) {
                    sb.append(buff, 0, len);
                }
                
    
            } catch(Exception ex) {
    
                System.out.println("Error: " + ex.toString());
    
            }     
            finally {
                if (is != null) try { is.close(); } catch (Exception ignored) {}
                if (isr != null) try { isr.close(); } catch (Exception ignored) {}
                return sb.toString();
            }                 
        }
          
}
