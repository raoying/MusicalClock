/*
 * KtQuerryDialog.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.decor.*; 

public final class KtQuerryDialog extends Dialog
{
    private EditField entryField;

    public KtQuerryDialog(String title, String text)
    {
        this(title, text, EditField.EDITABLE);
    }
    
     public KtQuerryDialog(String title, String text, long style) {
        //super(Dialog.D_OK, title, 1, Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION), Manager.FOCUSABLE);
        super(Dialog.D_OK, title, 1, null, Manager.FOCUSABLE);
        if(text != null) {
            entryField = new EditField("", text, 50,style);
            entryField.setCursorPosition(text.length() -1 );
        }
        else {
            entryField = new EditField("", "", 50, style);
        }
        Border border = BorderFactory.createRoundedBorder(new XYEdges(5, 5, 5, 5), 0xffffffff, Border.STYLE_SOLID);
        entryField.setBorder(border);
        add(entryField);
     }

    public String getText() {
        return entryField.getText();
    }
    public boolean keyChar(char key, int status, int time)
    {
        //Override key commands

        switch (key)
        {
            case Characters.ENTER:
                //Update main class with the entered text.
                //DialogDataEntry.setText(entryField.getText());
                this.close();
                break;
                //Override the backspace key to delete the last character.
            case Characters.BACKSPACE:
                if (entryField.getTextLength() > 0)
                {
                    entryField.setText(entryField.getText().substring(0, entryField.getTextLength() - 1));
                }
                break;
                //Override the escape key to delete the last character.
           /* case Characters.ESCAPE:
                if (entryField.getTextLength() > 0)
                {
                    entryField.setText(entryField.getText().substring(0, entryField.getTextLength() - 1));
                }
                break;*/
                //Enter the character the user typed into the EditField.
            default:
                entryField.setText(entryField.getText() + key);
                break;
        }
        return true;
    }
}
