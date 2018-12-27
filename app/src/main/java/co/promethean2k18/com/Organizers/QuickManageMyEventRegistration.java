package co.promethean2k18.com.Organizers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.promethean2k18.com.Data_.Register_model;
import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.General.InternetCheck;
import co.promethean2k18.com.Profile;
import co.promethean2k18.com.R;

public class QuickManageMyEventRegistration extends AppCompatActivity {

    Button sendEmail,confirm,cancel;
    MaterialSpinner selectTypeOfEmail;
    TextView name, email, phone, uid, rtoken, data, paymenStatus, rfee, teammates,eventid;
    Register_model register_model;
    ScrollView layout;
    InternetCheck internetCheck;
    ProgressDialog progressDialog;
    String token;
    
    ArrayList<String> emailsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_manage_my_event_registration);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        internetCheck = new InternetCheck(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        register_model = new Register_model();

        token = getIntent().getStringExtra("token");

        layout = findViewById(R.id.parentViewQucikManageEventRegn);

        emailsData = new ArrayList<>();
        emailsData.add("Event reminder!\nGet ready, your event is just few days ahead");
        emailsData.add("Payment reminder!\nYou have not paid the registration fee, please do it as soon as possible");
        emailsData.add("Registration Cancelled!\nWe have rejected your registration due to few issues");
        emailsData.add("Event reminder!\nWe need your attention, please reply us");
        
        sendEmail = findViewById(R.id.sendEmailToT);
        selectTypeOfEmail = findViewById(R.id.selectTypeOfEmail);
        name = findViewById(R.id.pnameT);
        email = findViewById(R.id.pemailT);
        uid = findViewById(R.id.puidT);
        rtoken = findViewById(R.id.pregtokenT);
        data = findViewById(R.id.pdataT);
        paymenStatus = findViewById(R.id.pPaymentStatusT);
        rfee = findViewById(R.id.pfeeT);
        teammates = findViewById(R.id.pteamT);
        eventid = findViewById(R.id.eventidT);

        selectTypeOfEmail.setItems(emailsData);
        confirm = findViewById(R.id.confirmParticipationT);
        cancel = findViewById(R.id.cancelParticiapationT);

        loadData();
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        //Toast.makeText(this, " user type " + StoreMyData.organizerProfile.getType(), Toast.LENGTH_SHORT).show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(StoreMyData.organizerProfile.getType()) != 1 ||
                        Integer.parseInt(StoreMyData.organizerProfile.getType()) != 2||
                        Integer.parseInt(StoreMyData.organizerProfile.getType()) != 7
                        || Integer.parseInt(StoreMyData.organizerProfile.getType()) != 6){
                    makeParticipationChange("Participated");
                }else{
                    Toast.makeText(QuickManageMyEventRegistration.this, "Sorry, you are not authorized to do this", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(StoreMyData.organizerProfile.getType()) != 1 ||
                        Integer.parseInt(StoreMyData.organizerProfile.getType()) != 2||
                        Integer.parseInt(StoreMyData.organizerProfile.getType()) != 7 ||
                        Integer.parseInt(StoreMyData.organizerProfile.getType()) != 6){
                    delete();
                }else{
                    Toast.makeText(QuickManageMyEventRegistration.this, "Sorry, you are not authorized to do this", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Profile.class);
                intent.putExtra("uid",register_model.getPart_uid());
                startActivity(intent);
            }
        });

    }

    private void delete(){
        progressDialog.show();
        AndroidNetworking.initialize(this);
        AndroidNetworking.post(Urls.delete)
                .addBodyParameter("regtoken",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(QuickManageMyEventRegistration.this, response, Toast.LENGTH_SHORT).show();
                        loadData();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(QuickManageMyEventRegistration.this, "Please try later or contact admin", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();
    }

    private void makeParticipationChange(String status){
        progressDialog.show();
        AndroidNetworking.initialize(this);
        AndroidNetworking.post(Urls.statusChange)
                .addBodyParameter("regtoken",token)
                .addBodyParameter("status",status)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(QuickManageMyEventRegistration.this, response, Toast.LENGTH_SHORT).show();
                        loadData();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(QuickManageMyEventRegistration.this, "Please try later or contact admin", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();
    }

    private void sendEmail() {

//        if (Integer.parseInt(StoreMyData.organizerProfile.getType()) == 1 ||
//                Integer.parseInt(StoreMyData.organizerProfile.getType()) == 2||
//                Integer.parseInt(StoreMyData.organizerProfile.getType()) != 7
//                || Integer.parseInt(StoreMyData.organizerProfile.getType()) != 6){
//            Toast.makeText(this, "Sorry, you are not authorized to do this", Toast.LENGTH_SHORT).show();
//            return;
//        }
        progressDialog.show();
        String to = register_model.getPart_email(),
                from = register_model.getOrganizerEmail(),
                sub = register_model.getEvent_name() + " Event Reminder",
                message = "Dear " + register_model.getPart_name() + "\n" + emailsData.get(selectTypeOfEmail.getSelectedIndex())+"\nThank You",
                header= "From: Promethean 2k18<" + from + ">\n" +
                        "Reply-To:" +from+'\n' +
                        "X-Mailer: PHP/";
        AndroidNetworking.initialize(QuickManageMyEventRegistration.this);
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
                        Toast.makeText(QuickManageMyEventRegistration.this, response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(QuickManageMyEventRegistration.this, "Cannot send mail, please try later " + "\n"+anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();

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

    private void loadData(){
        if (!internetCheck.isIsInternetAvailable()){
            progressDialog.dismiss();

            Snackbar.make(layout,"No internet connnection",Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData();
                }
            }).show();
            return;
        }
        progressDialog.show();

        AndroidNetworking.initialize(QuickManageMyEventRegistration.this);
        AndroidNetworking.post(Urls.getRegistrationData)
                .addBodyParameter("token", token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.show();
                        try {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("registrations");
                            //int count = 0 ;
                            if (jsonArray.length()==0 ) {
                                Snackbar.make(layout, "Sorry, registration doesn't exist or deleted, please try after sometime", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject j = jsonArray.getJSONObject(0);

                            register_model.setReg_token(j.getString("reg_token"));
                            register_model.setEvent_name(j.getString("event_name"));
                            register_model.setEvent_id(j.getString("event_id"));
                            register_model.setPart_uid(j.getString("part_uid"));
                            register_model.setPart_name(j.getString("part_name"));
                            register_model.setPart_phone(j.getString("part_phone"));
                            register_model.setPart_email(j.getString("part_email"));
                            register_model.setPart_referrer(j.getString("part_referer"));
                            register_model.setEvent_imageUrl(j.getString("url"));
                            register_model.setParticipationStatus(j.getString("part_status"));
                            register_model.setData(j.getString("data"));
                            register_model.setComments(j.getString("comments"));
                            register_model.setPaymentStatus(j.getString("payment_status"));
                            register_model.setOrganizerEmail(j.getString("org_email"));
                            register_model.setEventType(j.getString("e_type"));
                            register_model.setTeammates(j.getString("team"));
                            register_model.setEventPriceIndividual(j.getString("e_price_indi"));
                            register_model.setEventPriceTeam(j.getString("e_price_team"));
                            register_model.setFee(j.getString("fee"));

                            eventid.setText(Html.fromHtml("<b>Event Id: </b><br>" + j.getString("event_id")));
                            paymenStatus.setText(Html.fromHtml("<b>Payment Status: </b><br>" + j.getString("payment_status")));
                            teammates.setText(Html.fromHtml("<b>Teammates: </b><br>" + j.getString("team")));
                            rfee.setText(Html.fromHtml("<b>Registration Fee: </b><br>" +j.getString("fee")));
                            name.setText(Html.fromHtml("<b>Participant Name: </b><br>"+j.getString("part_name")));
                            email.setText(Html.fromHtml("<b>Participant Email: </b><br>" +j.getString("part_email")));
                            uid.setText(Html.fromHtml("<b>Participant UID:</b><br>" + j.getString("part_uid")));
                            rtoken.setText(Html.fromHtml("<b>Registration Token: </b><br>"+j.getString("reg_token")));
                            data.setText(Html.fromHtml("<b>Registration Data:</b> <br>") + j.getString("data"));

                            phone.setText(Html.fromHtml("<b>Partcipant Phone:</b> <br>" +j.getString("part_phone")));

                        }catch (JSONException e){
                            Log.d("exc11","exception " + e.getMessage());
                            e.printStackTrace();
                            Snackbar.make(layout,"Something's gone wrong, please try after sometime",Toast.LENGTH_SHORT).show();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        } finally {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("exc11","anerror " +anError.getResponse()+anError.getErrorBody());
                        progressDialog.dismiss();
                        Snackbar.make(layout,"Something's gone wrong, please try after sometime",Toast.LENGTH_SHORT).show();
                    }
                });
    }






}
