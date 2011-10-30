/*
 * RebootAboutScreen.java
 *
 * © Karvi Technology Inc., 2009-2014
 * Confidential and proprietary.
 */
package com.karvitech.api.appTools;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.blackberry.api.invoke.*;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;


public class AboutScreen extends MainScreen {
    static final String STR_ABOUT = "© Karvi Technologies, Inc.";
    static final String STR_CREDITS = "Weather Symbols and Weather forecast from yr.no, Delivered by the Norwegian Meteorological Institute and the Norwegian Broadcasting Corporation.";
    static final String STR_TIP_1 = "If there are multiple folders of music files, you can select the parent folder, the app will search recursively in the sub-folders.";
    static final String STR_TIP_2 = "The preloaded ringtones and songs on BB may not be accessible to 3rd party apps, please use your own music files and use the Test button to check if they work.";
    static AboutScreen _instance;
    private LabelField _text;

    private String _appTitle;
    private AboutScreen() 
    {        
        super(Field.FIELD_HCENTER);
        _appTitle = ApplicationDescriptor.currentApplicationDescriptor().getLocalizedName();
        
        VerticalFieldManager vfm = new VerticalFieldManager(Field.FIELD_HCENTER | Field.USE_ALL_WIDTH);
        
        setTitle("Support Information");
        _text = new LabelField(STR_ABOUT, Field.FIELD_HCENTER);
        vfm.add(_text);
        
        ActiveRichTextField link = new ActiveRichTextField("http://forums.karvitech.com", ActiveRichTextField.TEXT_ALIGN_HCENTER);
        vfm.add(link);
        
        vfm.add(new ButtonField("Send Feedback", Field.FIELD_HCENTER) {
            protected boolean navigationClick(int status,int time) {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        String strDeviceInfo = "Device Information:\nDevice Model: " + DeviceInfo.getDeviceName() + "\nOS Version: " + DeviceInfo.getSoftwareVersion();                        
                        Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, new MessageArguments(MessageArguments.ARG_NEW,"productsupport@karvitech.com",_appTitle + " Feedback",strDeviceInfo));
                    }
                });                
                return true;
            }            
        });
        vfm.add(new ButtonField("Tell a Friend", Field.FIELD_HCENTER) {
            protected boolean navigationClick(int status,int time) {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Invoke.invokeApplication( Invoke.APP_TYPE_MESSAGES, new MessageArguments(MessageArguments.ARG_NEW,"", _appTitle + " on Blackberry",""));
                    }
                });                
                return true;
            }            
        }); 

        vfm.add(new ButtonField("Term Of Use", Field.FIELD_HCENTER) {
            protected boolean navigationClick(int status,int time) {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        TermOfUseDialog.showDialogWithOkButton();
                    }
                });                
                return true;
            }            
        }); 
        // Credits
        vfm.add(new SeparatorField());
        addTip(vfm, "Credits:", STR_CREDITS);
        vfm.add(new SeparatorField());      
        // tip 1
        vfm.add(new SeparatorField());
        addTip(vfm, "Tip 1:", STR_TIP_1);
        vfm.add(new SeparatorField());       
        addTip(vfm, "Tip 2:", STR_TIP_2);       
        add(vfm);
    }
    
    private void addTip(VerticalFieldManager vfm, String tipTitle, String tipBody) {
        LabelField tipTitleField = new LabelField(tipTitle);
        Font titleFont = tipTitleField.getFont();
        titleFont = titleFont.derive(Font.BOLD, titleFont.getHeight() + 2);
        tipTitleField.setFont(titleFont);
        vfm.add(tipTitleField);
        vfm.add(new LabelField(tipBody));   
        vfm.add(new NullField(Field.FOCUSABLE));
    }
    
    public static AboutScreen getInstance()
    {
        if(_instance == null)
        {
            _instance = new AboutScreen();
        }
        return _instance;
    }
    
    public void setText(String text)
    {
        _text.setText(text);
    }
    
    public void setTitle(String title)
    {
        setTitle(new LabelField(title, LabelField.USE_ALL_WIDTH));   
    }
    
    protected boolean onSavePrompt() {
        return true;
    }    
} 
