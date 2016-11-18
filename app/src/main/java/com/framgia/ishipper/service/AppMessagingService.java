package com.framgia.ishipper.service;

import android.content.Intent;

import com.framgia.ishipper.common.Log;
import com.framgia.ishipper.util.Const;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by HungNT on 10/21/16.
 */

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        sendBroadcastNewNotification(remoteMessage);
    }

    private void sendBroadcastNewNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_NEW_NOTIFICATION);
        intent.putExtra(Const.KEY_TITLE, remoteMessage.getNotification().getTitle());
        intent.putExtra(Const.KEY_BODY, remoteMessage.getNotification().getBody());
        sendBroadcast(intent);
    }
}
