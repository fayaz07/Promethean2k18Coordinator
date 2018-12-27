package co.promethean2k18.com.General;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import co.promethean2k18.com.R;

public class Login extends AppCompatActivity {

    EditText email,password;
    Button login,reset;
    ProgressDialog progressDialog;
    ScrollView layout;
    InternetCheck internetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        login = findViewById(R.id.loginButton);
        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);

        reset = findViewById(R.id.resetPassword);

        layout = findViewById(R.id.parentLogin);

        internetCheck = new InternetCheck(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) ){
                    Snackbar.make(layout,"Enter valid credentials",Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!internetCheck.isIsInternetAvailable()){
                    Snackbar.make(layout,"No internet connection",Snackbar.LENGTH_LONG).show();
                    return;
                }
                progressDialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(),Home.class));
                                    finish();
                                }else{
                                    Snackbar.make(layout,"Invalid username/password",Snackbar.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText()) || !email.getText().toString().contains("@")){
                    Snackbar.make(layout,"Enter valid email address",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Snackbar.make(layout,"Check your mail for password reset link",Snackbar.LENGTH_SHORT).show();
                                }else{
                                    Snackbar.make(layout,"Contact admin",Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
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
