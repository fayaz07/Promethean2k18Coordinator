package co.promethean2k18.com.General;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import co.promethean2k18.com.Data_.Statistics_model;
import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.R;

public class PrometheanStatistics extends AppCompatActivity {

    TextView downloads, deptRegns, specRegns, totalRegns, payments, amount;
    ProgressDialog progressDialog;
    Statistics_model statistics_model;
    InternetCheck internetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promethean_statistics);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        internetCheck = new InternetCheck(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        statistics_model = new Statistics_model();

        downloads = findViewById(R.id.appdownloads);
        deptRegns = findViewById(R.id.deptEventRegns);
        specRegns = findViewById(R.id.specialEventRegns);
        totalRegns = findViewById(R.id.totalRegns);
        payments = findViewById(R.id.paymentsdone);
        amount = findViewById(R.id.amountthroughapp);


        loadProfileData();
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

    private void loadProfileData() {

        if (!internetCheck.isIsInternetAvailable()){
            Snackbar.make(findViewById(R.id.parentStatistics),"No Internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadProfileData();
                }
            }).show();
            return;
        }
        progressDialog.show();
        AndroidNetworking.post(Urls.getStatistics)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("statistics");

                            JSONObject j = jsonArray.getJSONObject(0);
                            statistics_model.setDownloads(j.getString("downloads"));
                            statistics_model.setAmount_recieved_through_app_payments(j.getString("amount"));
                            statistics_model.setDept_event_registrations(j.getString("depreg"));
                            statistics_model.setPayments_done(j.getString("nopayments"));
                            statistics_model.setSpecial_event_registrations(j.getString("spreg"));
                            statistics_model.setTotal_registrations(j.getString("totreg"));

                            totalRegns.setText(Html.fromHtml("<b>Total Registrations:</b><br>"+j.getString("totreg")));
                            downloads.setText(Html.fromHtml("<b>App Downloads:</b><br>" + j.getString("downloads")));
                            deptRegns.setText(Html.fromHtml("<b>Departmentat Event Registrations:</b><br>" + j.getString("depreg")));
                            specRegns.setText(Html.fromHtml("<b>Special Event Registraions: </b><br>"+ j.getString("spreg")));
                            amount.setText(Html.fromHtml("<b>Amount Recieved through online payments:</b><br>"+j.getString("amount")));
                            payments.setText(Html.fromHtml("<b>Payments:</b><br>"+j.getString("nopayments")));

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
                        progressDialog.dismiss();
                        return;
                    }
                });
    }
}
