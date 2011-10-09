/*
 * MediaPlayer.java
 *
 * © Karvi Technology Inc., 2009-2014
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;

import java.io.*;
import java.util.*;
import javax.microedition.media.*; 
import javax.microedition.media.control.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;


/**
 * Alarm media player.
 */
public final class AlarmMediaPlayer implements PlayerListener
{
    public static final int PLAY_ONCE = 0;
    public static final int PLAY_LOOP = 1;
        
    private AlarmPlayerListener _listener;
    private Player _player;
    private VolumeControl _volumeControl;
    private int _playMode;  
    private Vector _playList;
    private int _playListIndex = 0; // the file currently being played
    private int _volume = 70;
    
    private static AlarmMediaPlayer _instance;  
    
    
    public static  AlarmMediaPlayer getInstance() {
        if(_instance == null) {
            _instance = new AlarmMediaPlayer();
        }
        return _instance;
    }
    // Constructor
    private AlarmMediaPlayer()
    {     
    }
    
    public void setListener(AlarmPlayerListener listener) {
        _listener = listener;
    }
    
    public void close() {
        if(_player != null) {
            _player.close();
            _player = null;
        }
        _listener = null;
        _instance = null;        
    }
    
    public void setPlayMode(int mode) {
        _playMode = mode;
        if(_playMode == PLAY_LOOP && _player != null) {
            _player.setLoopCount(-1); // play forever        
        }
    }
    
    
    public void setPlayList(Vector playList, int startIndex) {
        _playList = playList;
        _playListIndex = startIndex; // the start index of the file in the list
    }
    
    public void setVolume(int volume) {
        if(volume > 100 || volume < 0) {
            _volume = 70;
            return;
        }
        else {
            _volume = volume;
        }
    } 
    /**
     * Creates a Player based on a specified URL and provides a
     * VolumeControl object.
     */
    public boolean initializeMedia(String uri)
    {   
        return initializeMedia(uri, false);
    }
    public boolean initializeMedia(String uri, boolean createUsingIs)
    {         
        try
        {   
            /* For the purpose of this sample we are supplying a URL to a media file
            that is included in the project itself. 
            See the javax.microedition.media.Manager javadoc for more information on 
            handling data residing on a server.
            */
            //InputStream is = getClass().getResourceAsStream(uri);
            //_player = javax.microedition.media.Manager.createPlayer(is, "video/mp4");
            //String uri = "file:///SDCard/BlackBerry/music/alarm.mp3";
            if(createUsingIs) {
                InputStream is = UiApplication.getApplication().getClass().getResourceAsStream(uri);
                _player = javax.microedition.media.Manager.createPlayer(is, "audio/mpeg"); 
            }
            else {
                _player = javax.microedition.media.Manager.createPlayer(uri);   
            }  
            
            _player.addPlayerListener(this);            
            _player.realize();
            
            // if there is only one file and play mode is loop then play forever            
            if(_playMode == PLAY_LOOP && _playList == null) {
                _player.setLoopCount(-1); // play forever
            }
           
                
            
  //          VideoControl vc = (VideoControl) _player.getControl("VideoControl");
     
            
            _volumeControl = (VolumeControl) _player.getControl("VolumeControl"); 
            return true;  
                    
        }
        catch(Exception pe)
        {
            System.out.println("initializeMedia exception:" + pe.toString() + ":" + pe.getMessage());
            return false;
        }
       /* catch (IOException ioe)
        {
            System.out.println(ioe.toString());
        }*/        
    }   

    private boolean findNextMediaFile() {
         // go through the list to find the first valid media file, if none found then return false;
         boolean mediaInitialized = false;
         for(int i=0;i<_playList.size();i++) {
             mediaInitialized = initializeMedia("file:///" + _playList.elementAt(_playListIndex).toString());
             if(mediaInitialized) {
                  //first valid media file
                  return true;
             }
             else {
                 // failed to initialize, try the next one
                 _playListIndex++;
                 _playListIndex %= _playList.size();
            }
         }
         return false;
    }
    
    public boolean start() {
            try
            { 
                boolean mediaInitialized = false;
                    
                // Start/resume the media player.
                if(_playList != null) {
                    mediaInitialized = findNextMediaFile();
                    if(!mediaInitialized) {
                        return false;
                    }
                }
                
                VolumeControl volumeControl = (VolumeControl) _player.getControl("VolumeControl");
                volumeControl.setLevel(_volume);              
                _player.start();
                return true;
                
                //_timerUpdateThread = new TimerUpdateThread();
                //_timerUpdateThread.start();
            }
            catch(Exception pe)
            {                
                System.out.println("AlarmMediaPlayer.(start()" + pe.toString());
                return false;
            }
    }
    
    public void stop() {
            try
            {
                // Stop/pause the media player.
                _player.stop();             
              //  _timerUpdateThread.stop();
            }
            catch(Exception pe)
            {
                System.out.println(pe.toString());
            }        
    }        
    
    /**
     * @see net.rim.device.api.ui.Screen#keyControl(char,int,int)
     */
    protected boolean keyControl(char c, int status, int time)
    {        
        // Capture volume control key press and adjust volume accordingly.
     /*   switch( c )
        {
            case Characters.CONTROL_VOLUME_DOWN:
                _volumeControl.setLevel(_volumeControl.getLevel() - 10);          
                return true;
                
            case Characters.CONTROL_VOLUME_UP:
                _volumeControl.setLevel(_volumeControl.getLevel() + 10);                           
                return true;
        }*/
        return false;
        //return super.keyControl( c, status, time );
    }    
    
    /**
     * @see javax.microedition.media.PlayerListener#playerUpdate(Player,String,Object)
     */
    public void playerUpdate(Player player, final String event, Object eventData)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            public void run()
            {
               /* if (event.equals(VOLUME_CHANGED))
                {
                    _volumeDisplay.setText("Volume : " + _volumeControl.getLevel());
                }
                else if (event.equals(STARTED ))
                {
                   // _currentTime.setText(" ");
                   //_controlButton.setLabel("Pause");      
                }    
                else if (event.equals(STOPPED))
                {
                    _currentTime.setText(_player.getMediaTime()/1000000 + "");
                    _controlButton.setLabel("Start");              
                }                       
                else if (event.equals(DURATION_UPDATED))
                {
                    _duration.setText(_player.getDuration()/1000000 + " s");       
                }           */
                
                // play again
                if (event.equals(END_OF_MEDIA) && _playMode == PLAY_LOOP)
                {
                    if(!isPlayListEmpty()) {
                        _playListIndex++;
                        _playListIndex %= _playList.size();
                        findNextMediaFile();
                        //initializeMedia("file:///" + _playList.elementAt(_playListIndex).toString());
                        
                        // notify the listener of the new play list index
                        if(_listener != null) {
                            _listener.PlayerUpdate(event, new Integer(_playListIndex));
                        }       
                        
                        // play the next        
                        try {
                            _player.start();    
                        }
                        catch (Exception e) {
                            System.out.println("Replay failed " + e.toString());
                        }
                    }         
                }
            }           
        });
    }

    private boolean isPlayListEmpty() {
        if(_playList != null && _playList.size() > 0) {
            return false;
        }
        return true;
    }
       
    // used to update progress
    private class TimerUpdateThread extends Thread
    {
        private boolean _threadCanRun;

        public void run()
        {
            _threadCanRun = true;
            while( _threadCanRun ) {
                UiApplication.getUiApplication().invokeLater( new Runnable() {
                    public void run()
                    {
                        //_currentTime.setText( _player.getMediaTime() / 1000000 + "" );
                    }
                } );
                
                try {
                    Thread.sleep( 500L );
                } catch( InterruptedException e ) {
                }
            }
        }

        public void stop()
        {
            _threadCanRun = false;
        }
    }
}    

