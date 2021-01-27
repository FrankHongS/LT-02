package com.hon.librarytest02.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

public class MediaService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogger.d("onStartCommand");
        // Create a media session. NotificationCompat.MediaStyle
        // PlayerService is your own Service or Activity responsible for media playback.
        MediaSession mediaSession = new MediaSession(this, "PlayerService");

        // Create a MediaStyle object and supply your media session token to it.
        Notification.MediaStyle mediaStyle = new Notification.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0,1,2)
                ;

        // Create a Notification which is styled by your MediaStyle object.
        // This connects your media session to the media controls.
        // Don't forget to include a small icon.
        PendingIntent pauseIntent = PendingIntent.getBroadcast(this, 1,
                new Intent(this, MediaReceiver.class).setAction("Pause")
                , PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action pauseAction = new Notification.Action
                .Builder(Icon.createWithResource(this, R.drawable.ic_baseline_pause_circle_filled_24),
                "Pause",
                pauseIntent)
                .build();
        PendingIntent nextIntent = PendingIntent.getBroadcast(this, 1,
                new Intent(this, MediaReceiver.class).setAction("Next")
                , PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action nextAction = new Notification.Action
                .Builder(Icon.createWithResource(this, R.drawable.ic_baseline_skip_next_24),
                "Next",
                nextIntent)
                .build();
        PendingIntent previousIntent = PendingIntent.getBroadcast(this, 1,
                new Intent(this, MediaReceiver.class).setAction("Previous")
                , PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action previousAction = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_baseline_skip_previous_24),
                "Previous",
                previousIntent)
                .build();
        Notification notification = new Notification.Builder(this, "player")
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .addAction(previousAction)
                .addAction(pauseAction)
                .addAction(nextAction)
                .setContentTitle("Wonderful music")
                .setContentText("My Awesome Band")
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
