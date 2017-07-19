package info.android.rowing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.android.rowing.R;

/**
 * Created by mahmoud_mashal on 7/14/2017.
 */

public class StartCoachModeActivity extends AppCompatActivity {


    private Spinner coachSpinner , clubSpinner;
    private Button startCoach;
    private String coachName , clubName;
    private boolean clubFlag=false;
    private boolean coachFlag=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_coach_mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coachSpinner = (Spinner) findViewById(R.id.coach_spinner);
        clubSpinner = (Spinner)  findViewById(R.id.club_spinner);



        final List<String> clubList =new ArrayList<String>();
        clubList.add("Elmuqawilin");
        clubList.add("Elmasry");
        clubList.add("Elmaadi");
        clubList.add("Elshorta");

        ArrayAdapter<String> clubAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, clubList);
        clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubSpinner.setAdapter(clubAdapter);
        clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isSpinnerInitial = true;

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                clubName = clubSpinner.getSelectedItem().toString();
                clubFlag=true;

            }

            public void onNothingSelected(AdapterView<?> paren) {
                Toast.makeText(getApplicationContext(), "Please enter the name of your club ", Toast.LENGTH_SHORT).show();
                clubFlag=false;
            }
        });
        final List<String> coachList =new ArrayList<String>();
        coachList.add("Ahmed");
        coachList.add("Zezo");
        coachList.add("mohamed");
        coachList.add("sayed");

        ArrayAdapter<String> coachAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, coachList);
        coachAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coachSpinner.setAdapter(coachAdapter);
        coachSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isSpinnerInitial = true;

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                coachName = coachSpinner.getSelectedItem().toString();
                coachFlag=true;

            }

            public void onNothingSelected(AdapterView<?> paren) {
                Toast.makeText(getApplicationContext(), "Please enter the name of your coach ", Toast.LENGTH_SHORT).show();
                coachFlag=false;
            }
        });

    }
    public void startCoachMode(View view){

        Intent coachReferenceName = new Intent(StartCoachModeActivity.this, CoachModeActivity.class);
            Bundle coachBundle = new Bundle();
            coachBundle.putString("club", clubName);
            coachBundle.putString("coach", coachName);
            coachReferenceName.putExtras(coachBundle);
            startActivity(coachReferenceName);



    }

}
