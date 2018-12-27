package co.promethean2k18.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.blikoon.qrcodescanner.QrCodeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.General.InternetCheck;


public class KitLunchData extends AppCompatActivity {

    private static final String LOGTAG = "QR";
    Button lunchday1,lunchday2,kit,reset,scanNow;
    TextView lunchday1Stats,kitStats,name;
    String type,rtoken,uuid;

    InternetCheck internetCheck;
    RelativeLayout layout;
    ProgressDialog progressDialog;
    String lunchStatusString, kitStatusString;
    int registeredEvents;
    boolean falseData;

    private static final int REQUEST_CODE_QR_SCAN = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_lunch_data);

        layout = findViewById(R.id.parentLayoutKitLunch);

        internetCheck = new InternetCheck(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");

        lunchday1 = findViewById(R.id.lunchDay1Taken);
        lunchday2 = findViewById(R.id.lunchDay2Taken);
        kit = findViewById(R.id.kitrecieved);
        reset = findViewById(R.id.resetStats);
        scanNow = findViewById(R.id.Scan);

        //uid = getIntent().getStringExtra("uid");

        lunchday1Stats = findViewById(R.id.statusOfLunchDay1);

        kitStats = findViewById(R.id.participationKit);
        name = findViewById(R.id.participant_nameNNN);

     //   loadData();

        if (Integer.parseInt(StoreMyData.organizerProfile.getType())!=7){
            reset.setVisibility(View.INVISIBLE);
        }

        lunchday1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (falseData){
                    Snackbar.make(layout,"Invalid operation/user doesn't exist",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (lunchStatusString.trim().equals("Day 1 taken") || !lunchStatusString.trim().equals("Not Taken") ){
                    Snackbar.make(layout,"Already taken",Snackbar.LENGTH_LONG).show();
                    return;
                }
                updateLunchKit("Day 1 taken",kitStatusString);
            }
        });

        lunchday2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (falseData){
                    Snackbar.make(layout,"Invalid operation/user doesn't exist",Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (lunchStatusString.trim().equals("Day 1 taken, Day 2 taken")){
                    Snackbar.make(layout,"Already taken",Snackbar.LENGTH_LONG).show();
                    return;
                }else if(lunchStatusString.trim().equals("Day 1 taken")){
                    updateLunchKit("Day 1 taken, Day 2 taken",kitStatusString);
                }else{
                    updateLunchKit("Day 2 taken",kitStatusString);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (falseData){
                    Snackbar.make(layout,"Invalid operation/user doesn't exist",Snackbar.LENGTH_LONG).show();
                    return;
                }
                updateLunchKit("Not Taken","Not recieved");
            }
        });

        kit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (falseData){
                    Snackbar.make(layout,"Invalid operation/user doesn't exist",Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (kitStatusString.trim().equals("Kit Taken")){
                    Snackbar.make(layout,"Already taken the kit",Snackbar.LENGTH_LONG).show();
                    return;
                }else if(registeredEvents==0){
                    Snackbar.make(layout,"You have not registered in any events",Snackbar.LENGTH_LONG).show();
                    return;
                }
                updateLunchKit(lunchStatusString,"Kit Taken");
            }
        });

        scanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(KitLunchData.this,QrCodeActivity.class);
                startActivityForResult( i,REQUEST_CODE_QR_SCAN);
            }
        });
    }


    private void updateLunchKit(String lunch,String kit){
        progressDialog.show();
        AndroidNetworking.initialize(this);
        AndroidNetworking.post(Urls.updateLunchKit)
                .addBodyParameter("uid",uuid)
                .addBodyParameter("lunch",lunch)
                .addBodyParameter("kit",kit)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Snackbar.make(layout,response,Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Snackbar.make(layout,"Something has gone wrong, please try later",Snackbar.LENGTH_LONG).show();
                    }
                });
        loadData(uuid);
        progressDialog.dismiss();
    }

    private void loadData(final String uid){
//        if (internetCheck.isIsInternetAvailable()) {
//            // myToastMessage.showMyToast();
//            loadData(uid);
//        }else {
//            Snackbar.make(layout,"No internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    loadData(uid);
//                }
//            }).show();
//
//        }
        progressDialog.show();
        AndroidNetworking.initialize(this);
        AndroidNetworking.post(Urls.retrieveuserdata)
                .addBodyParameter("uid", uid)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("user_data");

                            if (jsonArray.length()==0){
                                falseData = true;
                                Snackbar.make(layout, "Invalid QR Code/User doesn't exists", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject j = jsonArray.getJSONObject(0);

                            name.setText(Html.fromHtml("<b>Name: </b><br>" + j.getString("name")));
                            lunchday1Stats.setText(Html.fromHtml("<b>Lunch Status:</b><br>" + j.getString("lunch")));
                            kitStats.setText(Html.fromHtml("<b>Participation Kit:</b><br> " + j.getString("kit")));
                            lunchStatusString = j.getString("lunch");
                            kitStatusString = j.getString("kit");

                            registeredEvents = Integer.parseInt(j.getString("re"));

                            progressDialog.dismiss();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        //progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        // Log.d("error",""+anError);
                        Toast.makeText(getApplicationContext(),"Check your internet, avoid proxied wifi networks",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        Snackbar.make(layout,"No internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadData(uid);
                            }
                        }).show();
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            Log.d(LOGTAG,"COULD NOT GET A GOOD RESULT.");
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(KitLunchData.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG,"Have scan result in your app activity :"+ result);
//            AlertDialog alertDialog = new AlertDialog.Builder(KitLunchData.this).create();
//            alertDialog.setTitle("Scan result");
//            alertDialog.setMessage(result);
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
            uuid = result;
            loadData(result);
        }
    }


}
