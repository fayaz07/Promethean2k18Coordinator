package co.promethean2k18.com.General;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Organizers.RegistrationsForMyEvent;
import co.promethean2k18.com.R;

public class SelectTheEvent extends AppCompatActivity {

    MaterialSpinner materialSpinner;
    Button continueB;
    String eventNames,eventIds;
    ArrayList<String> names,ids;
    String type;
    Spinner spinner;
    //String[] arrayList = (getResources().getStringArray(R.array.eventids));
    //String[] arrayLista = (getResources().getStringArray(R.array.eventNames));

    String[] arrayList = {"1001", "2001","3001", "4001", "7001","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15",
            "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39",
            "40","41","42","43","44","45","46","47","48"};

    String[] arrayLista = {"Hackthon on Smart City Technologies","Poster Presentation","Project Expo","Ideation","Animethon",
            "IoT through Python","Xenium","Blockchain through crypto","CODEATHON","Computer Forensics workshop",
            "Techracy","CODE CRUNCH","Fast and furious","Big data And Hadoop","Code Conflicts","HAPTICS - A ROBOTIC ARM WORKSHOP",
            "PHYSIC-SENSE","DIGI-WISSEN","ELECTROWIZARD","SOLAR WORKSHOP","CRACK-IT-UP","Catch The Catch","WORD WRECKER",
            "MedTech Orator","Ad-Zap","Mystic code","BIZZ ENIGMA (Business Quiz)","YOUNG MANAGER","DEBATE(Battle of the Brains)",
            "Computer Aided Designing or Drafting","STARTUP CEO","Chemcode","Quest-ion","CHEM HUNT","Techno Vanza","CIRCUIT INNOVATION CHALLENGE",
            "RF DESIGN","ARDOPHRENIA","TECH QUIZ","INTERNET OF THINGS, an ARDUINO based workshop","ARDUBOTICS ROBOTICS workshop",
            "SRUSHTI(AUTOCAD WORKSHOP)","CIVIL CRAFT","QUICK SURVEY","RESTRICT TO METER","STAADPRO (WORKSHOP)","TECHNICAL QUIZ",
            "MECHMANIA","KEY TO CREATE","Clumsy scientist","Guess me if u can","Sherlocked","Product design",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_the_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        names = new ArrayList<>();
        ids = new ArrayList<>();

        eventNames = getIntent().getStringExtra("names");
        eventIds = getIntent().getStringExtra("ids");
        type = getIntent().getStringExtra("type");

        String eventslist = eventNames, subItem = "";
        spinner = findViewById(R.id.eventNamesSpinner2);

//        if (Integer.parseInt(StoreMyData.organizerProfile.getType())==)

        for (int i = 0; i < eventslist.length(); i++) {
            char c = eventslist.charAt(i);
            if (c == ',') {
                names.add(subItem);
                subItem = "";
            } else if (i == eventslist.length() - 1) {
                subItem += c;
                names.add(subItem);
                Log.i("fz","item " + subItem);
                subItem = "";
            } else {
                subItem += c;
            }
        }
        eventslist = eventIds;
        for (int i = 0; i < eventslist.length(); i++) {
            char c = eventslist.charAt(i);
            if (c == ',') {
                ids.add(subItem);
                subItem = "";
            } else if (i == eventslist.length() - 1) {
                subItem += c;
                ids.add(subItem);
                Log.i("fz","item " + subItem);
                subItem = "";
            } else {
                subItem += c;
            }
        }


        materialSpinner = findViewById(R.id.eventNamesSpinner);
        materialSpinner.setItems(names);

        spinner.setVisibility(View.GONE);
        if (Integer.parseInt(StoreMyData.organizerProfile.getType())==3
                || Integer.parseInt(StoreMyData.organizerProfile.getType())==4
                || Integer.parseInt(StoreMyData.organizerProfile.getType())==5
                || Integer.parseInt(StoreMyData.organizerProfile.getType())==7){

            materialSpinner.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        }

        continueB = findViewById(R.id.manageThisEvent);
        continueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(StoreMyData.organizerProfile.getType())==3
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==4
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==5
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==7){

                    Intent intent = new Intent(getApplicationContext(),RegistrationsForMyEvent.class);
                    intent.putExtra("id",arrayList[spinner.getSelectedItemPosition()]);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), RegistrationsForMyEvent.class);
                    intent.putExtra("id", ids.get(materialSpinner.getSelectedIndex()));
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}


/*

type
-1 Departmental event organizer
-2 Featured event organizer
-3 all events accessible + confirm registrations (core team)
-4 all events accessible but not editable (principal, all)
-7 Fayaz and friends
-5 Payments team
-6 Non technical events
1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42

*/