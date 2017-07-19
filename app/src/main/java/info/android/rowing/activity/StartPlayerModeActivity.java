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

import static android.R.attr.type;

/**
 * Created by mahmoud_mashal on 7/14/2017.
 */

public class StartPlayerModeActivity extends AppCompatActivity {


    private Spinner coachSpinner , clubSpinner, playerIdSpinner,PlayerCoxswainSpinner , boatTypeSpinner ;
    private Button startPlayer;
    private String coachName , clubName,PlayerID,PlayerCoxswain,boatType ;
    //private int PlayerID;
    private boolean clubFlag=false;
    private boolean coachFlag=false;
    private boolean playerIdFlag=false;
    private boolean playerCoxswainFlag=false;
    private boolean boatTypeFlag=false;

    //private EditText playerId ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_player_mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coachSpinner = (Spinner) findViewById(R.id.coach_spinner);
        clubSpinner = (Spinner)  findViewById(R.id.club_spinner);
        playerIdSpinner = (Spinner) findViewById(R.id.player_id);
        PlayerCoxswainSpinner = (Spinner) findViewById(R.id.player_coxswain);
        boatTypeSpinner=(Spinner) findViewById(R.id.boat_type);

        final List<String> boatTypeList =new ArrayList<String>();
        boatTypeList.add("Wooden boat");
        boatTypeList.add("Fiber boat");


        ArrayAdapter<String> boatTypeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, boatTypeList);
        boatTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boatTypeSpinner.setAdapter(boatTypeAdapter);
        boatTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isSpinnerInitial = true;

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                boatType = boatTypeSpinner.getSelectedItem().toString();
                boatTypeFlag=true;

            }

            public void onNothingSelected(AdapterView<?> paren) {
                Toast.makeText(getApplicationContext(), "Please choose Your Boat ", Toast.LENGTH_SHORT).show();
                boatTypeFlag=false;
            }
        });

        final List<String> playerCoxswainList =new ArrayList<String>();
        playerCoxswainList.add("Rower");
        playerCoxswainList.add("Coxswain");


        ArrayAdapter<String> playerCoxswainAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, playerCoxswainList);
        playerCoxswainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PlayerCoxswainSpinner.setAdapter(playerCoxswainAdapter);
        PlayerCoxswainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isSpinnerInitial = true;

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                PlayerCoxswain = PlayerCoxswainSpinner.getSelectedItem().toString();
                playerCoxswainFlag=true;

            }

            public void onNothingSelected(AdapterView<?> paren) {
                Toast.makeText(getApplicationContext(), "Please choose If You player or Coxswain ", Toast.LENGTH_SHORT).show();
                playerCoxswainFlag=false;
            }
        });


        final List<String> playerIdList =new ArrayList<String>();
        playerIdList.add("one");
        playerIdList.add("two");
        playerIdList.add("three");
        playerIdList.add("four");
        playerIdList.add("five");
        playerIdList.add("six");
        playerIdList.add("seven");
        playerIdList.add("eight");

        ArrayAdapter<String> playerIdAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, playerIdList);
        playerIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerIdSpinner.setAdapter(playerIdAdapter);
        playerIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isSpinnerInitial = true;

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                PlayerID = playerIdSpinner.getSelectedItem().toString();
                playerIdFlag=true;

            }

            public void onNothingSelected(AdapterView<?> paren) {
                Toast.makeText(getApplicationContext(), "Please enter your ID ", Toast.LENGTH_SHORT).show();
                playerIdFlag=false;
            }
        });

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
    public void startPlayerMode(View view){

        if(coachFlag&&clubFlag&&playerIdFlag&&playerCoxswainFlag&&boatTypeFlag) {

            Intent playerReferenceName = new Intent(StartPlayerModeActivity.this, PlayerModeActivity.class);
            Bundle playerBundle = new Bundle();
            playerBundle.putString("club", clubName);
            playerBundle.putString("coach", coachName);
            playerBundle.putString("playerID",PlayerID);
            playerBundle.putString("playerCoxswain",PlayerCoxswain);
            playerBundle.putString("boatType",boatType);
            playerReferenceName.putExtras(playerBundle);
            startActivity(playerReferenceName);

        }
    }

}

