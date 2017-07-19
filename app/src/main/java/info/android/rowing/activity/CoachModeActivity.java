package info.android.rowing.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import info.android.rowing.R;



/**
 * Created by mahmoud_mashal on 7/6/2017.
 */

public class CoachModeActivity extends AppCompatActivity {


    private String first,second,RowingRef;
    private FirebaseDatabase RowingFirebaseDatabase;
    private DatabaseReference rowingReference;
    List<RowerData> rowerList = new ArrayList<>();
    private RowerDataAdapter rowerAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle coachBundle = getIntent().getExtras();
        first = coachBundle.getString("club");
        second = coachBundle.getString("coach");
        RowingRef = first+second ;

        RowingFirebaseDatabase =FirebaseDatabase.getInstance();
        rowingReference =RowingFirebaseDatabase.getReference();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                rowingReference.child(RowingRef).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        rowerList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RowerData rower = snapshot.getValue(RowerData.class);
                            rowerList.add(rower);
                            rowerAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                recyclerView =(RecyclerView) findViewById(R.id.recyclerView);
                rowerAdapter= new RowerDataAdapter(CoachModeActivity.this, rowerList);
                recyclerView.setLayoutManager(new LinearLayoutManager(CoachModeActivity.this, LinearLayoutManager.HORIZONTAL, true));
                recyclerView.setAdapter(rowerAdapter);




            }
        }, 0, 500); // 7 sec


    }


}


