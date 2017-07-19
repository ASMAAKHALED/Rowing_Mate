package info.android.rowing.Stroke;

import android.widget.TextView;

/**
 * Created by Shorouk Ramzi on 4/16/2017.
 */

public class StrokeRate {
    private static int lastStrokeCount = 0;
    private static int lastTime = 0;
    private static int time200m = 0;
    private static double strRate;

    public double strRate(int str_count) {
        time200m++;
        if (str_count > (lastStrokeCount + 4)) {
            strRate = (str_count - lastStrokeCount) * 60 * 5 / (time200m - lastTime);
            lastTime = time200m;
            lastStrokeCount = str_count;
        }
        return strRate;
    }
}

