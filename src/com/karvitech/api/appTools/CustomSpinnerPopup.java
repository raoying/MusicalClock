/*
 * CustomSpinnerPopup.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

  /**
     * A PopupScreen to display a TextSpinBoxField
     */
    public final class CustomSpinnerPopup extends PopupScreen     
    {
        private TextSpinBoxField _spinBoxField;
        private boolean _isSet;        
        
        /**
         * Creates a new CustomSpinnerPopup object
         */
        public CustomSpinnerPopup(String title, String[] choices, int rowNum, int selectedIndex)
        {           
            super(new VerticalFieldManager(), Screen.DEFAULT_CLOSE);                       
            _spinBoxField = new TextSpinBoxField(choices);
            _spinBoxField.setSelectedIndex(selectedIndex);
            _spinBoxField.setVisibleRows(rowNum);            
            add(new LabelField(title));
            add(new SeparatorField());
            HorizontalFieldManager hfm = new HorizontalFieldManager(Field.FIELD_HCENTER);
            hfm.add(_spinBoxField);
            add(hfm);            
        }
        
        public void doModal() {
            UiApplication.getUiApplication().pushModalScreen(this);
        }
        
        /**
         * Retrieves the currently selected choice
         * @return The currently selected choice
         */
        public String getChoice()
        {
            return (String)_spinBoxField.get(_spinBoxField.getSelectedIndex());
        }
        
        /**
         * Retrieves the currently selected choice
         * @return The currently selected choice
         */
        public int getSelectedIndex()
        {
            return _spinBoxField.getSelectedIndex();
        }
              
        /**
         * Indicates whether the TextSpinBoxField has changed from
         * its initial state.
         * @return True if the selected choice has been modified, otherwise false
         */
        public boolean isSet()
        {
            return _isSet;
        }        
        
        
        /**
         * @see Screen#invokeAction(int)
         */
        protected boolean invokeAction(int action)
        {
            if(action == ACTION_INVOKE)
            {
                if(!_isSet)
                {                    
                    _isSet = true;
                }                
                close();
                return true;
            }
            return false;
        }    
        
        
        /**
         * @see Screen#close()
         */
        public void close()
        {
            if(!_isSet)
            {                
                _spinBoxField.setSelectedIndex(0);
            }
            super.close();
        }  
    }    

