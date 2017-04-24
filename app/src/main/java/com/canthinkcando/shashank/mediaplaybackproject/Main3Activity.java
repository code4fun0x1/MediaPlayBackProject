package com.canthinkcando.shashank.mediaplaybackproject;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.canthinkcando.shashank.mediaplaybackproject.intents.CustomIntentAction;
import com.canthinkcando.shashank.mediaplaybackproject.model.Song;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    IntentFilter filter;
    Cursor cursor,albumCursor;
    ArrayList<Song> listSong=new ArrayList<>();
    RecyclerView rv;
    public static final String TAG="SONG";
    ImageView playerThumbnail;
    TextView artist,song_name;
    ImageButton stop;
    ImageButton next,prev;
    MediaPlayer player;
    SeekBar seekbar;
    BroadcastReceiver playReceiver,nexReceiver,prevReceiver;
    boolean playing=false;
    int current=-1;
    int seektime=0;
    Handler mHandler;
    Runnable runnable;
    MediaPlaybackService myService;
    boolean bound=false;
    private ViewPager mViewPager;
    AsyncTask<Void,Void,ArrayList<Song>> songFetch=null;
    LibraryFragment libraryFragment=null;
    AlbumInfoFragment albumInfoFragment;
    TopTrackFragment topTrackFragment =null;
    RemoteViews remoteView,compactView;
    NotificationCompat.Builder notification;
    NotificationManager nManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        filter=new IntentFilter(getPackageName());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.splash);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        //old code
        mHandler= new Handler();
        playReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              //  Toast.makeText(Main3Activity.this, intent.getAction(), Toast.LENGTH_SHORT).show();
                    playPauseClick(null);
            }
        };
        nexReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            //    Toast.makeText(Main3Activity.this, intent.getAction(), Toast.LENGTH_SHORT).show();
                nextClick(null);
            }
        };
        prevReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            //    Toast.makeText(Main3Activity.this, intent.getAction(), Toast.LENGTH_SHORT).show();
                prevClick(null);
            }
        };




        stop=(ImageButton)findViewById(R.id.stop);
        next=(ImageButton)findViewById(R.id.next);
        prev=(ImageButton)findViewById(R.id.prev);
        seekbar=(SeekBar)findViewById(R.id.seekBar);
        playerThumbnail=(ImageView)findViewById(R.id.player_thumbnail);
        artist=(TextView)findViewById(R.id.artist);
        song_name=(TextView)findViewById(R.id.song_name);

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

         songFetch=new AsyncTask<Void, Void, ArrayList<Song>>() {
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

            }
        };
        songFetch.execute();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseClick(v);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClick(v);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClick(v);
            }
        });









    }


    void showNotification(){
        compactView=new RemoteViews(getPackageName(),R.layout.notification_small);
        remoteView =new RemoteViews(getPackageName(),R.layout.notification_expanded);
        //remoteView.setImageViewResource(R.id.album_art,R.drawable.photo);
        compactView.setImageViewResource(R.id.album_art,R.drawable.splash);

        //play_pause
        Intent playpauseIntent=new Intent();
        playpauseIntent.setAction(CustomIntentAction.ACTION_PLAY);
        PendingIntent piPlayPause=PendingIntent.getBroadcast(this,0,playpauseIntent,0);
        remoteView.setOnClickPendingIntent(R.id.play_pause, piPlayPause);
        compactView.setOnClickPendingIntent(R.id.play_pause,piPlayPause);
        //next intent
        Intent nextIntent=new Intent();
        nextIntent.setAction(CustomIntentAction.ACTION_NEXT);
        PendingIntent piNext=PendingIntent.getBroadcast(this,0,nextIntent,0);
        remoteView.setOnClickPendingIntent(R.id.next, piNext);
        compactView.setOnClickPendingIntent(R.id.next,piNext);
        //prev intent
        Intent prevIntent=new Intent();
        prevIntent.setAction(CustomIntentAction.ACTION_PREV);
        PendingIntent piPrev=PendingIntent.getBroadcast(this,0,prevIntent,0);
        remoteView.setOnClickPendingIntent(R.id.prev, piPrev);
        compactView.setOnClickPendingIntent(R.id.prev,piPrev);



        nManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //actual notificationd
        Intent i=new Intent(this,Main3Activity.class);
        PendingIntent notificationPIntent=PendingIntent.getActivity(this,100,i,PendingIntent.FLAG_UPDATE_CURRENT);
        notification= new NotificationCompat.Builder(this);
        notification.setContent(compactView);
        notification.setAutoCancel(true);
        notification.setCustomBigContentView(remoteView);
        notification.setSmallIcon(R.drawable.splash);
        nManager.notify(0,notification.build());
    }


    private void prevClick(View v) {

        seektime=0;

        if (current==-1 || current ==0){
            //   playSong(listSong.get(current).getDATA(),current,0);

        }else {
            current--;
            if(albumInfoFragment!=null){
                try {
                    albumInfoFragment.getSong(listSong.get(current));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            if(playing){
                player.stop();
                playing=false;
                stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                seekbar.setProgress(0);
            }
            updatePlayerDetails(current);
            playSong(listSong.get(current).getDATA(),current,0);
        }
    }

    private void nextClick(View v) {
        seektime=0;

        if(playing){
            player.stop();
            playing=false;
            stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            seekbar.setProgress(0);
        }
        if (current==listSong.size()-1){
            //

        }else  if(current!=-1){
            current++;

            if(albumInfoFragment!=null){
                try {
                    albumInfoFragment.getSong(listSong.get(current));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            updatePlayerDetails(current);
            playSong(listSong.get(current).getDATA(),current,0);
        } else{
            updatePlayerDetails(0);

            playSong(listSong.get(0).getDATA(),0,0);

        }
    }

    private void playPauseClick(View v) {
        if (playing){

            seektime=player.getCurrentPosition();
            player.pause();
            playing=false;
            stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_black_48dp);
            compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_black_48dp);

            nManager.notify(0,notification.build());

        } else if (listSong.size()!=0){
            if (current!=-1){
                // Toast.makeText(MainActivity.this,"YO",Toast.LENGTH_LONG).show();
                player.start();
                playing=true;
                playCycle();
                updatePlayerDetails(current);
                // playSong(listSong.get(current).getDATA(),current,seektime);
                stop.setImageResource(R.drawable.ic_pause_black_48dp);
                compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
                remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
                nManager.notify(0,notification.build());

            }else {
                current=0;
                updatePlayerDetails(current);
                playSong(listSong.get(0).getDATA(),0,0);
                stop.setImageResource(R.drawable.ic_pause_black_48dp);
                remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
                compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
                nManager.notify(0,notification.build());

            }
        }else {
            nManager.cancelAll();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0){
                if(libraryFragment==null){
                    libraryFragment= LibraryFragment.newInstance();

                }
                libraryFragment.setOnFragmentReady(new LibraryFragment.OnFragmentReady() {
                    @Override
                    public void onReady() {
                        if(libraryFragment!=null){
                            libraryFragment.sendSongs(listSong);
                        }
                    }
                });
                libraryFragment.setOnSongClickListener(new LibraryFragment.OnSongClickListener() {
                    @Override
                    public void onSongClick(int pos) {
                        seektime=0;
                        current=pos;
                        if(albumInfoFragment!=null ){
                            try {
                                albumInfoFragment.getSong(listSong.get(current));

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                        playSong(listSong.get(current).getDATA(),current,0);
                        updatePlayerDetails(current);
                    }
                });
                return libraryFragment;
            }
            if(position==1){
                if(albumInfoFragment==null)
                    albumInfoFragment = AlbumInfoFragment.newInstance();
                try {
                    if(current!=-1){
                        albumInfoFragment.getSong(listSong.get(current));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return albumInfoFragment;
            }
            if(position==2){
                if(topTrackFragment ==null) {
                    topTrackFragment = TopTrackFragment.newInstance();
                    topTrackFragment.setOnFragmentReady(new TopTrackFragment.OnFragmentReady() {
                        @Override
                        public void onReady() {
                            if(current!=-1)
                                topTrackFragment.getArtist(listSong.get(current).getARTIST());
                        }
                    });
                }

                return topTrackFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LIBRARY";
                case 1:
                    return "ALBUM INFO";
                case 2:
                    return "TOP";
            }
            return null;
        }
    }

    void playCycle(){
     //   Log.d(TAG, "playCycle: "+player.getCurrentPosition());

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
        if(!isNotificationVisible()){
            showNotification();
        }
        //   Toast.makeText(getApplicationContext(),s.getDATA().toString(),Toast.LENGTH_LONG).show();
        try {
            if(playing)
                player.release();

            player = MediaPlayer.create(Main3Activity.this, Uri.fromFile(new File(data)));

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
            remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
            compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
            nManager.notify(0,notification.build());
        }catch (IllegalStateException e){
            player.seekTo(seek);

            player.start();
            current=p;


            stop.setImageResource(R.drawable.ic_pause_black_48dp);

            remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
            compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_pause_black_48dp);
            nManager.notify(0,notification.build());
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                remoteView.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_black_48dp);
                compactView.setImageViewResource(R.id.play_pause,R.drawable.ic_play_arrow_black_48dp);
                nManager.notify(0,notification.build());
                seektime=seekbar.getMax();
            }
        });


    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(Main3Activity.this, 100, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    void updatePlayerDetails(int pos){

        if(!isNotificationVisible()){
            showNotification();
        }

        Song song=listSong.get(pos);
        artist.setText(song.getARTIST());
        song_name.setText(song.getTITLE());
        try {
            Picasso.with(getApplicationContext()).load(new File(song.getArtPath())).fit().into(playerThumbnail);
        }catch(Exception e){
          //  Log.d(TAG, "onBindViewHolder: "+e.toString());
            Picasso.with(getApplicationContext()).load(R.drawable.splash).fit().centerInside().into(playerThumbnail);
        }
        remoteView.setTextViewText(R.id.song_title,song.getTITLE());
        remoteView.setTextViewText(R.id.song_artist,song.getARTIST());
        compactView.setTextViewText(R.id.song_title,song.getTITLE());
        compactView.setTextViewText(R.id.song_artist,song.getARTIST());
        try {
            remoteView.setImageViewBitmap(R.id.album_art, BitmapFactory.decodeFile(String.valueOf(new File(song.getArtPath()))));
        }catch(Exception e){
            //   Log.d(TAG, "onBindViewHolder: "+e.toString());
            remoteView.setImageViewResource(R.id.album_art,R.drawable.splash);
        }
        nManager.notify(0,notification.build());

    }



    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(playReceiver,new IntentFilter(CustomIntentAction.ACTION_PLAY));
        registerReceiver(nexReceiver,new IntentFilter(CustomIntentAction.ACTION_NEXT));
        registerReceiver(prevReceiver,new IntentFilter(CustomIntentAction.ACTION_PREV));

    }


    @Override
    protected void onStop() {
      //  unregisterReceiver(playReceiver);
       // unregisterReceiver(prevReceiver);
        //unregisterReceiver(nexReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
          unregisterReceiver(playReceiver);
         unregisterReceiver(prevReceiver);
        unregisterReceiver(nexReceiver);
        nManager.cancelAll();
        super.onDestroy();
    }
}
