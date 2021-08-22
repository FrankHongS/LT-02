package com.hon.librarytest02.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.hon.librarytest02.LibraryTest;
import com.hon.librarytest02.R;

/**
 * Created by Frank_Hon on 3/7/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class Util {

    public static String getResourceString(int resId) {
        return LibraryTest.sContext.getResources().getString(resId);
    }

    public static int dp2px(float value) {
        float scale = Resources.getSystem().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void createNotificationChannel(String channelId,
                                                 String channelName,
                                                 int importance) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, importance);
        NotificationManager notificationManager =
                (NotificationManager) LibraryTest.sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

    }

    public static Notification createNotification(Class<? extends Activity> target,
                                                  String channelId,
                                                  String contentTitle,
                                                  String contentText) {
        Intent notificationIntent = new Intent(LibraryTest.sContext, target);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(LibraryTest.sContext, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(LibraryTest.sContext, channelId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                // important!, not showing if not set
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_ALL;

        return notification;
    }
}
