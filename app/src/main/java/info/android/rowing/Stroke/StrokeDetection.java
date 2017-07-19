package info.android.rowing.Stroke;
import android.provider.Settings;

import info.android.rowing.activity.PlayerModeActivity;


import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by Shorouk Ramzi on 4/16/2017.
 */

public class StrokeDetection {
    private int acc_count=50 , smooth_count=3 ; //j=0;
    private static  int l=0;
    private static double[] acc_values = new double[50];
    private static double[] acc_not_smoothed = new double[50];
    private static double[] stroke_places = new double[50];
    private static double[] acc_smooth = new double[3];
    private  static double avg_acc=0;
    private static double pthreshold,nthreshold;
    private static double pthreshold_avg=0,nthreshold_avg=0,pthreshold_sum=0,nthreshold_sum=0;
    private static  int w,z;
    private static int flag=0, flag2=0, flag3=0;
    private static double[] ymax = new double[3];
    private static double[] ymin = new double[3];
    private static double ymaxGraph=0.5,yminGraph=-0.5;
    private  static int strokeCount=0,lastStrokeCount=0;
    private double max=0;
    private int accelDir=0;
    private double a=0;
    private static int accMode;
    private static  double initialValue;

    public void setAccMode(int mode)
    {
        accMode=mode;
    }
    public void setThreshold(double pos,double neg)
    {   pthreshold=pos;
        nthreshold=neg;
        initialValue=pos;

    }

    public  double[] DetectStroke( double accX,double accY,double accZ)
    {
        if(strokeCount==0){
            ymax[0]=0;ymax[1]=0;ymax[2]=0;
            ymin[0]=0;ymin[1]=0;ymin[2]=0;
        }
        else if(strokeCount==1){
            pthreshold=initialValue;
            nthreshold=-initialValue;
        }
        max = Math.abs(accY) > Math.abs(accZ) ? -accY : accZ; // if device is exactly flat or vertical, one axis has to be ignored

        accelDir = accMode*max < 0 ? -1 : 1;
        System.out.println("accelerationMode =" + accMode);
        a = accelDir * Math.sqrt(accY* accY+ accZ*accZ);


     /*   acc_smooth[l]=a;
        if (l < (smooth_count - 1) ) {
            l++;
        }
        else {
            l = 0;
        }*/
        for (int k = 1; k < 3; k++)
        {
            acc_smooth[k-1]=acc_smooth[k];
        }
        acc_smooth[2]=a;

        avg_acc = (0.3*acc_smooth[0] + 0.4*acc_smooth[1] +0.5* acc_smooth[2]) ;

        for (int k = 1; k < acc_count; k++)
        {
            acc_values[k-1]=acc_values[k];
        }
        acc_values[49]=avg_acc;


        if (avg_acc > (pthreshold) && flag == 0) {
            if (avg_acc > ymax[0]) {

                ymax[0] = avg_acc;
            }
            if (ymax[0] > ymaxGraph) {
                ymaxGraph=ymax[0];

                // viewport.setMaxY((int) 1.5 * ymaxGraph);
                PlayerModeActivity.getInstance().setViewportMax(ymaxGraph);

                // mSessionActivity.setViewportMax(ymaxGraph);
            }
            flag2 = 1; //lma da yb2a 1 m3nah en el acc 3det el +ve threshold
        }

        if (avg_acc <= (nthreshold) && flag == 0 && flag2 == 1) {

            ymax[2]=ymax[1];
            ymax[1]=ymax[0];

            flag = 1; //da ma3nah eny lazem ad5ol fi el -ve
            flag2 = 0; //l2any hdawar 3ala el min
            flag3 = 0;
            w++;
            pthreshold = 0.6 * ((ymax[0]+ymax[1]+ymax[2])/3);
            pthreshold_sum+=pthreshold;
            pthreshold_avg+=pthreshold_sum/w;
            if (pthreshold >= 2*pthreshold_avg){
                pthreshold=pthreshold_avg;
            }
         /*   if(pthreshold>10*initialValue){
                pthreshold=initialValue;
            }
            if(pthreshold<initialValue){
                pthreshold=initialValue;
            }*/
            ymax[0] = 0;
        }

        if (avg_acc <= nthreshold && flag == 1) {
            if (avg_acc < ymin[0]) {

                ymin[0] = avg_acc;
            }
            if (ymin[0] < yminGraph) {
                yminGraph=ymin[0];
                // viewport.setMinY((int) 1.5 * yminGraph);
                PlayerModeActivity.getInstance().setViewportMin(yminGraph);

                // mSessionActivity.setViewportMin(yminGraph);

            }
            flag2 = 1;
        }

        if (avg_acc >= pthreshold && flag == 1 && flag2 == 1) {
            ymin[2]=ymin[1];
            ymin[1]=ymin[0];
            strokeCount++;
            /////this to avoid the effect of the spike
            if(strokeCount==1)
                pthreshold=initialValue;
            nthreshold=-initialValue;

            PlayerModeActivity.getInstance().strokeCount(strokeCount);
            flag = 0; //da ma3nah eny lazem ad5ol fi el +ve
            flag2 = 0; //l2any hdawar 3ala el max
            //nthreshold = 0.25 * ymin;
            nthreshold = 0.6 * ((ymin[0]+ymin[1]+ymin[2])/3);
            z++;
            nthreshold_sum+=nthreshold;
            nthreshold_avg+=nthreshold_sum/z;
            if (nthreshold <= 2*nthreshold_avg){
                nthreshold=nthreshold_avg;
            }
   /*         if(nthreshold<10*-initialValue){
                nthreshold=-initialValue;
            }
            if(nthreshold>-initialValue){
                nthreshold=-initialValue;
            }*/

            ymin[0] = 0;
            //for (int k = 0; k < acc_count; k++) {
            //   acc_values[k] = 0;
            //}
        }
        return acc_values;
    }

    public int strokeCount(){
        return strokeCount;
    }
    public double getavgACC(){
        return avg_acc;
    }
    public double positiveTh(){return pthreshold;}
    public double nThreshold(){return nthreshold;}
    public  double[] NotSmoothed( double accX,double accY,double accZ){
        max = Math.abs(accY) > Math.abs(accZ) ? -accY : accZ; // if device is exactly flat or vertical, one axis has to be ignored

        accelDir = accMode*max < 0 ? -1 : 1;
        System.out.println("accelerationMode =" + accMode);
        a = accelDir * Math.sqrt(accY* accY+ accZ*accZ);

        for (int k = 1; k < acc_count; k++)
        {
            acc_not_smoothed[k-1]=acc_not_smoothed[k];
        }
        acc_not_smoothed[49]=a;
        return acc_not_smoothed;}
    public  double[] StrokePlaces(double accX,double accY,double accZ){
        for (int k = 1; k < acc_count; k++)
        {
            stroke_places[k-1]=stroke_places[k];
        }
        if(strokeCount!=lastStrokeCount){
            stroke_places[49]=ymaxGraph;
        }
        else {
            stroke_places[49]=0;
        }

        lastStrokeCount=strokeCount;

        return stroke_places;}


}
