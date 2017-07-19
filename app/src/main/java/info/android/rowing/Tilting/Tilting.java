package info.android.rowing.Tilting;

import android.hardware.SensorEvent;
import android.widget.TextView;

import info.android.rowing.R;

import static android.R.attr.gravity;

/**
 * Created by Shorouk Ramzi on 7/3/2017.
 */

public class Tilting {
    private double acc_x_t=0, acc_y_t=0, acc_z_t=0;
    float[] inclineGravity = new float[3];
    float[] mGravity;
    float[] mAcc;
    float[] inclineTotal = new float[3];

    float[] gravity= new float[3];

    public int TiltMeasure(double x, double y , double z, SensorEvent sensorEvent ) {
       /* double norm_Of_g = Math.sqrt(x * x+ y * y + z * z);
        acc_x_t = x;
        acc_y_t = y;
        acc_z_t = z;
        // Normalize the accelerometer vector

        acc_x_t = acc_x_t / norm_Of_g;
        acc_y_t = acc_y_t / norm_Of_g;
        acc_z_t = acc_z_t / norm_Of_g;
        // inclination can be calculated as
        //  it is the angle measurment

        mGravity = sensorEvent.values;
        inclineGravity = mGravity.clone();
        double norm_Of_g2 = Math.sqrt(inclineGravity[0] * inclineGravity[0] + inclineGravity[1] * inclineGravity[1] + inclineGravity[2] * inclineGravity[2]);

        // Normalize the accelerometer vector
        inclineGravity[0] = (float) (inclineGravity[0] / norm_Of_g2);
        inclineGravity[1] = (float) (inclineGravity[1] / norm_Of_g2);
        inclineGravity[2] = (float) (inclineGravity[2] / norm_Of_g2);
        // int inclination = (int) Math.round(Math.toDegrees(Math.acos(acc_z_t)));
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(inclineGravity[0], inclineGravity[1])));*/
       ////////////////////////////////////////////
        mAcc = sensorEvent.values;
        inclineTotal = mAcc.clone();
        gravity[0]=inclineTotal[0]- (float)x;
        gravity[1]=inclineTotal[1]- (float)y;
        gravity[2]=inclineTotal[2]- (float)z;
        double norm_Of_g2 = Math.sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] *gravity[2]);
        inclineGravity[0] = (float) (gravity[0] / norm_Of_g2);

        inclineGravity[1] = (float) (gravity[1] / norm_Of_g2);
        inclineGravity[2] = (float) (gravity[2] / norm_Of_g2);
        // int inclination = (int) Math.round(Math.toDegrees(Math.acos(acc_z_t)));
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(inclineGravity[0], inclineGravity[1])));

        return angle ;

    }
    public float[] displayGravity(double x, double y , double z, SensorEvent sensorEvent)
    {
        mAcc = sensorEvent.values;
        inclineTotal = mAcc.clone();
        gravity[0]=inclineTotal[0]- (float)x;
        gravity[1]=inclineTotal[1]- (float)y;
        gravity[2]=inclineTotal[2]- (float)z;
        return gravity;

    }

}
