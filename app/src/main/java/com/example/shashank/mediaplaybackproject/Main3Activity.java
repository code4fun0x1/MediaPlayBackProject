package com.example.shashank.mediaplaybackproject;

import android.content.ContentResolver;
import android.database.Cursor;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.shashank.mediaplaybackproject.model.Song;

import java.io.File;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    Cursor cursor,albumCursor;
    ArrayList<Song> listSong=null;
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
    private ViewPager mViewPager;
    AsyncTask<Void,Void,ArrayList<Song>> songFetch=null;
    LibraryFragment libraryFragment=null;
    AlbumInfoFragment albumInfoFragment;
    SuggestFragment suggestFragment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

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
                listSong=new ArrayList<>();
                listSong=songs;




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
                    stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    seekbar.setProgress(0);
                }
                if (current==listSong.size()-1){
                    //

                }else  if(current!=-1){
                    current++;
                    playSong(listSong.get(current).getDATA(),current,0);
                } else{
                    playSong(listSong.get(0).getDATA(),0,0);

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seektime=0;

                if (current==-1 || current ==0){
                    //   playSong(listSong.get(current).getDATA(),current,0);

                }else {
                    current--;
                    if(playing){
                        player.stop();
                        playing=false;
                        stop.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                        seekbar.setProgress(0);
                    }

                    playSong(listSong.get(current).getDATA(),current,0);
                }
            }
        });









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
                        playSong(listSong.get(current).getDATA(),current,0);
                    }
                });
                return libraryFragment;
            }
            if(position==1){
                if(albumInfoFragment==null)
                    return AlbumInfoFragment.newInstance();
                else{
                    return albumInfoFragment;
                }
            }
            if(position==2){
                if(suggestFragment==null)
                    return SuggestFragment.newInstance();
                else{
                    return suggestFragment;
                }
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
                    return "SUGGESTION";
            }
            return null;
        }
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



    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }







}
