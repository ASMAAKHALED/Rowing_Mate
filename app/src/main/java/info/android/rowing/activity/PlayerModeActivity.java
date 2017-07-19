package info.android.rowing.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Timer;
import java.util.TimerTask;


import info.android.rowing.R;
import info.android.rowing.Sensors.AccelerometerValues;

//import static info.android.rowing.Sensors.AccelerometerValues.getAccelerationValues;
import info.android.rowing.Stroke.StrokeDetection;
import info.android.rowing.Stroke.StrokeRate;
import info.android.rowing.Tilting.Tilting;

import static android.R.attr.max;
import static android.R.attr.y;

import static android.R.attr.key;
import static info.android.rowing.activity.MainActivity.RC_SIGN_IN;

/**
 * Created by mahmoud_mashal on 2/2/2017.
 */

public class PlayerModeActivity extends AppCompatActivity implements LocationListener, SensorEventListener, IBaseGpsListener {

    private int seconds = 0;
    private boolean running ,wasRunning;
    private SensorManager mSensorManager;
    private Sensor senAccelerometer;
    private static double[]  AccelerationComponent=new double[3];
    private final Handler mHandler = new Handler();
    private Runnable mTimer0;
    private Runnable mTimer1;
    private int angle;
    GraphView graph;
    Viewport viewport;
    private LineGraphSeries<DataPoint> mSeries1;
    private BarGraphSeries<DataPoint> mSeries2;
    private static DataPoint[] DataPointArray =new DataPoint[50];

    private int acc_count=50;
    private DataPoint[] values = new DataPoint[acc_count];
    private double[] acc_values = new double[acc_count];
    private double[] stroke_places = new double[acc_count];
    private double[] acc_not_smoothed = new double[acc_count];
    private static int strokeCount=0,lastStrokeCount=0;
    private double avgStrRate=0, strRate;
    private TextView distance ,Speed , strokeRate;
    private int lastTime=0;
    private static double time200m=0;
    private static int changed=0;
    private double m=0,x=0;
    double x_axes,y_axes;
    ///// smoothing
    private int  smooth_count=3 ,l=0; //j=0;
    private double[] acc_smooth = new double[smooth_count];
    private double avg_acc=0;
    private static PlayerModeActivity mPlayerModeActivity;
    private double max=0;
    private int accelDir=0;
    private double a=0;
    private final static int ROWER_MODE = 1;
    private final static int COXSWAIN_MODE = -1;
    private  double pthreshold,nthreshold;
    ////SPEED &DISTANCE
    private CLocation oldLocation = null;//////////////////////////////////
    private  float[] results;
    private float Dist = 0;
    float nCurrentSpeed = 0;
    ////firebase
    private String first, second , RowingRef,playerKey,PlayerCoxswain ,rowerName,boatType;
    private FirebaseDatabase RowingFirebaseDatabase;
    private DatabaseReference rowingReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState!=null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        mPlayerModeActivity=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);//////////////
        this.updateSpeed(null);////////////
        //to keep the screen light on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        runTimer();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            // Success! There's an  ACCELEROMETER.
            senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //7atet el sensor el default fi el object bta3y
            mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL); //3malt regiter lel accelerometer bta3y fi el maneger w ch8lto b normal delay
            for (int k = 0; k < acc_count; k++) {
                acc_values[k] = 0;
            }
            for (int k = 0; k < acc_count; k++) {
                acc_not_smoothed[k] = 0;
            }
            for (int k = 0; k < acc_count; k++) {
                stroke_places[k] = 0;
            }
        }
        else {
            // Failure! No accelerometer .
        }
        graph = (GraphView) findViewById(R.id.acceleration_graph);
        //Viewport viewport = graph.getViewport();
        DataPoint dataP = new DataPoint(0,0);
        for(int i=0;i<50;i++) {
            values[i]=dataP;
            //DataPointArray[i] = ;
        }
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);
        mSeries2 = new BarGraphSeries<>(generateData2());
        graph.addSeries(mSeries2);
        viewport = graph.getViewport();
        //viewport.scrollToEnd();
        viewport.setScrollable(true);
        //Viewport viewport2 = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(50);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-0.5);
        viewport.setMaxY(0.5);
        mSeries1.setColor(Color.parseColor("#006064"));
        //mSeries1.setDrawDataPoints(true);
        mSeries1.setDrawBackground(true);
        //mSeries1.setBackgroundColor(Color.parseColor("#E0F7FA"));
        mSeries1.setTitle("Acceleration Curve");
        //mSeries1.setColor(Color.GREEN);
        //mSeries1.setDrawDataPoints(true);
        //mSeries1.setDataPointsRadius(4);
        mSeries1.setThickness(8);

        mSeries2.setColor(Color.parseColor("#006064"));
        mSeries2.setSpacing(80);


        Intent playerReferenceName = getIntent();
        Bundle playerBundle = playerReferenceName.getExtras();

        first = playerBundle.getString("club");
        second = playerBundle.getString("coach");
        RowingRef=first+second;
        playerKey=playerBundle.getString("playerID");
        PlayerCoxswain=playerBundle.getString("playerCoxswain");
        boatType=playerBundle.getString("boatType");

        RowingFirebaseDatabase =FirebaseDatabase.getInstance();
        rowingReference =RowingFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                rowerName = user.getDisplayName();
            }
        };


        StrokeDetection mStrokeDetection = new StrokeDetection();
        if(PlayerCoxswain .equals("Rower"))
        {
            mStrokeDetection.setAccMode(ROWER_MODE);
        }

        else if(PlayerCoxswain.equals("Coxswain"))
        {
            mStrokeDetection.setAccMode(COXSWAIN_MODE);
        }

        if(boatType .equals("Wooden boat"))
        {
            mStrokeDetection.setThreshold(0.01,-0.01);
        }

        else if(boatType.equals("Fiber boat"))
        {
            mStrokeDetection.setThreshold(0.21,-0.21);
        }




        ///send data every second 1000ms////////////////
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                RowerData rowerData=new RowerData( rowerName,strRate, nCurrentSpeed,angle,Dist,time) ;


                //HashMap<String,RowerData> rowerHashMap=new HashMap<>();
                //rowerHashMap.put(key,rowerData);
                //rowingReference.child(RowingRef).push().setValue(rowerHashMap);
                rowingReference.child(RowingRef).child(playerKey).child("name").setValue(rowerData.getname());
                rowingReference.child(RowingRef).child(playerKey).child("angle").setValue(rowerData.getAngle());
                rowingReference.child(RowingRef).child(playerKey).child("distance").setValue(rowerData.getDistance());
                rowingReference.child(RowingRef).child(playerKey).child("speed").setValue(rowerData.getSpeed());
                rowingReference.child(RowingRef).child(playerKey).child("strokeRate").setValue(rowerData.getStrokeRate());
                rowingReference.child(RowingRef).child(playerKey).child("time").setValue(rowerData.gettime());

            }
        }, 0, 2000); // 2 sec


    }
    public static PlayerModeActivity getInstance(){
        return   mPlayerModeActivity;
    }
    private static int timer10s=0;
    private void runTimer() {

        final TextView timeView = (TextView) findViewById(R.id.time);
        strokeRate= (TextView)findViewById(R.id.stroke_rate);

        mTimer0 = new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                time = String.format("%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);


                if (running) {
                    seconds++;
                }
                //avgStrRate = (double)(strokeCount*60) / (double)seconds;
                mHandler.postDelayed(this,1000);
            }
        };
        //handler.postDelayed(mTimer0,1000);
        mHandler.postDelayed(mTimer0, 100);
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                if(running) {
                    GraphView graph = (GraphView) findViewById(R.id.acceleration_graph);
                    Viewport viewport = graph.getViewport();
                    //generateData();
                    mSeries2.resetData(generateData2());
                    mSeries1.resetData(generateData());

                    viewport.setMinX(m-10);
                    viewport.setMaxX(m);
                    time200m=time200m+0.2;
                    if(time200m>=10){
                        timer10s=1;
                    }
                    /*if (strokeCount > (lastStrokeCount + 4)) {
                        strRate = (strokeCount - lastStrokeCount) * 60 * 5 / (time200m-lastTime);
                        strokeRate.setText(String.valueOf(strRate) + "str/min");
                        lastTime = time200m;
                        lastStrokeCount = strokeCount;
                    }*/
                    //stroke rate
                    StrokeRate mStrokeRate=new StrokeRate();
                    strRate=mStrokeRate.strRate(strokeCount);
                    TextView strokeRate=(TextView)findViewById(R.id.stroke_rate);
                    strokeRate.setText(String.valueOf(strRate));
                }
                mHandler.postDelayed(this, 200);
                // mHandler.postDelayed(this, 10000);
                changed=0;
            }
        };
        mHandler.postDelayed(mTimer1, 100);

    }

    /////////GPS/////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Allow Rowing Mate to access location?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(PlayerModeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void finish() {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        double nCurrentLong = 0;
        double nCurrentLat = 0;

        results = new float[10];
        if (location != null) {
            nCurrentSpeed = location.getSpeed();
            nCurrentLong = location.getLongitude();
            nCurrentLat = location.getLatitude();
            if (oldLocation != null) {
                Location.distanceBetween(oldLocation.getLatitude(), oldLocation.getLongitude(), nCurrentLat, nCurrentLong, results);
            }
            oldLocation = location;
        }
        Dist = Dist + results[0];
        String strUnits = "m/sec";
        TextView Speed = (TextView) this.findViewById(R.id.pace);
        Speed.setText(String.valueOf(nCurrentSpeed) + " " + strUnits);
        TextView distance = (TextView) this.findViewById(R.id.distance);
        distance.setText(String.valueOf(Dist) + "m");

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            CLocation myLocation = new CLocation(location);
            if (running) {
                this.updateSpeed(myLocation);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        // mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (wasRunning) {
            running = true;
        }

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        mSensorManager.unregisterListener(this);
        //mHandler.removeCallbacks(mTimer0);
        //mHandler.removeCallbacks(mTimer1);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setViewportMax(double v){
        viewport.setMaxY((int) 1.5 * v);
    }
    public void setViewportMin(double v){
        viewport.setMinY((int) 1.5 * v);
    }
    public void strokeCount(int count){ strokeCount=count;}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        changed++;
        GraphView graph = (GraphView) findViewById(R.id.acceleration_graph);
        Viewport viewport = graph.getViewport();
        Sensor mySensor = sensorEvent.sensor;
        if(running) {
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                //get values of accelerometer
                AccelerometerValues mAccelerometerValues=new AccelerometerValues();
                AccelerationComponent = mAccelerometerValues.getAccelerationValues(sensorEvent);

                //detec stroke
                StrokeDetection mStrokeDetection = new StrokeDetection();
                acc_values= mStrokeDetection.DetectStroke(AccelerationComponent[0], AccelerationComponent[1], AccelerationComponent[2]);
                acc_not_smoothed= mStrokeDetection.NotSmoothed(AccelerationComponent[0], AccelerationComponent[1], AccelerationComponent[2]);
                stroke_places=mStrokeDetection.StrokePlaces(AccelerationComponent[0], AccelerationComponent[1], AccelerationComponent[2]);
                //stroke count
                strokeCount=mStrokeDetection.strokeCount();




                //measure angle of tilting
                Tilting mTilting = new Tilting();
                angle = mTilting.TiltMeasure(AccelerationComponent[0], AccelerationComponent[1], AccelerationComponent[2],sensorEvent);
                TextView s=(TextView) findViewById(R.id.angle);
                s.setText(String.valueOf(angle));
                Tilting mTiltingg = new Tilting();
                float[] gg = mTiltingg.displayGravity(AccelerationComponent[0], AccelerationComponent[1], AccelerationComponent[2],sensorEvent);
                double avgacc=mStrokeDetection.getavgACC();


                if(angle<=10 && angle>=-10)
                {
                    ImageView img = (ImageView) findViewById(R.id.pic);
                    img.setImageResource(R.drawable.im_0);
                }
                else if(angle>90 ||angle<-90)
                {
                    ImageView img = (ImageView) findViewById(R.id.pic);
                    img.setImageResource(R.drawable.im_180jpg);
                }
                else if (angle>0) // +ve
                {
                    if(angle>=35 &&angle<=55)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im_45);
                    }

                    else if(angle>=80 &&angle <=90)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im_90);
                    }
                    else if(angle>=55&& angle <=80)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im_20);
                    }
                    else if(angle>=10&& angle <=35)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im_60);
                    }


                }
                else if(angle<0) //-ve
                {
                    if(angle<=-35 &&angle>=-55)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im__45);
                    }

                    else if(angle<=-80 &&angle >=-90)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im__90);
                    }
                    else if(angle<=-55&& angle >=-80)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im__20);
                    }
                    else if(angle<=-10&& angle >=-35)
                    {
                        ImageView img = (ImageView) findViewById(R.id.pic);
                        img.setImageResource(R.drawable.im__60);
                    }

                }
                //TextView COMPZ = (TextView) findViewById(R.id.compz);
                // COMPZ.setText(String.valueOf(gg[2]));
                double po=mStrokeDetection.positiveTh();
                double ne=mStrokeDetection.nThreshold();


            }
        }

    }
    private int dataCount=0;
    private static double mInit=0;
    private static double zInit=0;
    private static double z=0;

    private DataPoint[] generateData() {

        if(timer10s==1) {
            mInit = time200m-10;// + 0.24;
        }
        else {
            mInit=0;
        }
        m=mInit;
        DataPoint[] values = new DataPoint[acc_count];
        for (int i = 0; i < acc_count; i++) {
            x_axes = m;
            y_axes = acc_values[i];
            DataPoint v = new DataPoint(x_axes, y_axes);
            values[i] = v;
            // m=m+0.2;
            if(timer10s==1) {
                m=m+0.2;
            }
            else m=m+0.2;

        }
        return values;
        //return DataPointArray;
    }
    private DataPoint[] generateData2(){
        if(timer10s==1) {
            mInit = time200m-10;// + 0.24;
        }
        else {
            mInit=0;
        }
        m=mInit;
        DataPoint[] values = new DataPoint[acc_count];
        for (int i = 0; i < acc_count; i++) {
            x_axes = m;
            y_axes = stroke_places[i];
            DataPoint v = new DataPoint(x_axes, y_axes);
            values[i] = v;
            // m=m+0.2;
            if(timer10s==1) {
                m=m+0.2;
            }
            else m=m+0.2;

        }
        return values;
    }
    // ----------------menu-----------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        distance = (TextView)findViewById(R.id.distance);
        Speed = (TextView)findViewById(R.id.pace);
        strokeRate= (TextView)findViewById(R.id.stroke_rate);
        if (id == R.id.start_session) {
            running = true;
            if(wasRunning) {
                running = true;
            }

        }
        if (id == R.id.stop_session) {
            running = false;
            distance.setText(String.valueOf(Dist) + " m ");
            Speed.setText(String.valueOf(nCurrentSpeed) + " m/s");
            strokeRate.setText(String.valueOf(strRate) + "str/min");
        }

        if (id == R.id.reset_session) {
            running = false;
            seconds = 0;
            Dist = 0 ;
            nCurrentSpeed = 0 ;
            strokeCount = 0;
            time200m=0;
            distance.setText(String.valueOf(Dist) + " m ");
            Speed.setText(String.valueOf(nCurrentSpeed) + " m/s");
            strokeRate.setText(String.valueOf(strRate) + "str/min");
        }
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
