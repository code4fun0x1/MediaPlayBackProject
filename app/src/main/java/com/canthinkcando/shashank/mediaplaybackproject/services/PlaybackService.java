package com.canthinkcando.shashank.mediaplaybackproject.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;

public class PlaybackService extends Service {

    private PlaybackListeners mListener;
    private MediaPlayer player;
    boolean playing =false;

    public void setOnPlaybackListeners(PlaybackListeners listeners){
        mListener=listeners;
    }

    public void setBooleanPausePlaySong(boolean x) {
        playing=x;
    }

    public void playPausedSong() {
        player.start();
    }

    public void pausePlayingSong() {
        player.pause();
    }

    public interface PlaybackListeners{
        void onPlayerPrepared(MediaPlayer mp);
        void onSongCompleted(MediaPlayer mp);
         void onSeekProgress(int seek);
         void notifyCurrent(int p);
    }

    //this is the Binder which is returned to the client
    // usning this binder client can call thie oublic methods of the Service
    PlaybackBinder playbackBinder = new PlaybackBinder();

    public PlaybackService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return playbackBinder;
    }

    public class PlaybackBinder extends Binder{
        public PlaybackService getService(){
            //retrurns the current binder objectto the clirent
            return PlaybackService.this;
        }
    }


    public void playSong(String data, final int p, final int seek){
//        if(!isNotificationVisible()){
//            showNotification();
//        }
        //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
        try {
            if(playing)
                player.release();
            player = MediaPlayer.create(PlaybackService.this, Uri.fromFile(new File(data)));
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mListener.onPlayerPrepared(mp);
                    mListener.notifyCurrent(p);
                    player.seekTo(seek);
                    if(player.getDuration()!=-1){
                        mListener.onSeekProgress(seek);
                    }else {
                        mListener.onSeekProgress(0);
                    }
                    playing=true;
                    player.start();
                }
            });
            player.prepareAsync();

        }catch (IllegalStateException e){
            player.seekTo(seek);
            playing=true;
            player.start();
            mListener.notifyCurrent(p);
//            stop.setImageResource(R.drawable.ic_pause_black_48dp);
//            remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
//            compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
//            nManager.notify(0,notification.build());
            mListener.onPlayerPrepared(player);
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               mListener.onSongCompleted(mp);
            }
        });


    }


}
