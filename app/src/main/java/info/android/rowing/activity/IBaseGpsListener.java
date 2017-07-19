package info.android.rowing.activity;

/**
 * Created by mahmoud_mashal on 2/5/2017.
 */

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;

public interface IBaseGpsListener extends LocationListener, GpsStatus.Listener {

    public void onLocationChanged(Location location);

    public void onProviderDisabled(String provider);

    public void onProviderEnabled(String provider);

    public void onStatusChanged(String provider, int status, Bundle extras);

    public void onGpsStatusChanged(int event);

    void onDataChange(DataSnapshot dataSnapshot);
}
