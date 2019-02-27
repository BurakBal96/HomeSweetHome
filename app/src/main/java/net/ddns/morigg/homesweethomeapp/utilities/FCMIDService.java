package net.ddns.morigg.homesweethomeapp.utilities;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MoriartyGG on 26.04.2018.
 */

public class FCMIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM Token: ", "Refreshed token: " + refreshedToken);

    }
}
