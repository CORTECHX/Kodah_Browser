package com.cortechx.kodah;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;

public class ClipboardService extends Service{

    private final String tag = "[Kodah Clipboard Service] ";
    private final String CHANNEL_ID = "default";

    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        CharSequence text = null;

        if (cb.hasPrimaryClip()) {
            ClipData cd = cb.getPrimaryClip();

            text = cd.getItemAt(0).getText();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.kodah_icon)
                    .setContentTitle("JUST COPIED CODE?")
                    .setContentText("Tap to send to PC clipboard")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1000, builder.build());

            TCPClient client = new TCPClient();
            client.SendMessage(text.toString(), "");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
