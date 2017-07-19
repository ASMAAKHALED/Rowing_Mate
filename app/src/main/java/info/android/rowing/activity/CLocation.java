package info.android.rowing.activity;

/**
 * Created by mahmoud_mashal on 2/5/2017.
 */

import android.location.Location;

public class CLocation extends Location {



    public CLocation(Location location) {
        // TODO Auto-generated constructor stub
        super(location);
    }

    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        float nDistance = super.distanceTo(dest);
        return nDistance;
    }

    @Override
    public float getAccuracy() {
        // TODO Auto-generated method stub
        float nAccuracy = super.getAccuracy();
        return nAccuracy;
    }

    @Override
    public double getAltitude() {
        // TODO Auto-generated method stub
        double nAltitude = super.getAltitude();
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        // TODO Auto-generated method stub
        float nSpeed = super.getSpeed();
        return nSpeed;
    }

    @Override
    public double getLongitude() {
        // TODO Auto-generated method stub
        double nLong = super.getLongitude();
        return nLong;
    }

    @Override
    public double getLatitude() {
        // TODO Auto-generated method stub
        double nLat = super.getLatitude();
        return nLat;
    }

}
