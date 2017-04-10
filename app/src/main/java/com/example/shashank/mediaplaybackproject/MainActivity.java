package com.example.shashank.mediaplaybackproject;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shashank.mediaplaybackproject.model.Song;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Cursor cursor,albumCursor;
    ArrayList<Song> listSong=new ArrayList<>();
    RecyclerView rv;
    public static final String TAG="SONG";
    ImageButton stop;
    ImageButton next,prev;
    MediaPlayer player;
    SeekBar seekbar;
    boolean playing=false;
    int current=-1;
    int seektime=0;
    Handler mHandler;
    Runnable runnable;
    MediaPlaybackService myService;
    boolean bound=false;
    Notification status;
    Intent playPauseIntent,nextIntent,prevIntent;
    PendingIntent pplayPauseIntent,pnextIntent,pprevIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler= new Handler();



        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.splash);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        rv=(RecyclerView)findViewById(R.id.body);
        stop=(ImageButton)findViewById(R.id.stop);
        next=(ImageButton)findViewById(R.id.next);
        prev=(ImageButton)findViewById(R.id.prev);
        seekbar=(SeekBar)findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(playing){

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(player!=null){

                    seektime=seekbar.getProgress();
                    player.seekTo(seekBar.getProgress() );
                }

            }
        });




        ContentResolver resolver=getContentResolver();
        cursor=resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        albumCursor=resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,null);
        //data is the path of the file on the disk

        AsyncTask<Void,Void,ArrayList<Song>> songFetch=new AsyncTask<Void, Void, ArrayList<Song>>() {
            @Override
            protected ArrayList<Song> doInBackground(Void... params) {
                int data=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int id=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int name=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int album=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int artist=cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                int art=albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                int artname=albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                int artId=albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
                ArrayList<Song> s=new ArrayList<>();

                while(cursor.moveToNext()){
                    String albumname=cursor.getString(album);
                    String title=cursor.getString(name);
                    String songartist=cursor.getString(artist);
                    String songid=cursor.getString(id);
                    String songfilepath=cursor.getString(data);
                    s.add(new Song(songfilepath,albumname,null,songid,songartist,title,null));

                }
                while(albumCursor.moveToNext()){
                    String albumartpath=albumCursor.getString(art);
                    String albumname=albumCursor.getString(artname);
                    for(int i=0;i<s.size();i++){
                        if(s.get(i).getALBUM().equals(albumname))
                            s.get(i).setArtPath(albumartpath);
                    }
                }
                return s;
            }

            @Override
            protected void onPostExecute(ArrayList<Song> songs) {
                super.onPostExecute(songs);
                listSong=songs;
                rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rv.setAdapter(new SongAdapter());

            }
        };
        songFetch.execute();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing){
                    seektime=player.getCurrentPosition();
                    player.pause();
                    playing=false;
                    stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                } else if (current!=-1){

                   // Toast.makeText(MainActivity.this,"YO",Toast.LENGTH_LONG).show();
                    player.start();
                    playing=true;
                    playCycle();
                   // playSong(listSong.get(current).getDATA(),current,seektime);
                    stop.setImageResource(R.drawable.ic_pause_black_48dp);
                }else if (listSong.size()!=0){
                    playSong(listSong.get(0).getDATA(),0,0);
                    stop.setImageResource(R.drawable.ic_pause_black_48dp);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seektime=0;

                if(playing){
                    player.stop();
                    playing=false;
                }
                if (current==listSong.size()){
                   //

                }else  if(current!=-1){
                    playSong(listSong.get(current+1).getDATA(),current+1,0);
                } else{
                    playSong(listSong.get(0).getDATA(),0,0);

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seektime=0;

                if(playing){
                    player.stop();
                    playing=false;
                }
                if (current==-1 || current ==0){
                 //   playSong(listSong.get(current).getDATA(),current,0);

                }else  if(current!=-1){
                    playSong(listSong.get(current-1).getDATA(),current-1,0);
                }
            }
        });







    }

    void playCycle(){
        Log.d(TAG, "playCycle: "+player.getCurrentPosition());

        seekbar.setProgress(player.getCurrentPosition());
        if(playing){
             runnable=new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            mHandler.postDelayed(runnable,1000);
        }

    }


        void playSong(String data,int p,int seek){

            //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
            try {
                if(playing)
                    player.release();

                player = MediaPlayer.create(MainActivity.this, Uri.fromFile(new File(data)));

                playing=true;
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        seekbar.setMax(mp.getDuration());
                        seekbar.setProgress(0);
                        playCycle();
                    }
                });
                player.prepareAsync();
                player.seekTo(seek);
                if(player.getDuration()!=-1){
                    seekbar.setProgress(seek);
                }else {
                    seekbar.setProgress(0);

                }

                player.start();
                current=p;
                stop.setImageResource(R.drawable.ic_pause_black_48dp);
            }catch (IllegalStateException e){
                player.seekTo(seek);

                player.start();
                current=p;
                stop.setImageResource(R.drawable.ic_pause_black_48dp);
            }
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    seektime=seekbar.getMax();
                }
            });


        }


        public class SongHolder extends RecyclerView.ViewHolder{

            View v;
            public  TextView title,artist,album;
            ImageView artView;
            public SongHolder(View itemView) {
                super(itemView);
                v=itemView;
                artView=(ImageView)itemView.findViewById(R.id.album_art);
                title=(TextView)itemView.findViewById(R.id.song_name);
                artist=(TextView)itemView.findViewById(R.id.artist);
                album=(TextView)itemView.findViewById(R.id.album);

            }
        }


        public class SongAdapter extends RecyclerView.Adapter<SongHolder>{


            @Override
            public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=getLayoutInflater().inflate(R.layout.song_card,parent,false);

                return new SongHolder(v);
            }

            @Override
            public void onBindViewHolder(final SongHolder holder, int position) {
                final Song s=listSong.get(position);
                final int p=position;
                holder.album.setText(s.getALBUM());
                holder.title.setText(s.getTITLE());
                holder.artist.setText(s.getARTIST());
                try {
                    Picasso.with(MainActivity.this).load(new File(s.getArtPath())).fit().into(holder.artView);
                }catch(Exception e){
                    Log.d(TAG, "onBindViewHolder: "+e.toString());
                }
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seektime=0;
                        playSong(s.getDATA(),p,0);
                        RemoteViews notificationView=new RemoteViews(getPackageName(),R.layout.notification);
                   //     notificationView.setImageViewBitmap(R.id.album_art, BitmapFactory.decodeFile(coverArt));
                        }
                });

            }

            @Override
            public int getItemCount() {
                return listSong.size();
            }
        }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }






}
