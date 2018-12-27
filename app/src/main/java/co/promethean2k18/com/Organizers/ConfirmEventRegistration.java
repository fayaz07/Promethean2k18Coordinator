package co.promethean2k18.com.Organizers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

//import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import co.promethean2k18.com.R;
//import info.androidhive.barcode.BarcodeReader;

public class ConfirmEventRegistration extends AppCompatActivity {// implements BarcodeReader.BarcodeReaderListener {

    String token;
    ProgressDialog progressDialog;

    //private BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_event_registration);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayHomeAsUpEnabled(true);

        token = getIntent().getStringExtra("token");

      //  barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);



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

    /*
    @Override
    public void onScanned(Barcode barcode) {
        // play beep sound
        barcodeReader.playBeep();
        barcodeReader.pauseScanning();
        String bb = barcode.rawValue;
        Log.i("barcode",bb);
        String tokenIGot ="";
        for (int i=0;i<bb.length();i++){
            if (bb.charAt(i)==':'){
                break;
            }else {
                tokenIGot += bb.charAt(i);
            }
        }

        if (token.equals(tokenIGot)){
            setAsParticipated();
        }
    }

    private void setAsParticipated() {


    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        //Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }
    }

    */
}
