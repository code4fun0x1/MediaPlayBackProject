package com.example.shashank.mediaplaybackproject;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;

public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener{


    private final IBinder mBinder=new LocalBinder();
    MediaPlayer player=null;
    boolean playing=false;
    int currentTrack=-1;
    String track,coverArt;




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equals(Constants.ACTION_PLAY)){
            track=intent.getStringExtra("track");
            coverArt=intent.getStringExtra("cover");
            if(currentTrack==-1){
                player=MediaPlayer.create(this, Uri.fromFile(new File(track)));
                player.setOnPreparedListener(this);
                player.prepareAsync();
            }else{
                player.start();
            }
        }else if(intent.getAction().equals(Constants.ACTION_PAUSE)){
            player.pause();
        }else if(intent.getAction().equals(Constants.ACTION_NEXT)){
            track=intent.getStringExtra("track");
            coverArt=intent.getStringExtra("cover");
            player.stop();
        }else if(intent.getAction().equals(Constants.ACTION_PREV)){
            track=intent.getStringExtra("track");
            coverArt=intent.getStringExtra("cover");
        }

        return START_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }

    public class LocalBinder extends Binder{

        MediaPlaybackService getService(){
            return MediaPlaybackService.this;
        }

    }


}
