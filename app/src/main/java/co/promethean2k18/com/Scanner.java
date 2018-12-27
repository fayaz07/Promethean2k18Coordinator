package co.promethean2k18.com;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

//import com.budiyev.android.codescanner.CodeScanner;
//import com.budiyev.android.codescanner.CodeScannerView;
//import com.budiyev.android.codescanner.DecodeCallback;
//import com.google.zxing.Result;

public class Scanner extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 123;
    //private CodeScanner mCodeScanner;

    private static final int REQUEST_CODE_QR_SCAN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

//        final CodeScannerView scannerView = findViewById(R.id.scanner_view);
//        mCodeScanner = new CodeScanner(this, scannerView);
//        mCodeScanner.setDecodeCallback(new DecodeCallback() {
//            @Override
//            public void onDecoded(@NonNull final Result result) {
//                Scanner.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                      //  parseRegToken(result.getText());
////                        Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_SHORT).show();
//                        mCodeScanner.stopPreview();
//                        //startActivityForME(result.getText());
//                       // Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_SHORT).show();
//                        startActivityForME(result.getText());
//
//                    }
//                });
//                startActivityForME(result.getText());
//            }
//        });
//        scannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mCodeScanner.startPreview();
//            }
//        });


    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
  //      mCodeScanner.releaseResources();
        super.onPause();
    }

    private void startActivityForME(String uid){
//        mCodeScanner.releaseResources();
        finish();

        Intent intent = new Intent(getApplicationContext(),KitLunchData.class);
        intent.putExtra("uid",uid);
        startActivity(intent);
    }

    private void parseRegToken(String token){
        if (!token.contains(":")){
            Toast.makeText(this, "Invalid registration token", Toast.LENGTH_SHORT).show();
            return;
        }
        int i;
        String regtoken="", eventName="";
        for (i=0;token.charAt(i)!=':';i++){
            regtoken+=token.charAt(i);
        }

        for (int j=i;j<token.length();j++){
            eventName += token.charAt(j);
        }

        Toast.makeText(this, "Registration Token: " + regtoken+ "\nEvent Name: " + eventName, Toast.LENGTH_LONG).show();
    }
}
