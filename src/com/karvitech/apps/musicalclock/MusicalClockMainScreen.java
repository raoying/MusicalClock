//#preprocess
/*
 * WoldClockMainScreen.java
 *
 * � <your company here>, 2003-2008
 * Confidential and proprietary.
 */

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
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.MenuItem;
//import com.karvitech.apps.utilities.*;
import javax.microedition.content.*;
import net.rim.blackberry.api.browser.*;


import net.rim.device.api.system.*;
import com.karvitech.api.appTools.*;
import com.karvitech.api.weather.DayWeatherInfo;
import com.karvitech.apps.alarmlib.*;
//#ifdef FREE_VERSION
//import net.rim.blackberry.api.advertising.app10432.Banner;
//#endif
//#ifdef FREE_VERSION
import com.inneractive.api.ads.InneractiveAd;
import net.rimlib.blackberry.api.advertising.app.Banner;
//#endif
/**
 * 
 */
class MusicalClockMainScreen extends MainScreen implements ConfigurationListener, RealtimeClockListener {
    private final static String STR_SHUT_DOWN_WARNING = "The alarms will not be triggered if the app is shut down. Do you want to proceed?";
    private static final int MODE_NORMAL = 0;        // normal, just show the time
    private static final int MODE_SETTING_STYLE = 1; // setting style mode, user can set style
    
    private static int AD_PACEMENT_ID = 34898;
    private static String INNER_ACTIVE_AD_ID = "KarviTech_MusicalClock_BB";
        
    private Calendar _cal = Calendar.getInstance();
    private Vector _settings;
    private LabelField _timeLabel = new LabelField();
    private LabelField _timeLabel2 = new LabelField();
    private static MusicalClockMainScreen _instance;
    
    private int _style = DigitalClockFaceField.SKY_COLOR;  // use the values defined in the DigitalClockFaceField
    private int _bkgroundType; // use the values defined in the DigitalClockFaceField 
    private Vector _clocks = new Vector();
    private int _mode = MODE_NORMAL;
    private int _orientation;
    
    private VerticalFieldManager _vfm;
    private VerticalFieldManager _container; // the vfm with clock field in it
    DigitalClockFaceField _clockField;
//#ifdef FREE_VERSION        
    Banner _bannerAd;
//#endif    
    private OnlinHelpMenuItem _onlineHelp = new OnlinHelpMenuItem("Online Help", 100, 10, "http://www.karvitech.com/OnlineHelp/BB/MusicalClock/Instructions.html");
        
    private MenuItem _alarmsMenuItem = new MenuItem("Alarms", 100, 10)
    {
        public void run()
        {
            AlarmListScreen.showScreen();
        }
    };    
    private MenuItem _aboutMenuItem = new MenuItem("Support", 100, 10)
    {
        public void run()
        {
            ((UiApplication)(Application.getApplication())).pushScreen(AboutScreen.getInstance());
        }
    }; 
    private MenuItem _weatherMenuItem = new MenuItem("Weather", 100, 10)
    {   
        public void run()
        {
		    String url = "http://api.yr.no/weatherapi/locationforecast/1.8/?lat=60.10;lon=9.58";
		    url = "com.karvitech.apps.musicalclock.MusicalClockApp";
		    DayWeatherInfo.parseXML(url);
        }
    };
        
    private MenuItem _shutDownMenuItem = new MenuItem("Shut Down", 100, 10)
    {
        public void run()
        {
            Dialog dlg = new Dialog(Dialog.D_YES_NO, STR_SHUT_DOWN_WARNING, Dialog.NO, Bitmap.getPredefinedBitmap(Bitmap.QUESTION), 0);
            dlg.doModal();
            if(dlg.getSelectedValue() == Dialog.YES) { 
                Configuration.getInstance().removeAllListener();
                Configuration.getInstance().forceSaveSettings();
                System.exit(0);
            }
        }
    }; 

    private MenuItem _upgradeMenuItem = new MenuItem("Upgrade", 100, 10)
    {
        public void run()
        {
            UpgradeDialog.show();
        }
    }; 

    private MenuItem _moreAppsMenuItem = new MenuItem("More Apps", 100, 10)
    {
        public void run()
        {
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
    }; 
        
    private MenuItem _timeFormatMenuItem = new MenuItem("Toggle Military Time", 100, 10)
    {
        public void run()
        {
            MusicalClockGlobalSettingItem globalSettings = (MusicalClockGlobalSettingItem)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS);            
            globalSettings.setMilitaryTime(!globalSettings.isMilitaryTime()); 
            Configuration.getInstance().saveSettings();   
        }
    };     

    private MenuItem _settingsMenuItem = new MenuItem("Settings", 100, 10)
    {
        public void run()
        {
            ((UiApplication)(Application.getApplication())).pushScreen(new MusicalClockSettingsScreen()); 
        }
    };

    private MenuItem _changeStyleMenuItem = new MenuItem("Change Style", 100, 10)
    {
        public void run()
        {
            MusicalClockMainScreen mainScreen = MusicalClockMainScreen.getInstance();
            mainScreen.setMode(MusicalClockMainScreen.MODE_SETTING_STYLE);
            mainScreen.invalidate();
            //Status.show("Scroll/Swipe left/right to adjust the clock color, up/down to change the background.\n Click the trackball/touch screen to accept.", 15000);
            Dialog.inform("Scroll/Swipe left/right to adjust the clock color, up/down to change the background.\n Click the trackball/touch screen to accept.");

        }
    };    
            
    static MusicalClockMainScreen getInstance() {
        if (_instance == null) {
            _instance = new MusicalClockMainScreen();
        }
        return _instance;
    }
    
    void setStyle(int style) {
            _style = style;        
    }
    
    void refresh() {
        // clear the listeners
        for(int i=_clocks.size(); i>0;i--) {
            Application.getApplication().removeRealtimeClockListener((RealtimeClockListener)(_clocks.elementAt(i-1)));
        }
        _clocks.removeAllElements();
        
        this.deleteAll();
        //this.invalidate();
        populateScreen();       
        //refreshClock();
    }
    
    private void setMode(int mode) {
        _mode = mode;
    }
    
    private MusicalClockMainScreen() {
        // the listener is used to fresh ad
        // not sure why in this app the ad does not refresh
        //UiApplication.getApplication().addRealtimeClockListener(this);
        try {
            _settings = (Vector) Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST);
            Configuration.getInstance().addListener(this);       
            _orientation = Display.getOrientation();
            populateScreen();
        }
        catch(Exception e) {
             EventLogger.logEvent( 0xbd2b228daa4b3197L, ("Main Screen problem:" + e.toString() + " " + e.getMessage()).getBytes());
        }
    }


    
    public void paint(Graphics graphics)
    {        
        super.paint(graphics);
        if(this._mode == MODE_SETTING_STYLE) {
            Bitmap leftArrowImage = Bitmap.getBitmapResource(DigitalClockFaceField.getModuleName(), "arrow_left.png");            
            Bitmap rightArrowImage = Bitmap.getBitmapResource(DigitalClockFaceField.getModuleName(), "arrow_right.png");
            int sideArrowY = (Display.getHeight() - leftArrowImage.getHeight())/2;
            int sideArrowWidth = leftArrowImage.getWidth();
            int sideArrowHeight = leftArrowImage.getHeight();
            graphics.drawBitmap(0, sideArrowY, sideArrowWidth, sideArrowHeight, leftArrowImage, 0, 0);
            graphics.drawBitmap(Display.getWidth() - sideArrowWidth, sideArrowY, sideArrowWidth, sideArrowHeight, rightArrowImage, 0, 0);
            
            Bitmap topArrowImage = Bitmap.getBitmapResource(DigitalClockFaceField.getModuleName(), "arrow_top.png");            
            Bitmap bottomArrowImage = Bitmap.getBitmapResource(DigitalClockFaceField.getModuleName(), "arrow_bottom.png");
            int topArrowX = (Display.getWidth() - topArrowImage.getWidth())/2;
            int topArrowWidth = topArrowImage.getWidth();
            int topArrowHeight = topArrowImage.getHeight();
            graphics.drawBitmap(topArrowX, 0, topArrowWidth, topArrowHeight, topArrowImage, 0, 0);
            graphics.drawBitmap(topArrowX, Display.getHeight() - topArrowHeight, topArrowWidth, topArrowHeight, bottomArrowImage, 0, 0);
            
        }
    }    
    
    private void populateScreen() {
        _clockField = null; // need a new clock field
        _vfm = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR | USE_ALL_WIDTH | USE_ALL_HEIGHT | FIELD_HCENTER)
        {
          /*  public void paintBackground( Graphics g ) {
               //if(_style == DigitalClockFaceField.DIGITAL_STYLE) 
                {
                    g.clear();
                    int color = g.getColor();
                    g.setColor( Color.BLACK );
                    g.fillRect( 0, 0, Display.getWidth(), Display.getHeight() );
                    g.setColor( color );
                }
            }*/
            
            public void paint(Graphics graphics)
            {
                //if(_style == DigitalClockFaceField.DIGITAL_STYLE) 
                {
                    graphics.setBackgroundColor(Color.BLACK);
                }
                graphics.clear();
                super.paint(graphics);
            }
            protected void sublayout(int width,
                         int height) {

                super.sublayout(Display.getWidth(), Display.getHeight());
                          
                // super.sublayout(Display.getWidth(), Display.getHeight());
            }
        };      
        
         add(_vfm);
        _container = new VerticalFieldManager( Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR );

//        HorizontalFieldManager hfm = null;
        
        // only one clock
        //MusicalClockSettingItem item = new MusicalClockSettingItem(Calendar.getInstance().getTimeZone());
       
        
        // style must be set before calling this getInstance is first called   
        MusicalClockGlobalSettingItem globalSettings = (MusicalClockGlobalSettingItem)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS);
       // MusicalClockGlobalSettingItem2 globalSettings2 = (MusicalClockGlobalSettingItem2)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2);
        
        _style = globalSettings.getClockColor();
        _bkgroundType = globalSettings.getBackgroundType();     
        
        // create banner 
//#ifdef FREE_VERSION       
 
        // add the ad
        try {
           // InneractiveAd.displayAd((MainScreen)(this), INNER_ACTIVE_AD_ID , InneractiveAd.FULL_SCREEN_AD_TYPE, null);
            Banner bannerAd = new Banner(AD_PACEMENT_ID, null);
            bannerAd.setMMASize(Banner.MMA_SIZE_EXTRA_LARGE);
    
    
            HorizontalFieldManager hfmBanner = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER
                                                                    | HorizontalFieldManager.FIELD_VCENTER);
            hfmBanner.add(bannerAd);
            _vfm.add(hfmBanner);
        }
        catch(Exception e) {
            System.out.println("error:" + e.toString() + " :" + e.getMessage());
        }
        
        
//#endif        
        
        // create clock
        refreshClock();
     /*   _clockField = new DigitalClockFaceField(item, globalSettings2.isMilitaryTime(), DigitalClockFaceField.PINK_COLOR);
        DigitalClockFaceField.loadImages(_style);         
        _clockField.setBackGroundType(this._bkgroundType); 
        Vector alarmList = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST); 
        _clockField.setAlarmOn(alarmList.size() > 0);
        container.add(_clockField);*/
        
        _vfm.add(_container);
      
    }
    
    protected void refreshClock() {
        if(_clockField != null) {
            _container.delete(_clockField);
        }
        MusicalClockGlobalSettingItem2 globalSettings2 = (MusicalClockGlobalSettingItem2)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2);     
        MusicalClockSettingItem item = new MusicalClockSettingItem(Calendar.getInstance().getTimeZone());        
        _clockField = new DigitalClockFaceField(item, globalSettings2.isMilitaryTime(), DigitalClockFaceField.PINK_COLOR);
        DigitalClockFaceField.loadImages(_style);         
        _clockField.setBackGroundType(this._bkgroundType); 
        Vector alarmList = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST); 
        
        // check if there are active alarms
        boolean hasAlarm = false;
        for(int i=0; i< alarmList.size();i++) {
            AlarmItem alarmItem = (AlarmItem)alarmList.elementAt(i);
            if(alarmItem._alarmOn) {
                hasAlarm = true;
                break;
            }
        }
        _clockField.setAlarmOn(hasAlarm);
        _container.add(_clockField);

        
    }
    
    protected void makeMenu(Menu menu, int instance)
    {
        menu.deleteAll();
        menu.add(_onlineHelp);
        menu.addSeparator();
        menu.add(_alarmsMenuItem);
       // menu.add(_timeFormatMenuItem);
        menu.add(_settingsMenuItem);
     //   menu.add(_addMenuItem);
       /* if(_style == DigitalClockFaceField.DIGITAL_STYLE) {
            menu.add(_toLcdMenuItem);
        } 
        else {
            menu.add(_toDigitalMenuItem);
        }*/
        menu.add(_changeStyleMenuItem);        
        menu.add(_shutDownMenuItem);
        menu.add(_weatherMenuItem);
        menu.addSeparator();
        menu.add(_aboutMenuItem);
        if(Configuration.isFreeVersion()) {
             menu.addSeparator();
             menu.add(_upgradeMenuItem);
        }
        menu.add(_moreAppsMenuItem);
    }
    
    public boolean onClose() {
        Configuration.getInstance().removeListener(this);
        // clear the listeners
        for(int i=_clocks.size(); i>0;i--) {
            Application.getApplication().removeRealtimeClockListener((RealtimeClockListener)(_clocks.elementAt(i-1)));
        }
        _clocks.removeAllElements();        
        return super.onClose();
    }
            
   /**
    * @see Screen#touchEvent(TouchEvent)
    * This implementation outputs Touch Event notifications to standard output
    */
    protected boolean touchEvent(TouchEvent message)
    {
        // Handle the current event                                  
        TouchGesture touchGesture = message.getGesture(); 
        boolean styleChanged = false;
        boolean backgroundChanged = false;   
        
        /*if(_mode == MODE_SETTING_STYLE) {
             if(message.getEvent() ==  TouchEvent.CLICK) {
                saveStyle();
                return true;
            }
        }      
        else*/ 
        if (touchGesture != null && _mode == MODE_SETTING_STYLE) {
            if(touchGesture.getEvent() ==  TouchGesture.TAP) {
                saveStyle();
                return true;
            }
            else if (touchGesture.getEvent() == TouchGesture.SWIPE) {
                    switch(touchGesture.getSwipeDirection())
                    {
                        case TouchGesture.SWIPE_WEST:                            
                            _style = (++_style)%DigitalClockFaceField.TOTAL_STYLE_NUM;
                            styleChanged = true;
                            break;
                        case TouchGesture.SWIPE_EAST:
                            _style = (DigitalClockFaceField.TOTAL_STYLE_NUM + --_style)%DigitalClockFaceField.TOTAL_STYLE_NUM;
                            styleChanged = true;
                            break;
                        case TouchGesture.SWIPE_NORTH:
                            backgroundChanged = true;
                            _bkgroundType = (++_bkgroundType)%DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM;
                            break;
                        case TouchGesture.SWIPE_SOUTH:
                            backgroundChanged = true;
                            _bkgroundType = (DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM + --_bkgroundType)%DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM;
                            break;                            
                    } 
                    if(backgroundChanged) {
                        _clockField.setBackGroundType(_bkgroundType);
                        this.invalidate();
                    }
                    
                    if(styleChanged) {
                        DigitalClockFaceField.loadImages(_style);
                        this.invalidate();
                    }
                    return true;
            }
        }
        return true;  // consume
    }
    
    protected boolean navigationMovement(int dx,
                                         int dy,
                                         int status,
                                         int time)    {
    
        int threshold = 0; // need to find out the real threshold on a phone
        // for test only         
  //      dx = 30;
                                             
        // set the clock color and background if in the setting mode
        if(_mode == MODE_SETTING_STYLE) {
            if(dx > threshold) {
                _style = (++_style)%DigitalClockFaceField.TOTAL_STYLE_NUM;
            } 
            else if(dx < -threshold) {
                _style = (DigitalClockFaceField.TOTAL_STYLE_NUM + --_style)%DigitalClockFaceField.TOTAL_STYLE_NUM;
            }
            
            if(dy > threshold) {
                _bkgroundType = (++_bkgroundType)%DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM;
            } 
            else if(dy < -threshold) {
                _bkgroundType = (DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM + --_bkgroundType)%DigitalClockFaceField.TOTAL_BACKGROUND_TYPE_NUM;
            }            
            
            boolean needToRefresh = false;
            if(Math.abs(dx) > threshold) {
                DigitalClockFaceField.loadImages(_style);
                needToRefresh = true;
            }
            if(Math.abs(dy) > threshold) {
                _clockField.setBackGroundType(_bkgroundType);
                needToRefresh = true;
            }
            
            if(needToRefresh)            
            {
                this.invalidate();
            }
        }
        return true; 
    }
    
    protected void saveStyle() {
            _mode = MODE_NORMAL;
            MusicalClockGlobalSettingItem globalSettings = (MusicalClockGlobalSettingItem)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS);            
            globalSettings.setClockColor(_style);
            globalSettings.setBackGroundType(_bkgroundType);
            Configuration.getInstance().saveSettings();
            this.invalidate();
//#ifdef FREE_VERSION
            UpgradeDialog.show();
//#endif
            
    }
    
    protected boolean navigationClick(int status, int time) {
        if(_mode == MODE_SETTING_STYLE) {
            saveStyle();
            return true;
        }
        return super.navigationClick(status, time);
    }
                                      
    protected boolean keyChar(char c, int status, int time) {
        if(c == Characters.ESCAPE) {
            UiApplication.getApplication().requestBackground();
            return true;
        }
        else {
            return super.keyChar(c, status, time);
        }        
    }
    
    
    protected boolean keyUp(int keycode, int time) {
        if(keycode == Characters.ESCAPE) {
            UiApplication.getApplication().requestBackground();
            return true;
        }
        else {
            return super.keyUp(keycode, time);
        }
    }
    
    protected void sublayout(int width,
                         int height) {

        if (_orientation != Display.getOrientation()) {
            _orientation = Display.getOrientation();
                                      
            /*this.deleteAll();
            this.invalidate();
            populateScreen();*/
            refreshClock();
                    
        }
        super.sublayout(Display.getWidth(), Display.getHeight());
                          
        // super.sublayout(Display.getWidth(), Display.getHeight());
    }        
    // implements the listener
    public void configurationChanged() {
        this.refreshClock();
    }
   /**
    * implement the RealtimeClockListener interface
    */
    public void clockUpdated() 
    {
        if(UiApplication.getApplication().isForeground()) {
            
            this.refresh();
        }
    }
}