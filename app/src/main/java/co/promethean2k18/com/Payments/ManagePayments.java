package co.promethean2k18.com.Payments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.EditText;
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
import co.promethean2k18.com.Organizers.QuickManageMyEventRegistration;
import co.promethean2k18.com.Profile;
import co.promethean2k18.com.R;

public class ManagePayments extends AppCompatActivity {

    Button sendEmail,updatePayment;
    MaterialSpinner selectTypeOfEmail;
    TextView name, email, phone, uid, rtoken, data, paymenStatus, rfee, teammates,eventid,comments;
    Register_model register_model;
    ScrollView layout;
    InternetCheck internetCheck;
    ProgressDialog progressDialog;
    String token;
    EditText paidAmount,transactionid;
    ArrayList<String> emailsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payments);

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
        emailsData.add("Your payment has been recieved and updated succesfully, make sure to attend the event on time, Thank you");
        emailsData.add("Your payment was not done correctly, please pay the correct amount to get your payment confirmed. We will refund your money shortly");
        emailsData.add("Payment Failed, invalid payment");
        emailsData.add("Payment was not succesful, please try later");
        emailsData.add("Invalid/improper screen shot. Please send the proper screen shot of payment along with the transaction Id");

        sendEmail = findViewById(R.id.sendEmailToM);
        selectTypeOfEmail = findViewById(R.id.selectTypeOfEmailM);
        name = findViewById(R.id.pnameM);
        email = findViewById(R.id.pemailM);
        uid = findViewById(R.id.puidM);
        rtoken = findViewById(R.id.pregtokenM);
        data = findViewById(R.id.pdataM);
        paymenStatus = findViewById(R.id.pPaymentStatusM);
        rfee = findViewById(R.id.pfeeM);
        teammates = findViewById(R.id.pteamM);
        eventid = findViewById(R.id.eventidM);
        updatePayment = findViewById(R.id.updatePaymentStatus);
        comments = findViewById(R.id.pcommentsM);

        paidAmount = findViewById(R.id.amountpaidM);
        transactionid = findViewById(R.id.ptransactionIdM);

        selectTypeOfEmail.setItems(emailsData);

        loadData();
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        //Toast.makeText(this, " user type " + StoreMyData.organizerProfile.getType(), Toast.LENGTH_SHORT).show();

        updatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePayment();
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

    private void updatePayment() {
        progressDialog.show();
        final String regtoken = register_model.getReg_token(),paidAm = paidAmount.getText().toString(),
                comments = "Your payment was successful through online, transaction id " + transactionid.getText().toString()  + " for Rs. "+ paidAmount.getText().toString() + " and validated by our payments team";
        AndroidNetworking.initialize(ManagePayments.this);
        AndroidNetworking.post("http://promethean2k18.com/app/updatePayment.php")
                .addBodyParameter("regtoken",regtoken)
                .addBodyParameter("comments",comments)
                .addBodyParameter("en",register_model.getEvent_name())
                .addBodyParameter("pam",paidAm)
                .addBodyParameter("pname",register_model.getPart_name())
                .addBodyParameter("pemail",register_model.getPart_email())
                .addBodyParameter("puid",register_model.getPart_uid())
                .addBodyParameter("org",register_model.getOrganizerEmail())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ManagePayments.this, response.trim(), Toast.LENGTH_SHORT).show();
                        if (response.trim().contains("success")){
                            loadData();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(ManagePayments.this, "Cannot update data, please try later", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();

    }

    private void sendEmail() {
        progressDialog.show();
        String to = register_model.getPart_email(),
                from = "payments@promethean2k18.com",
                sub = register_model.getEvent_name() + " Event Reminder",
                message = "Your payment was successful through online, transaction id " + transactionid.getText().toString()
                        + " and validated by our payments team\nThank You",
                header= "From: Promethean 2k18<" + from + ">\n" +
                        "Reply-To:" +from+'\n' +
                        "X-Mailer: PHP/";
        message = emailsData.get(selectTypeOfEmail.getSelectedIndex());
        AndroidNetworking.initialize(ManagePayments.this);
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
                        Toast.makeText(ManagePayments.this, response, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(ManagePayments.this, "Cannot send mail, please try later", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDialog.dismiss();
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
        AndroidNetworking.initialize(ManagePayments.this);
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
                                Snackbar.make(layout, "Sorry, we are facing trouble out there, please try after sometime", Snackbar.LENGTH_SHORT).show();
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
                            eventid.requestLayout();
                            paymenStatus.setText(Html.fromHtml("<b>Payment Status: </b><br>" + j.getString("payment_status")));
                            paymenStatus.requestLayout();
                            teammates.setText(Html.fromHtml("<b>Teammates: </b><br>" + j.getString("team")));
                            teammates.requestLayout();
                            rfee.setText(Html.fromHtml("<b>Registration Fee: </b><br>" +j.getString("fee")));
                            rfee.requestLayout();
                            name.setText(Html.fromHtml("<b>Participant Name: </b><br>"+j.getString("part_name")));
                            name.requestLayout();
                            email.setText(Html.fromHtml("<b>Participant Email: </b><br>" +j.getString("part_email")));
                            email.requestLayout();
                            uid.setText(Html.fromHtml("<b>Participant UID:</b><br>" + j.getString("part_uid")));
                            uid.requestLayout();
                            rtoken.setText(Html.fromHtml("<b>Registration Token: </b><br>"+j.getString("reg_token")));
                            rtoken.requestLayout();
                            data.setText(Html.fromHtml("<b>Registration Data:</b> <br>") + j.getString("data"));
                            data.requestLayout();
                            comments.setText("Comments\n" + j.getString("comments"));
                            comments.requestLayout();
                            phone.setText(Html.fromHtml("<b>Partcipant Phone:</b> <br>" +j.getString("part_phone")));

                        }catch (JSONException e){
                            Log.d("exc11","exception " + e.getMessage());
                            e.printStackTrace();
                            Snackbar.make(layout,"Something's gone wrong, please try after sometime",Toast.LENGTH_SHORT).show();
                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        } finally
                        {
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
