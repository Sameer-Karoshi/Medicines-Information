package com.sameer.Medicines_Information;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GettingDeviceTokenService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token");

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
}
