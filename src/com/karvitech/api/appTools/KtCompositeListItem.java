package com.karvitech.api.appTools;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class KtCompositeListItem extends HorizontalFieldManager implements FieldChangeListener {
    public static int INDICATOR_NONE = 0;
    public static int INDICATOR_CHECK = 1;
    public static int INDICATOR_ACTION = 2;
    
	private Field				 _indicator;
	private KtListItem           _mainItem; // the main Item, with title and detailed text
	private int 				_indicatorType;
	public KtCompositeListItem(String title, String details, long style) {
		_mainItem = new KtListItem(title, details, style);
		_indicator = new KtCheckbox(style);
		Screen sr = UiApplication.getUiApplication().getActiveScreen();
		int screenWidth = sr.getWidth();
		int mainItemWidth = screenWidth - _indicator.getPreferredWidth();
		_mainItem.setPreferedWidth(mainItemWidth);
		
		_mainItem.setChangeListener(this);
		_indicator.setChangeListener(this);
		add(_mainItem);
		add(_indicator);
		_indicatorType = INDICATOR_NONE;
	}
	public void setSwitchOn(boolean on) {
		if(_indicator instanceof KtCheckbox) {
			((KtCheckbox) _indicator).setSwitchOn(on);
		}
	}
   /* protected  boolean navigationClick(int status, int time)  {
    	if(_mainItem.isFocus()) {
    		this.fieldChangeNotify(0);
    	} else if(_indicator.isFocus()) {
    		if(_indicator instanceof KtCheckbox) {
    			KtCheckbox chk = (KtCheckbox)_indicator;
    			chk.setSwitchOn(!chk.getSwitchState());
    		}
    		this.fieldChangeNotify(1);
    	}
    	return false;
    }   */
	public void fieldChanged(Field field, int context) {
		if(field == _mainItem) {
			fieldChangeNotify(0);
		}
		else {
			fieldChangeNotify(1);
		}
		
	}
}
