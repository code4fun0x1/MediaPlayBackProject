package com.example.shashank.mediaplaybackproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.mediaplaybackproject.model.Song;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.example.shashank.mediaplaybackproject.Main3Activity.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {


    public LibraryFragment() {
        // Required empty public constructor
    }




    static ArrayList<Song> listSong=new ArrayList<>();
    RecyclerView rv;
    View mainView=null;
    LayoutInflater inflater;
    SongAdapter adapter=null;

    private OnSongClickListener songClickListener;

    interface OnSongClickListener{
        void onSongClick(int pos);
    }

    void setOnSongClickListener(OnSongClickListener listener){
        songClickListener=listener;
    }

    private OnFragmentReady readyListener;

    interface OnFragmentReady{
        void onReady();
    }

    void setOnFragmentReady(OnFragmentReady listener){
        readyListener=listener;
    }


    public static LibraryFragment newInstance() {

        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        Toast.makeText(getContext(), ""+listSong.size(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        readyListener.onReady();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView=inflater.inflate(R.layout.fragment_library, container, false);
        rv=(RecyclerView)mainView.findViewById(R.id.body);
        this.inflater=inflater;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter=new SongAdapter());
        return mainView;
    }


    public class SongHolder extends RecyclerView.ViewHolder{

        View v;
        public TextView title,artist,album;
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
            View v=inflater.inflate(R.layout.song_card,parent,false);

            return new SongHolder(v);
        }

        @Override
        public void onBindViewHolder(final SongHolder holder, final int position) {
            final Song s=listSong.get(position);
            final int p=position;
            holder.album.setText(s.getALBUM());
            holder.title.setText(s.getTITLE());
            holder.artist.setText(s.getARTIST());
            try {
                Picasso.with(getContext()).load(new File(s.getArtPath())).fit().into(holder.artView);
            }catch(Exception e){
                Log.d(TAG, "onBindViewHolder: "+e.toString());
            }
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        songClickListener.onSongClick(position);
//                    RemoteViews notificationView=new RemoteViews(getPackageName(),R.layout.notification);
                    //     notificationView.setImageViewBitmap(R.id.album_art, BitmapFactory.decodeFile(coverArt));
                }
            });

        }

        @Override
        public int getItemCount() {
            return listSong.size();
        }
    }


    void sendSongs(ArrayList<Song> temp){
        listSong=temp;
        adapter.notifyDataSetChanged();
    }

}
