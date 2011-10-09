//#preprocessor
/*
 * MusicalClockFace.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;

import java.util.TimeZone;

import net.rim.device.api.ui.*;
//import net.rim.device.api.ui.Graphics;

import net.rim.device.api.system.*;


/**
 * 
 */
class DigitalClockFaceField extends ClockFaceField {
    //public static final int BLUE_COLOR = 1;
    //public static final int PURPLE_COLOR = 5; 
    public static final int AD_HEIGHT = 53;
    public static final int LEMON_COLOR = 0;
    public static final int SKY_COLOR = 1;
    public static final int PINK_COLOR = 2;
    public static final int ORANGE_COLOR = 3; 
    public static final int GREEN_COLOR = 4;
    //public static final int WHITE_COLOR = 5;
//#ifdef FREE_VERSION      
    public static final int TOTAL_STYLE_NUM = 2; // the total number of the styles, if a new style is added it should be increased too 
//#else
    public static final int TOTAL_STYLE_NUM = 5; // the total number of the styles, if a new style is added it should be increased too 
//#endif    
    public static final int BACKGROUND_BLACK = 0;
    public static final int BACKGROUND_TEXTURED = 1;
   // public static final int BACKGROUND_WHITE = 2;
    public static final int BACKGROUND_SPARCE = 2;
    public static final int TOTAL_BACKGROUND_TYPE_NUM = 3;
     
    private String _filePrefix;
    private int screenWidth = Display.getWidth();
    private int screenHeight = Display.getHeight();
    private String _deviceName = DeviceInfo.getDeviceName();
    
//#ifdef FREE_VERSION  
    private static final String MODULE_NAME_SUFIX_SUPER = "SuperFree";
    private static final String MODULE_NAME_SUFIX_HIGH = "HighFree";
    private static final String MODULE_NAME_SUFIX_LOW = "LowFree";        
//#else
    private static final String MODULE_NAME_SUFIX_SUPER = "Super";
    private static final String MODULE_NAME_SUFIX_HIGH = "High";
    private static final String MODULE_NAME_SUFIX_LOW = "Standard";  
//#endif
    
    private static final String MODULE_NAME_PREFIX = "MusicClockResource";
    private static final String IMAGE_FILE_SUFIX = ".png";    
    private static final String BG_IMAGE_ID = "_bg";    
        
    private static String _moduleName;
    private static Bitmap _colonImage;
    
    // images
    private static Bitmap _numImages; // this is used when all the numbers are in one image file
    private static Bitmap _weekdayImages;
    private static Bitmap _amPmImages;
    private static Bitmap _alarmBellImage;
    
    private static Bitmap _weekdayBgImages;
    private static Bitmap _amPmBgImages;
    private static Bitmap _alarmBgBellImage; 
    private static Bitmap _bgImage;
    private static Bitmap _bgSparceImage;
    
    private static Bitmap[] _numImageList;
    
    private static int _digitHeight;
    private static int _digitWidth;  
    private static int _weekdayWidth;
    private static int _bellWidth;
    private static int _amPmWidth; 
    private static int _amOffset; // AM offset within the big image
    private static int _pmOffset;  // PM offset with in the big image 
    private static int _colonWidth; 
    private static int _orientation;
    
    private static int _color;
    private int _bkgroundType;
    
  //  private boolean _militaryTime;
    private boolean _alarmOn;
    
    private static int _screenSize;
        
    static final int YGAP = Display.getHeight()/40; //
    static final int XGAP = Display.getWidth()/60; //
    
    private static boolean _useSingleFile = false; // this is used when all the numbers are in one image file
    
    static {
        // for separate number images
        _numImageList = new Bitmap[10];
        
        _screenSize = Display.getHeight() * Display.getWidth(); 
    }
    
    void configScreen() {
        _orientation = Display.getOrientation();
        if(_screenSize == 320*240 || (_screenSize == 480*360 && Display.getWidth() == 360) || _screenSize == 400*360) { 
            // if it is 320*240 or when touch screen is put on vertical mode, use the 320*240 package
            _moduleName = MODULE_NAME_PREFIX + MODULE_NAME_SUFIX_LOW;
            _digitWidth = 53;
            _weekdayWidth = 41;
            //_amPmWidth = 48;
            _amPmWidth = 28;
            _amOffset = 19;
            _pmOffset = 67;
            _colonWidth = 28;       
        }
        else if(_screenSize == 480*320 || _screenSize == 480*360 ||(_screenSize == 480*640 && Display.getWidth() == 480)  ) 
        {
            // for 480*320 and 480*360, use the 480*320 module
            _moduleName = MODULE_NAME_PREFIX + MODULE_NAME_SUFIX_HIGH;
            _digitWidth = 80;
            _weekdayWidth = 61;
            //_amPmWidth = 77;   
             _amPmWidth = 46;
            _amOffset = 29;
            _pmOffset = 107;
            _colonWidth = 46;            
        }     
        else {
            // 640*480 landscape
            _moduleName = MODULE_NAME_PREFIX + MODULE_NAME_SUFIX_SUPER;
            _digitWidth = 53*2;
            _weekdayWidth = 41*2;
            //_amPmWidth = 48;
            _amPmWidth = 28*2;
            _amOffset = 19*2;
            _pmOffset = 69*2;
            _colonWidth = 28*2;                  
        }
    }
    
    DigitalClockFaceField(MusicalClockSettingItem settingItem, boolean militaryTime, int color) {
        super(settingItem, militaryTime);
        //_militaryTime = militaryTime;
        _color = color;
        //for test only
        //_color = GREEN_COLOR;
        configScreen();
         
    }
    
    static void loadImages(int color) {
        _color = color;
        //for test only
       // _color = GREEN_COLOR;
        String fileId = "lemon";
        switch(_color) {
            case LEMON_COLOR:
                fileId = "lemon";
                break;
            /*case BLUE_COLOR:
                fileId = "blue";      
                break;*/
            case SKY_COLOR:
                fileId = "sky";        
                break;
            case ORANGE_COLOR:
                fileId = "orange";            
                break;
            case PINK_COLOR:
                fileId = "pink";      
                break;
            /*case PURPLE_COLOR:
                fileId = "purple";         
                break;*/
            case GREEN_COLOR:
                fileId = "g";
                break;
            /*case WHITE_COLOR:
                fileId = "w";
                break;       */         
            default:
                fileId = "sky";
                break;                                                       
        }

        _weekdayImages = Bitmap.getBitmapResource(_moduleName, "Week_" + fileId + IMAGE_FILE_SUFIX);
        _amPmImages = Bitmap.getBitmapResource(_moduleName, "AmPm_" + fileId + IMAGE_FILE_SUFIX);
        _alarmBellImage = Bitmap.getBitmapResource(_moduleName, "Alarm_" + fileId + IMAGE_FILE_SUFIX);
        
        _weekdayBgImages = Bitmap.getBitmapResource(_moduleName, "Week_" + fileId + BG_IMAGE_ID + IMAGE_FILE_SUFIX);
        _amPmBgImages = Bitmap.getBitmapResource(_moduleName, "AmPm_" + fileId + BG_IMAGE_ID + IMAGE_FILE_SUFIX);
        
        _bgImage = Bitmap.getBitmapResource(_moduleName, "BG_carbon.png");
        _bgSparceImage =  Bitmap.getBitmapResource(_moduleName, "BG_sparce.png");
        //_bgSparceImage =  Bitmap.getBitmapResource(_moduleName, "carbon-fibre-pattern.jpg");
        if(_useSingleFile) {
            _numImages = Bitmap.getBitmapResource(_moduleName, "NUM_" + fileId + IMAGE_FILE_SUFIX);
            _digitHeight = _numImages.getHeight();
        }
        else {
            for(int i=0;i<_numImageList.length;i++) {
                _numImageList[i] = Bitmap.getBitmapResource(_moduleName, fileId + "_" + i + IMAGE_FILE_SUFIX);
            }
            _colonImage =  Bitmap.getBitmapResource(_moduleName, fileId + "_colon" + IMAGE_FILE_SUFIX);
            _colonWidth = _colonImage.getWidth();
            _digitHeight = _numImageList[0].getHeight();
        }
    }

    public void setAlarmOn(boolean alarmOn) {
        _alarmOn = alarmOn;
    }

    public void setBackGroundType(int bkgroundType) {
        _bkgroundType = bkgroundType;
    }
    
    public static String getModuleName() {
        return _moduleName; 
    }
    
    // calculate the x margin for the time display 
    int calculateDigitalXoffset(int digitImageWidth, int colonImageWidth) {
        int imageWidth;        // the width of the time images
        if(_hour >=10 || _militaryTime) {
            imageWidth = 4*digitImageWidth + colonImageWidth;
        }
        else {
            imageWidth = 3*digitImageWidth + colonImageWidth;
        }
        
        if(!_militaryTime) {
            imageWidth += _amPmWidth;
        }
        
        int xMargin = (getPreferredWidth() -  imageWidth)/2;
        return (xMargin > 0)? xMargin:0;
    }
    
    /**
     * This method is for the number images in one sigle file in the sequence 1,2,3,4,5,6,7,8,9,0,:
     * @param i <description>
     * @return <description>
     */
    private int getDigitIndex(int i) {
        if(i>0) {
            return i-1;
        } 
        else {
            return 9; // the index of digit 0 in the image file(index from 0 to 9. digit '0' in the image is the 10th)
        }
    }
    
    // this is used when all the numbers are in one image file
    protected int paintTimeWithOneFile(Graphics graphics, int xOffset, int yOffset) {
        int digit1X = getDigitIndex(_hour/10)*_digitWidth;
        int digit2X = getDigitIndex(_hour%10)*_digitWidth;
        int digit3X = getDigitIndex(_minute/10)*_digitWidth;
        int digit4X = getDigitIndex(_minute%10)*_digitWidth; 
        int colonX  = 10*_digitWidth;  
        
        // draw numbers
        if(_hour/10 > 0 || _militaryTime) {
            graphics.drawBitmap(xOffset,yOffset,_digitWidth,_numImages.getHeight(), _numImages, digit1X, 0);            
            xOffset += _digitWidth;
        }
        
        graphics.drawBitmap(xOffset,yOffset, _digitWidth, _numImages.getHeight(), _numImages, digit2X, 0);
        xOffset += _digitWidth;
        
        graphics.drawBitmap(xOffset,yOffset, _colonWidth, _numImages.getHeight(), _numImages, colonX, 0);
        xOffset += _colonWidth;       
                
        graphics.drawBitmap(xOffset,yOffset, _digitWidth, _numImages.getHeight(), _numImages, digit3X, 0);
        xOffset += _digitWidth;

        graphics.drawBitmap(xOffset,yOffset, _digitWidth, _numImages.getHeight(), _numImages, digit4X, 0);
        xOffset += _digitWidth; 
        
        return xOffset;                
    }

    // this is used when all the numbers are in separate image files
    protected int paintTimeWithSeparateFiles(Graphics graphics, int xOffset, int yOffset) {
        Bitmap digit1 = _numImageList[_hour/10];
        Bitmap digit2 = _numImageList[_hour%10];
        Bitmap digit3 = _numImageList[_minute/10];
        Bitmap digit4 = _numImageList[_minute%10];
        
        if(_hour/10 > 0 || _militaryTime) {
            graphics.drawBitmap(xOffset,yOffset,digit1.getWidth(),digit1.getHeight(), digit1, 0, 0);            
            xOffset += digit1.getWidth();
        }
        
        graphics.drawBitmap(xOffset,yOffset,digit2.getWidth(),digit1.getHeight(), digit2, 0, 0);            
        xOffset += digit2.getWidth();
        
        graphics.drawBitmap(xOffset,yOffset, _colonImage.getWidth(), _colonImage.getHeight(), _colonImage, 0, 0);
        xOffset += _colonImage.getWidth();       
                
        graphics.drawBitmap(xOffset,yOffset,digit3.getWidth(),digit3.getHeight(), digit3, 0, 0);            
        xOffset += digit3.getWidth();

        graphics.drawBitmap(xOffset,yOffset,digit4.getWidth(),digit3.getHeight(), digit4, 0, 0);            
        xOffset += digit4.getWidth();
        
        return xOffset;      
    }
        
    protected void paintDigital(Graphics graphics) {        
        
        int xOffset = 0;
        int bgWidth = _bgImage.getWidth();
        int bgHeight = _bgImage.getHeight();
        
        /*boolean bgOn = false;
        if(bgOn) {
            for(int i=0;i<Display.getWidth()/bgWidth;i++) {
                graphics.drawBitmap(xOffset, 0,bgWidth,bgHeight, _bgImage, 0, 0);
                xOffset += bgWidth;
            }
        }*/
        
        // for test only
        //_militaryTime = true;
        //_alarmOn = true;
        
        // calculate the x margin for the time images
        xOffset = calculateDigitalXoffset(_digitWidth,_colonWidth);
       
        // calculate top margine
        int yOffset = this.getPreferredHeight() -  _digitHeight -  _weekdayImages.getHeight() - _alarmBellImage.getHeight() - YGAP*2; // 10;
//#ifdef FREE_VERSION
        yOffset -= AD_HEIGHT;
//#endif        
        yOffset = (yOffset > 0)?yOffset/2:0; 
                        
        // draw the bell if the alarm is set
        if(_alarmOn) {
            //graphics.drawBitmap(xOffset,yOffset,_alarmBellImage);                       
            graphics.drawBitmap(xOffset + _digitWidth/5,yOffset,_alarmBellImage.getWidth(),_alarmBellImage.getHeight(), _alarmBellImage, 0, 0);            
            
        }
        yOffset += (_alarmBellImage.getHeight() + YGAP);
        
        if(_useSingleFile) {
            // draw the numbers stored in a signle image
            xOffset = paintTimeWithOneFile(graphics, xOffset, yOffset);
        }
        else {
            // draw the numbers using separate images
            xOffset = paintTimeWithSeparateFiles(graphics, xOffset, yOffset);
        }
       
        xOffset += XGAP;
        
        if(!_militaryTime) {
            // draw the AM/PM. The yOffset needs to be adjusted
            int amY = yOffset;
            int pmY = yOffset + (_digitHeight - _amPmImages.getHeight());
            if(_isAm) {
                graphics.drawBitmap(xOffset,amY, _amPmWidth,_amPmImages.getHeight(), _amPmImages, _amOffset, 0);
                graphics.drawBitmap(xOffset,pmY, _amPmWidth,_amPmImages.getHeight(), _amPmBgImages, _pmOffset , 0);
            }
            else {
                graphics.drawBitmap(xOffset,amY, _amPmWidth,_amPmBgImages.getHeight(), _amPmBgImages, _amOffset, 0);
                graphics.drawBitmap(xOffset,pmY, _amPmWidth,_amPmImages.getHeight(), _amPmImages, _pmOffset, 0);            
            }
        }
        
        yOffset+=(_digitHeight + YGAP);
        //yOffset+=_amPmImages.getHeight();
                    
        // draw the week day
        int dayOffset = 0;
        xOffset = (Display.getWidth() - _weekdayBgImages.getWidth())/2;
        for(int i=0;i<7;i++) {
            if(i== _dayOfWeek - 1) {
                graphics.drawBitmap(xOffset,yOffset, _weekdayWidth,_weekdayImages.getHeight(), _weekdayImages, dayOffset, 0);
            }
            else {
                graphics.drawBitmap(xOffset ,yOffset, _weekdayWidth,_weekdayBgImages.getHeight(), _weekdayBgImages, dayOffset, 0);
            }
            xOffset += _weekdayWidth;
            dayOffset += _weekdayWidth;
        }      
        
    }
    /**
     * Paint
     * @param graphics <description>
     */
    protected void paint(Graphics graphics) {
        if (_orientation != Display.getOrientation()) {
           // configScreen();
            //loadImages(_color);
        }      
        
        // paint backgound first
        if(this._bkgroundType == BACKGROUND_TEXTURED ||_bkgroundType == BACKGROUND_SPARCE ) {
            int xOffset = 0;

            Bitmap bgImg = _bgImage;
            if(this._bkgroundType == BACKGROUND_SPARCE) {
                bgImg = _bgSparceImage;
            }
            int bgWidth = bgImg.getWidth();
            int bgHeight = bgImg.getHeight(); 
            
            // calculate how many rows of the background image needs to be drawn. THe background image is smaller than the screen to save
            // space and also provide flexibility to fit different screen sizes. For example a 80*10 can be used for all screen sizes bby drawing multiple rows and columns of it
            // to fill both 320*240, 480*320 and 480*360
            int bg_rows = getPreferredHeight()/bgHeight;
            if(getPreferredHeight()%bgHeight > 0) {
                bg_rows++;
            }
            
            int bg_colums = getPreferredWidth()/bgWidth; 
            if(getPreferredWidth()%bgWidth > 0) {
                bg_colums++;
            }        
            for(int j=0;j< bg_rows; j++) {
                xOffset = 0;
                for(int i=0;i<bg_colums;i++) {
                    graphics.drawBitmap(xOffset, j*bgHeight,bgWidth,bgHeight, bgImg, 0, 0);
                    xOffset += bgWidth;
                }                
            }
        } else {
            int color = Color.BLACK;
            /*
            if(this._bkgroundType == BACKGROUND_WHITE) {
                color = Color.WHITE;
            }*/
            graphics.setBackgroundColor(color);
            graphics.clear();       
        }
        
        // paint the clock
        paintDigital(graphics);
    }
    
    public int getPreferredWidth() {
        return Display.getWidth();
    }
    public int getPreferredHeight() {
//#ifdef FREE_VERSION
        return  Display.getHeight() - AD_HEIGHT; // minus the ad height
//#else        
        return Display.getHeight();
//#endif        
    } 
    protected void layout(int width, int height) {
        setExtent(getPreferredWidth(),getPreferredHeight()); 
    }
   
//ifdef VER_4_7_0_AND_ABOVE 
 /*   protected void sublayout(int width, int height) {
        if (_orientation != Display.getOrientation()) {
            configScreen();
            loadImages(_color);
            
             if (myDirection != Display.getOrientation()) {
                 this.deleteAll();
                 this.invalidate();
                 populateScreen();
            }
        }
        super.sublayout(Display.getWidth(), Display.getHeight());
    }*/
//endif    
} 
