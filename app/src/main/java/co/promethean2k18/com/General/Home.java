package co.promethean2k18.com.General;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.promethean2k18.com.Data_.OrganizerProfile_model;
import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.KitLunchData;
import co.promethean2k18.com.Organizers.QuickManageMyEventRegistration;
import co.promethean2k18.com.Payments.ManagePayments;
import co.promethean2k18.com.R;
import co.promethean2k18.com.Scanner;

public class Home extends AppCompatActivity {

    CardView statistics, lunchkit, confirm_regn, manage_registration,paymentsTeam;
    Intent manageActivityIntent;
    String eventsICanAccess,eventsNames;
    boolean isDataLoaded;
    InternetCheck internetCheck;
    ProgressDialog progressDialog;
    ScrollView layout;
    ArrayList<OrganizerProfile_model> organizerProfile_models;
    Button sendEmails,scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        organizerProfile_models = new ArrayList<>();
        organizerProfile_models.clear();

        internetCheck = new InternetCheck(this);

        layout = findViewById(R.id.homeParentLayout);
        progressDialog.show();
        AndroidNetworking.initialize(this);
        loadProfileData();

        scanner = findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),KitLunchData.class));
            }
        });

        statistics = findViewById(R.id.statistics_page);
        confirm_regn = findViewById(R.id.confirm_registrations);
        manage_registration = findViewById(R.id.manage_event_registrations);
        paymentsTeam = findViewById(R.id.paymentsTeam);
        sendEmails = findViewById(R.id.sendMAilsMAn);
        lunchkit = findViewById(R.id.LunchKitsManage);

        sendEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailTOtheOrganizer();
            }
        });

        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(StoreMyData.organizerProfile.getType())==3
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==4
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==5
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==7)
                startActivity(new Intent(getApplicationContext(), PrometheanStatistics.class));
                else
                    Toast.makeText(Home.this, "Sorry, you are not authorized to perform this action", Toast.LENGTH_SHORT).show();
            }
        });

        manage_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             //   manageActivityIntent = new Intent(Home.this, SelectEvent.class);
           //     manageActivityIntent.putExtra("type", "manage");
                Intent intent = new Intent(getApplicationContext(),SelectTheEvent.class);
                intent.putExtra("type", "manage");
                intent.putExtra("names",eventsNames);
                intent.putExtra("ids",eventsICanAccess);
                startActivity(intent);
            }
        });

        confirm_regn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(StoreMyData.organizerProfile.getType())==1
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==2
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==6
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==7) {
                    Intent intent = new Intent(getApplicationContext(), SelectTheEvent.class);
                    intent.putExtra("type", "confirm");
                    intent.putExtra("names", eventsNames);
                    intent.putExtra("ids", eventsICanAccess);
                    startActivity(intent);
                }else{
                    Toast.makeText(Home.this, "Sorry you are not authorized to perform this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paymentsTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(StoreMyData.organizerProfile.getType())==5
                        || Integer.parseInt(StoreMyData.organizerProfile.getType())==7){
                    Intent intent = new Intent(getApplicationContext(), SelectTheEvent.class);
                    intent.putExtra("type", "payment");
                    intent.putExtra("names",eventsNames);
                    intent.putExtra("ids",eventsICanAccess);
                    startActivity(intent);
                }else {
                    Toast.makeText(Home.this, "Sorry, you are not authorized to perform this", Toast.LENGTH_SHORT).show();
                    return;
                }
//                startActivity(new Intent(getApplicationContext(),ManagePayments.class));
            }
        });

//        Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
     //   sendEmail();

        lunchkit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),KitLunchData.class));
            }
        });
    }

    private void loadProfileData() {

        if (!internetCheck.isIsInternetAvailable()){
            Snackbar.make(layout,"No Internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadProfileData();
                }
            }).show();
            return;
        }

        AndroidNetworking.post(Urls.getOrganizerData)
                .addBodyParameter("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("user_data");

                            JSONObject j = jsonArray.getJSONObject(0);
                            StoreMyData.organizerProfile.setEventsICanAccessIds(j.getString("ev"));
                            StoreMyData.organizerProfile.setEventNames(j.getString("evn"));

                            StoreMyData.organizerProfile.setOrgId(j.getString("uid"));
                            StoreMyData.organizerProfile.setName(j.getString("name"));
                            StoreMyData.organizerProfile.setDept(j.getString("dept"));
                            StoreMyData.organizerProfile.setType(j.getString("type"));
                            StoreMyData.organizerProfile.setEmail(j.getString("email"));
                            StoreMyData.organizerProfile.setPassword(j.getString("pass"));
                            StoreMyData.organizerProfile.setPhone(j.getString("phone"));
                            //Toast.makeText(SelectEvent.this, organizerProfile.getEventsICanAccessIds(), Toast.LENGTH_SHORT).show();
                            eventsICanAccess = j.getString("ev");
                            eventsNames = j.getString("evn");
                            Toast.makeText(Home.this, "Welcome "+j.getString("name"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            progressDialog.dismiss();
                        }
                        //progressDialog.dismiss();
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d("error",""+anError);
                        Toast.makeText(getApplicationContext(), "Check your internet, avoid proxied wifi networks ", Toast.LENGTH_SHORT).show();
                        // progressDialog.dismiss();
//                        Snackbar.make(layout,"No internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                checkInternet();
//                            }
//                        }).show();
                        return;
                    }
                });
    }

    void startActivityManage() {
        startActivity(manageActivityIntent);
    }


    private void sendEmail(){
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.post(Urls.getAllOrg)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("user_data");

                            int count=0;

                            while (count<jsonArray.length()) {
                                JSONObject j = jsonArray.getJSONObject(count);
                                OrganizerProfile_model o = new OrganizerProfile_model();

                                o.setEventsICanAccessIds(j.getString("ev"));
                                o.setEventNames(j.getString("evn"));

                                o.setOrgId(j.getString("uid"));
                                o.setName(j.getString("name"));
                                o.setDept(j.getString("dept"));
                                o.setType(j.getString("type"));
                                o.setEmail(j.getString("email"));
                                o.setPassword(j.getString("pass"));
                                o.setPhone(j.getString("phone"));
                                //Toast.makeText(SelectEvent.this, organizerProfile.getEventsICanAccessIds(), Toast.LENGTH_SHORT).show();
//                            eventsICanAccess = j.getString("ev");
//                            eventsNames = j.getString("evn");
                                organizerProfile_models.add(o);
                                //sendEmailTOtheOrganizer(o.getPassword(), o.getEmail());
                                count++;
                                Log.i("empas",count+" ");
                            }

                            //Toast.makeText(Home.this, "Welcome "+j.getString("name"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("empas",e.getLocalizedMessage());
                        }finally {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void sendEmailTOtheOrganizer(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (int i=45;i<organizerProfile_models.size();i++){

                    String to = organizerProfile_models.get(i).getEmail(),
                            from = "admin@promethean2k18.com",
                            sub = "Organizer app login credentials",
                            message = "Dear organizer, \nEmail: " +to+"\nPassword: " + organizerProfile_models.get(i).getPassword() +" are your credentials," +
                                    " if you are not able to login, please contact 9502039079, the app link will be shared with you shortly\nThank You",
                            header= "From: Promethean 2k18<" + from + ">\n" +
                                    "Reply-To:" +from+'\n' +
                                    "X-Mailer: PHP/";
                    AndroidNetworking.initialize(Home.this);
                    final int finalI = i;
                    AndroidNetworking.post("http://promethean2k18.com/app/sendMailToParticipant.php")
                            .addBodyParameter("to",to)
                            .addBodyParameter("from",from)
                            .addBodyParameter("sub",sub)
                            .addBodyParameter("header",header)
                            .addBodyParameter("mess",message)
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(Home.this, response, Toast.LENGTH_SHORT).show();
                                    Log.i("empas","mail " + finalI + " " + response);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(Home.this, "Cannot send mail, please try later " + "\n"+anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                                    Log.i("empas",anError.getMessage() + " " + anError.getErrorDetail());
                                }
                            });

                }
            }
        });
    }




    class DownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StoreMyData.organizerProfile = null;
            StoreMyData.events.clear();
            StoreMyData.eventsICanAccessIDs.clear();

            Thread thread = new Thread();
            Log.i("fz","thread created");
            try {
                thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("fz","thread started");
            thread.start();
            Log.i("fz","thread stopped");

            if (!isDataLoaded){
                Toast.makeText(Home.this, "data has not loaded" + eventsICanAccess, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Home.this, "data has not loaded" + eventsICanAccess, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            startActivityManage();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("fz","do in background " + eventsICanAccess);


            return null;
        }
    }
}


/*

    (Common to all)

    Registrations
    Payments
    Users registered in the app
    Events

    (Event Organizer)

    Manage my event()

        Confirm Participation

 */

