package com.hon.librarytest02.service

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.hon.librarytest02.R
import androidx.media.app.NotificationCompat as MediaNotificationCompat

/**
 * Created by shuaihua on 2021/1/27 5:45 PM
 * Email: shuaihua@staff.sina.com.cn
 */
private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empey_root_id"
private const val TAG = "MediaPlaybackService"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(MySessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }

        // Given a media session and its context (usually the component containing the session)
        // Create a NotificationCompat.Builder

        // Get the session's metadata
        val controller = mediaSession!!.controller
//        val mediaMetadata = controller.metadata
//        val description = mediaMetadata.description

        val builder = NotificationCompat.Builder(this, "player").apply {
            // Add the metadata for the currently playing track
            setContentTitle("title")
            setContentText("subtitle")
            setSubText("description")
//            setLargeIcon()

            // Enable launching the player by clicking the notification
            setContentIntent(controller.sessionActivity)

            // Stop the service when the notification is swiped away
            setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this@MediaPlaybackService,
                            PlaybackStateCompat.ACTION_STOP
                    )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Add an app icon and set its accent color
            // Be careful about the color
            setSmallIcon(R.drawable.ic_android_black_24dp)
            color = ContextCompat.getColor(this@MediaPlaybackService, R.color.colorPrimaryDark)

            // Add a pause button
            addAction(
                    NotificationCompat.Action(
                            R.drawable.ic_baseline_pause_circle_filled_24,
                            "Pause",
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this@MediaPlaybackService,
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                            )
                    )
            )

            // Take advantage of MediaStyle features
            setStyle(MediaNotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession!!.sessionToken)
                    .setShowActionsInCompactView(0)

                    // Add a cancel button
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this@MediaPlaybackService,
                                    PlaybackStateCompat.ACTION_STOP
                            )
                    )
            )
        }

        // Display the notification and place the service in the foreground
        startForeground(1, builder.build())

    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        return true
    }

    override fun onLoadChildren(parentMediaId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        //  Browsing not allowed
        if (MY_EMPTY_MEDIA_ROOT_ID == parentMediaId) {
            result.sendResult(null)
            return
        }

        // Assume for example that the music catalog is already loaded/cached.

        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID == parentMediaId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems)
    }

    class MySessionCallback : MediaSessionCompat.Callback() {

        override fun onPrepare() {
            super.onPrepare()
        }

        override fun onPause() {
            super.onPause()
        }

        override fun onPlay() {
            super.onPlay()
        }

    }
}