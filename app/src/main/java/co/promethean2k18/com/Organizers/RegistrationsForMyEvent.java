package co.promethean2k18.com.Organizers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.promethean2k18.com.Data_.Register_model;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.Payments.ManagePayments;
import co.promethean2k18.com.R;

public class RegistrationsForMyEvent extends AppCompatActivity {

    private ArrayList<Register_model> registraions;
    String eventId;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String typeOfAction;
    Button newRegistraion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations_for_my_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        registraions = new ArrayList<>();

        eventId = getIntent().getStringExtra("id");
        typeOfAction = getIntent().getStringExtra("type");

        newRegistraion = findViewById(R.id.createNewRegistrationForMyEvent);
        if (!typeOfAction.trim().equals("manage"))
            newRegistraion.setVisibility(View.INVISIBLE);

        registraions.clear();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerViewRegistrations);

        AndroidNetworking.initialize(this);
        AndroidNetworking.post(Urls.getRegistrationsForMyevent)
                .addBodyParameter("id",eventId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = response;
                            JSONArray jsonArray = jsonObject.getJSONArray("registrations");

                            if (jsonArray.length()==0){
                                Toast.makeText(RegistrationsForMyEvent.this, "Sorry, your event doesn't have any registrations, check back later", Toast.LENGTH_LONG).show();
                            }

                            Toast.makeText(RegistrationsForMyEvent.this, "Total Registrations: " + jsonArray.length(), Toast.LENGTH_LONG).show();

                            int count = 0;
                            while (count<jsonArray.length()) {
                                JSONObject j = jsonArray.getJSONObject(count);
                                Register_model register_model = new Register_model();
                                register_model.setFee(j.getString("fee"));
                                register_model.setPart_name(j.getString("pn"));
                                register_model.setPaymentStatus(j.getString("payms"));
                                register_model.setReg_token(j.getString("regtoken"));
                                registraions.add(register_model);
                                count++;
                            }

                            RegistrationsAdapter registrationsAdapter = new RegistrationsAdapter(registraions,getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(registrationsAdapter);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }finally {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(RegistrationsForMyEvent.this, "Something has gone wrong, please try after some time", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
        newRegistraion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int eId = Integer.parseInt(eventId);
                String typee = "";
                if (eId>0 && eId<100){
                    typee = "n";
                }else if (eId>1000 && eId<5000){
                    typee = "f";
                }else{
                    typee = "non";
                }

                Intent intent = new Intent(getApplicationContext(),Event_details_register.class);
                intent.putExtra("id",eventId);
                intent.putExtra("type",typee);
                startActivity(intent);
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

    class RegistrationsAdapter extends RecyclerView.Adapter<RegistrationsAdapter.RegistrationsViewHolder>{

        ArrayList<Register_model> registraionslist;
        Context context;

        public RegistrationsAdapter(ArrayList<Register_model> registraionslist, Context context) {
            this.registraionslist = registraionslist;
            this.context = context;
        }

        @NonNull
        @Override
        public RegistrationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.registrations_layout,viewGroup,false);
            RegistrationsViewHolder registrationsViewHolder = new RegistrationsViewHolder(view);
            return registrationsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RegistrationsViewHolder holder, final int i) {
            holder.name.setText("Name: " + registraionslist.get(i).getPart_name());
            holder.paymentstatus.setText("Payment: " + registraionslist.get(i).getPaymentStatus());
            holder.token.setText("#"+registraionslist.get(i).getReg_token());
            holder.regMode.setText("Registration Fee: " + registraionslist.get(i).getFee());
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuickManageActivity(registraionslist.get(i).getReg_token());
                }
            });
        }

        @Override
        public int getItemCount() {
            return registraionslist.size();
        }

        class RegistrationsViewHolder extends RecyclerView.ViewHolder{

            CardView parent;
            TextView name,token, paymentstatus,regMode;

            public RegistrationsViewHolder(@NonNull View itemView) {
                super(itemView);
                parent = itemView.findViewById(R.id.parentCardViewRegistrations);
                name = itemView.findViewById(R.id.participant_name);
                token = itemView.findViewById(R.id.reg_token);
                regMode = itemView.findViewById(R.id.reg_mode);
                paymentstatus = itemView.findViewById(R.id.payment_status);
            }
        }
    }

    private void startQuickManageActivity(String reg_token) {

        if (typeOfAction.trim().equals("confirm")){
            Intent intent = new Intent(getApplicationContext(),ConfirmEventRegistration.class);
            intent.putExtra("token",reg_token);
            startActivity(intent);
        }else if (typeOfAction.trim().equals("manage")){
            Intent intent = new Intent(getApplicationContext(),QuickManageMyEventRegistration.class);
            intent.putExtra("token",reg_token);
            startActivity(intent);
        }else if (typeOfAction.trim().equals("payment")){
            Intent intent = new Intent(getApplicationContext(),ManagePayments.class);
            intent.putExtra("token",reg_token);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
