package info.android.rowing.Sensors;

import android.hardware.SensorEvent;

import static android.R.attr.gravity;

/**
 * Created by Shorouk Ramzi on 4/16/2017.
 */

public class AccelerometerValues {
    private static double acc_x=0, acc_y=0, acc_z=0;
    private static double gravity []={0,0,0};
    //public static double AccComponents []=new double[3];
    public  double AccComponents []=new double[3];


    //public static double [] getAccelerationValues(  SensorEvent sensorEvent){
    public  double [] getAccelerationValues(  SensorEvent sensorEvent){
        /// remove the gravity component from acceleration values using low-pass filter
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        final float alpha = (float) 0.8;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
        acc_x = sensorEvent.values[0] - gravity[0];
        acc_y = sensorEvent.values[1] - gravity[1];
        acc_z  = sensorEvent.values[2] - gravity[2];

        AccComponents[0]=acc_x;
        AccComponents[1]=acc_y;
        AccComponents[2]=acc_z;

        return AccComponents;



    }
}
