package co.promethean2k18.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.promethean2k18.com.Data_.Events_model;
import co.promethean2k18.com.Data_.OrganizerProfile_model;
import co.promethean2k18.com.Data_.StoreMyData;
import co.promethean2k18.com.Data_.Urls;
import co.promethean2k18.com.General.InternetCheck;
import co.promethean2k18.com.Organizers.ConfirmEventRegistration;
import co.promethean2k18.com.Organizers.QuickManageMyEventRegistration;

public class SelectEvent extends AppCompatActivity {

    private ProgressDialog progressDialog;
    Intent intent;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Events_model> events_models;

    InternetCheck internetCheck;
    OrganizerProfile_model organizerProfile;
    List<String> eventsICanAccess;
    String whatIGonnaDo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_ican_access);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        organizerProfile = new OrganizerProfile_model();

        events_models = new ArrayList<>();
        events_models.clear();

        Intent intent = getIntent();
        whatIGonnaDo = intent.getStringExtra("type");


        eventsICanAccess = new ArrayList<>();
        eventsICanAccess.clear();

        internetCheck = new InternetCheck(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerEvents);
        swipeRefreshLayout = findViewById(R.id.swiperefreshEvent);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.purple), getResources().getColor(R.color.green));

        new DOwnlOad().execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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


    public void goToEventRegisterActivity(String eventId, String type) {
        if (whatIGonnaDo.equals("confirm")) {
            Intent intent = new Intent(getApplicationContext(), ConfirmEventRegistration.class);
            intent.putExtra("id", eventId);
            intent.putExtra("type", type);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), QuickManageMyEventRegistration.class);
            intent.putExtra("id", eventId);
            intent.putExtra("type", type);
            startActivity(intent);
        }

    }

    private void loadData() {
        if (!internetCheck.isIsInternetAvailable()) {
            Snackbar.make(swipeRefreshLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData();
                }
            }).show();
            return;
        }
        Log.d("tag", "loading started");
        events_models.clear();
        progressDialog.show();
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.initialize(this);
        Log.d("tag", "initialized fan");

        String urll = "";

        if (StoreMyData.events.size() == 0) {
            Toast.makeText(this, "Sorry, you can't access any events, contact administrator", Toast.LENGTH_SHORT).show();
        }

        Events_adapter adapter = new Events_adapter(getApplicationContext(), StoreMyData.events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        Log.d("tag", "setting adapter");

        progressDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);
    }

    //Adapter
    public class Events_adapter extends RecyclerView.Adapter<Events_adapter.Events_view> {

        Context context;
        ArrayList<Events_model> events_list;

        public Events_adapter(Context context, ArrayList<Events_model> events_list) {
            this.context = context;
            this.events_list = events_list;
        }

        @NonNull
        @Override
        public Events_view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);
            Events_adapter.Events_view cv = new Events_adapter.Events_view(view);
            return cv;
        }

        @Override
        public void onBindViewHolder(@NonNull final Events_view holder, final int position) {

            holder.eventName.setText(events_list.get(position).getEventName());
            holder.noOfParticipants.setText("Tags: " + events_list.get(position).getTags());
            Picasso.get().load(events_list.get(position).getImageUrl())
                    .fit()
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_event_available_black_24dp));
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
            holder.dept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (events_list.get(position).getEventName().equals("Hackthon on Smart City Technologies")
                            || events_list.get(position).getEventName().equals("Project Expo")
                            || events_list.get(position).getEventName().equals("Ideation")
                            || events_list.get(position).getEventName().equals("Poster Presentation")) {
                        goToEventRegisterActivity(events_list.get(position).getEventId(), "f");
                    } else {
                        goToEventRegisterActivity(events_list.get(position).getEventId(), "n");
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return events_list.size();
        }

        class Events_view extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView eventName, noOfParticipants;
            CardView dept;
            ProgressBar progressBar;

            public Events_view(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageViewTeam);
                dept = (CardView) itemView.findViewById(R.id.eventView);
                eventName = (TextView) itemView.findViewById(R.id.eventName);
                noOfParticipants = (TextView) itemView.findViewById(R.id.tags);
                progressBar = (ProgressBar) itemView.findViewById(R.id.imageProgressEvent);
            }
        }
    }

    class DOwnlOad extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadData();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < StoreMyData.eventsICanAccessIDs.size(); i++) {
                final int finalI = i;
                String url = "";
                int id = Integer.parseInt(StoreMyData.eventsICanAccessIDs.get(i));
                if (Integer.parseInt(StoreMyData.organizerProfile.getType()) == 1) {
                    //Normal event organizer
                    url = Urls.retrieveNEventByID;
                } else if(Integer.parseInt(StoreMyData.organizerProfile.getType()) == 2){
                    //Special event organizer
                    url = Urls.retrieveFEventbyid;
                }else if(Integer.parseInt(StoreMyData.organizerProfile.getType()) == 3){

                }
                Log.i("error", String.valueOf(i) + " " + StoreMyData.eventsICanAccessIDs.get(i));
                final int finalI1 = i;


            }
            return null;
        }
    }
}
