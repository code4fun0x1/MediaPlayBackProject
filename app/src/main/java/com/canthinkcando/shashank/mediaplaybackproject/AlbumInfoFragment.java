package com.canthinkcando.shashank.mediaplaybackproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.canthinkcando.shashank.mediaplaybackproject.model.FetchSong;
import com.canthinkcando.shashank.mediaplaybackproject.model.IndividualFetchSong;
import com.canthinkcando.shashank.mediaplaybackproject.model.Song;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumInfoFragment extends Fragment {


    public AlbumInfoFragment() {
        // Required empty public constructor
    }



    FetchSong fetchSong=null;
    RecyclerView rv;
    View mainView=null;
    TextView albumName,artistName;
    ImageView cover;
    LayoutInflater inflater;
    SongAdapter adapter=null;
    Song song=null;

    public static AlbumInfoFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AlbumInfoFragment fragment = new AlbumInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView=inflater.inflate(R.layout.fragment_album_info, container, false);
        rv=(RecyclerView)mainView.findViewById(R.id.body);
        artistName=(TextView)mainView.findViewById(R.id.artist_name);
        albumName=(TextView)mainView.findViewById(R.id.album_name);
        cover=(ImageView)mainView.findViewById(R.id.cover);

        this.inflater=inflater;
        fetchSong=new FetchSong();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter=new SongAdapter());
        return mainView;
    }




    public class SongHolder extends RecyclerView.ViewHolder{

        View v;
        public TextView title,duration;
       // ImageView artView;
        public SongHolder(View itemView) {
            super(itemView);
            v=itemView;
          //  artView=(ImageView)itemView.findViewById(R.id.album_art);
            title=(TextView)itemView.findViewById(R.id.song_name);

            duration=(TextView)itemView.findViewById(R.id.duration);

        }
    }


    public class SongAdapter extends RecyclerView.Adapter<SongHolder>{


        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=inflater.inflate(R.layout.album_song_card,parent,false);

            return new SongHolder(v);
        }

        @Override
        public void onBindViewHolder(final SongHolder holder, final int position) {
            holder.title.setText(fetchSong.getSongs().get(position).getSongName());

            int min=Integer.parseInt(fetchSong.getSongs().get(position).getDuration())/60;
            int sec=Integer.parseInt(fetchSong.getSongs().get(position).getDuration())%60;
            holder.duration.setText(min+" Min "+sec+" Sec");



//            try {
//                Picasso.with(getContext()).load(new File(s.getArtPath())).fit().into(holder.artView);
//            }catch(Exception e){
//                Log.d(TAG, "onBindViewHolder: "+e.toString());
//                Picasso.with(getContext()).load(R.drawable.photo).fit().into(holder.artView);
//            }


        }

        @Override
        public int getItemCount() {
            return fetchSong.getSongs().size();
        }
    }

    void getSong(final Song song) throws UnsupportedEncodingException, MalformedURLException {
        artistName.setText(song.getARTIST());
        albumName.setText(song.getALBUM());
        try {

            Picasso.with(getContext()).load(R.drawable.splash).fit().into(cover);
        } catch (Exception e) {

        }
        if (song != null) {

            fetchSong = new FetchSong();
       //     Log.d(TAG, "Artist "+song.getARTIST() + "&album=" + song.getALBUM());

            String u=null;


            try {
                u = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=bcf006ec84c572c33264e09c026d3f11&artist="
                        +URLEncoder.encode( song.getARTIST().trim(), "UTF-8")
                        + "&album=" + URLEncoder.encode( song.getALBUM().trim(), "UTF-8")+ "&format=json";
        //        Log.d(TAG, "u = "+ u);

            } catch (Exception e) {

            }
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, u, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //mTxtDisplay.setText("Response: " + response.toString());
                          // Log.d(TAG, "onResponse: " + response.toString());
                            fetchSong.setNameAlbum(song.getALBUM());
                            fetchSong.setNameArtist(song.getARTIST());
                            artistName.setText(song.getARTIST());
                            albumName.setText(song.getALBUM());

                            try {
                                response=response.getJSONObject("album");

                                fetchSong.setUrl(response.getString("url"));
                                fetchSong.setPlayCount(response.getString("playcount"));
                                JSONArray images=response.getJSONArray("image");
                                for(int i=0;i<images.length();i++){
                                    JSONObject temp= images.getJSONObject(i);
                                    if(temp.getString("size").equals("large")){
                                        fetchSong.setCover(temp.getString("#text"));
                                    }
                                }

                                JSONArray tracks=response.getJSONObject("tracks").getJSONArray("track");
                                ArrayList<IndividualFetchSong> s = new ArrayList<>();
                                for(int i=0;i<tracks.length();i++){
                                    JSONObject temp= tracks.getJSONObject(i);
                                    IndividualFetchSong t= new IndividualFetchSong();
                                    t.setDuration(temp.getString("duration"));
                                    t.setSongName(temp.getString("name"));
                                    s.add(t);

                                }
                                fetchSong.setSongs(s);
                                try {
                                    //Toast.makeText(getContext(),fetchSong.getCover(),Toast.LENGTH_LONG).show();
                                    Picasso.with(getContext().getApplicationContext()).load(fetchSong.getCover()).fit().into(cover);
                                } catch (Exception e) {

                                }


                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Toast.makeText(getContext(), "Volley Error", Toast.LENGTH_SHORT).show();
                        }
                    });

// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);
        }


    }

}
