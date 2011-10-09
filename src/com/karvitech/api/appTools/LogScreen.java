/*
 * LogScreen.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */


package com.karvitech.api.appTools;
import java.util.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
/**
 * 
 */
class LogScreen extends MainScreen implements FocusChangeListener {
    
    private MenuItem _clearMenuItem = new MenuItem("Clear Logs", 100, 10)
    {
        public void run()
        {
            LogScreen.this.deleteAll();
            LogHistory.getInstance().clearHistory();
            LogHistory.getInstance().saveLogs(); // save the logs
            LogScreen.this.loadLogs();
            LogScreen.this.invalidate();
            
        }
    };     
    
    public static void showScreen() {
        ((UiApplication)(Application.getApplication())).pushScreen(new LogScreen());
    }

    private LogScreen() {
        this.setTitle("Reboot Logs");
        loadLogs();
    }

    private void loadLogs() {
        VerticalFieldManager vfm = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR | USE_ALL_WIDTH | USE_ALL_HEIGHT);      
        Vector logs = LogHistory.getInstance().getAllEntrys();
        for(int i=logs.size(); i> 0; i--) {
            /*LabelFieldBkgrdcolor lf = new LabelFieldBkgrdcolor((String)logs.elementAt(i-1));
            lf.setColor(i%2 == 0? Color.SILVER : Color.LIGHTGREY); */
            
            LabelFieldBkgrdcolor lf = new LabelFieldBkgrdcolor((String)logs.elementAt(i-1));
            lf.setColor(i%2 == 0? Color.WHITE : Color.LIGHTGREY); 
            lf.setFocusListener(this);
            vfm.add(lf);
        }
        if(logs.size() == 0) {
            vfm.add(new LabelField("No log record."));
        }
        add(vfm);
    }
    
    protected void makeMenu(Menu menu, int instance) {
        menu.add(_clearMenuItem);
    }    
 
    // implementing focusChangeListener
    public void focusChanged(Field field, int eventType)
    {
        if(field instanceof LabelFieldBkgrdcolor)
        {
            LabelFieldBkgrdcolor lf = (LabelFieldBkgrdcolor)field;
            if(lf.isFocus())
            {
                 this.invalidate();
            }
        }
    }     
 
    public class RichTexFieldBkgrdcolor extends RichTextField {
        private int _color;
        public RichTexFieldBkgrdcolor(String text) {
            super(text, Field.USE_ALL_WIDTH);
        }
        
        public void setColor(int color) {
            _color = color;
        }
        public void paint(Graphics graphics)
        {           
            // Sets the BackgroundColor
            if(this.isFocus()) {
                graphics.setBackgroundColor(Color.BLUE);
            }
            else {    
                graphics.setBackgroundColor(_color);
            }
                    
            // Clears the entire graphic area to the current background
            graphics.clear();
            super.paint(graphics);                    
        }    
    }
                
    public class LabelFieldBkgrdcolor extends LabelField {
        private int _color;
        public LabelFieldBkgrdcolor(String text) {
            super(text, Field.FOCUSABLE|Field.USE_ALL_WIDTH);
        }
        
        public void setColor(int color) {
            _color = color;
        }
        public void paint(Graphics graphics)
        {           
            // Sets the BackgroundColor
            if(this.isFocus()) {
                graphics.setBackgroundColor(Color.DODGERBLUE);
            }
            else {    
                graphics.setBackgroundColor(_color);
            }
                    
            // Clears the entire graphic area to the current background
            graphics.clear();
            super.paint(graphics);                    
        }    
    }
} 




