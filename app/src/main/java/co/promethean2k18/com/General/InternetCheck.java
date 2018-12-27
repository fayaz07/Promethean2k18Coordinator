package co.promethean2k18.com.General;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {

    Context context;
    NetworkInfo info;
    ConnectivityManager connectivityManager;
    boolean isNetworkactive;

    public InternetCheck(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isIsInternetAvailable() {
        info = connectivityManager.getActiveNetworkInfo();
        isNetworkactive = info != null && info.isConnectedOrConnecting();
        return isNetworkactive;
    }
}
