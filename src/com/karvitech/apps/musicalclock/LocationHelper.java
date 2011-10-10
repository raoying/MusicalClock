package com.karvitech.apps.musicalclock;

import java.util.Vector;

import javax.microedition.location.*;

import net.rim.device.api.gps.*;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class LocationHelper {
    // Represents the number of updates over which altitude is calculated, in seconds
    private static final int GRADE_INTERVAL = 5;
	private static int _interval = 1;
	
	private LocationProvider _locationProvider;
    private static Vector _previousPoints;
    private static float[] _altitudes;
    private static float[] _horizontalDistances;
    private double _latitude;
    private double _longitude;
    private float _wayHorizontalDistance;
    private float _horizontalDistance;
    private float _verticalDistance;
    /**
     * Invokes the Location API with Standalone criteria
     * 
     * @return True if the <code>LocationProvider</code> was successfully started, false otherwise
     */
    private boolean startLocationUpdate()
    {
        boolean returnValue = false;

        if(GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_AUTONOMOUS))
        {
            try
            {
                Criteria criteria = new Criteria();
                criteria.setCostAllowed(false);

                _locationProvider = LocationProvider.getInstance(criteria);

                if(_locationProvider != null)
                {
                    /*
                     * Only a single listener can be associated with a provider,
                     * and unsetting it involves the same call but with null.
                     * Therefore, there is no need to cache the listener
                     * instance request an update every second.
                     */
                    _locationProvider.setLocationListener(new LocationListenerImpl(), _interval, -1, -1);
                    returnValue = true;
                }
                else
                {
                    UiApplication.getUiApplication().invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            Dialog.alert("Failed to obtain a location provider, exiting...");
                            System.exit(0);
                        }
                    });
                }

            }
            catch(final LocationException le)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Dialog.alert("Failed to instantiate LocationProvider object, exiting..." + le.toString());
                        System.exit(0);
                    }
                });
            }
        }
        else
        {
            UiApplication.getUiApplication().invokeLater(new Runnable()
            {
                public void run()
                {
                    Dialog.alert("GPS autonomous/standalone mode is not supported on this device, exiting...");
                    System.exit(0);
                }
            });
        }

        return returnValue;
    }
    
    /**
     * Implementation of the LocationListener interface. Listens for updates to
     * the device location and displays the results.
     */
    private class LocationListenerImpl implements LocationListener
    {

        /**
         * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,Location)
         */
        public void locationUpdated(LocationProvider provider, Location location)
        {
            if(location.isValid())
            {
                float heading = location.getCourse();
                _longitude = location.getQualifiedCoordinates().getLongitude();
                _latitude = location.getQualifiedCoordinates().getLatitude();
                float altitude = location.getQualifiedCoordinates().getAltitude();
                float speed = location.getSpeed();

                // Horizontal distance for current Location
                float horizontalDistance = speed * _interval;
                _horizontalDistance += horizontalDistance;

                // Horizontal distance for WayPoint
                _wayHorizontalDistance += horizontalDistance;

                // Distance over the current interval
                float totalDist = 0;

                // Moving average grade
                for(int i = 0; i < GRADE_INTERVAL - 1; ++i)
                {
                    _altitudes[i] = _altitudes[i + 1];
                    _horizontalDistances[i] = _horizontalDistances[i + 1];
                    totalDist = totalDist + _horizontalDistances[i];
                }

                _altitudes[GRADE_INTERVAL - 1] = altitude;
                _horizontalDistances[GRADE_INTERVAL - 1] = speed * _interval;
                totalDist = totalDist + _horizontalDistances[GRADE_INTERVAL - 1];
                float grade = (totalDist == 0.0F) ? Float.NaN : ((_altitudes[4] - _altitudes[0]) * 100 / totalDist);

                // Running total of the vertical distance gain
                float altGain = _altitudes[GRADE_INTERVAL - 1] - _altitudes[GRADE_INTERVAL - 2];

                if(altGain > 0)
                {
                    _verticalDistance = _verticalDistance + altGain;
                }

                // Information to be displayed on the device
                StringBuffer sb = new StringBuffer();
                sb.append("Longitude: ");
                sb.append(_longitude);
                sb.append("\n");
                sb.append("Latitude: ");
                sb.append(_latitude);
                sb.append("\n");
                sb.append("Altitude: ");
                sb.append(altitude);
                sb.append(" m");
                sb.append("\n");
                sb.append("Heading relative to true north: ");
                sb.append(heading);
                sb.append("\n");
                sb.append("Speed : ");
                sb.append(speed);
                sb.append(" m/s");
                sb.append("\n");
                sb.append("Grade : ");
                if(Float.isNaN(grade))
                {
                    sb.append(" Not available");
                }
                else
                {
                    sb.append(grade + " %");
                }
                
            }
        }


        /**
         * @see javax.microedition.location.LocationListener#providerStateChanged(LocationProvider, int)
         */
        public void providerStateChanged(LocationProvider provider, int newState)
        {
            if(newState == LocationProvider.TEMPORARILY_UNAVAILABLE)
            {
                provider.reset();
            }
        }
    }

}
