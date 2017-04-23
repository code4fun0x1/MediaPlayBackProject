package com.canthinkcando.shashank.mediaplaybackproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.canthinkcando.shashank.mediaplaybackproject.model.TopSong;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import static com.canthinkcando.shashank.mediaplaybackproject.Main3Activity.TAG;



public class TopTrackFragment extends Fragment {

    public boolean isVisible=false;

    private OnFragmentReady readyListener;

    interface OnFragmentReady{
        void onReady();
    }

    void setOnFragmentReady(OnFragmentReady listener){
        readyListener=listener;
    }


    @Override
    public void onStart() {
        super.onStart();
        readyListener.onReady();
        isVisible=true;
        Log.d(TAG, "onStart: ");

    }

    @Override
    public void onPause() {
        super.onPause();
        isVisible=false;
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    public TopTrackFragment() {
        // Required empty public constructor
    }

    public static TopTrackFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TopTrackFragment fragment = new TopTrackFragment();
        fragment.setArguments(args);
        return fragment;
    }


    ArrayList<TopSong> topSongs=new ArrayList<>();
    RecyclerView rv;
    View mainView=null;
    LayoutInflater inflater;
    SongAdapter adapter=null;
    String ARTIST=null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_top_tracks, container, false);
        rv=(RecyclerView)mainView.findViewById(R.id.body);
        this.inflater=inflater;
        rv.setLayoutManager(new GridLayoutManager(getContext(),2));
        rv.setAdapter(adapter=new SongAdapter());
        return mainView;
    }




    public void getArtist(String artist){
        if((ARTIST!=null && artist.equals(ARTIST))){
            return;
        }else{
            topSongs=null;
            topSongs=new ArrayList<>();
        }

        if(artist!=null && artist.length()!=0){

            String u=null;


            try {
                u = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist="+
                        URLEncoder.encode( artist, "UTF-8")
                        +"&api_key=bcf006ec84c572c33264e09c026d3f11&format=json";
            //   Log.d(TAG, "u = "+ u);

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, u, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                              //  Log.d(TAG, "onResponse: "+response.toString());
                                try {
                                    JSONArray tracks = response.getJSONObject("toptracks").getJSONArray("track");
                                    for(int i=0;i<(tracks.length()>20?20:tracks.length());i++){
                                        TopSong song=new TopSong();

                                        if(tracks.getJSONObject(i).getString("mbid")!=null){
                                            song.setMbid(tracks.getJSONObject(i).getString("mbid"));

                                        }
                                        song.setTitle(tracks.getJSONObject(i).getString("name"));
                                        song.setUrl(tracks.getJSONObject(i).getString("url"));

                                        JSONArray images = tracks.getJSONObject(i).getJSONArray("image");
                                        for(int j=0;j<images.length();j++){
                                            JSONObject temp= images.getJSONObject(j);
                                            if(temp.getString("size").equals("large")){
                                                song.setCover(temp.getString("#text"));
                                            }
                                        }
                                        topSongs.add(song);
                                    }
                             //       Log.d(TAG, "ARRAYLIST"+topSongs.size());
                                    adapter.notifyDataSetChanged();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                            //        Log.d(TAG, "ERROR ARRAYLIST"+topSongs.size());
                                    adapter.notifyDataSetChanged();
                                }



                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                error.printStackTrace();
                            }
                        });

// Access the RequestQueue through your singleton class.
                MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);

            } catch (Exception e) {

            }

        }

    }


    public class SongHolder extends RecyclerView.ViewHolder{

        View v;
        TextView title;
        ImageView background;
        // ImageView artView;
        public SongHolder(View itemView) {
            super(itemView);
            v=itemView;
            background=(ImageView)itemView.findViewById(R.id.album_art);
            title=(TextView)itemView.findViewById(R.id.song_name);
        }
    }


    public class SongAdapter extends RecyclerView.Adapter<SongHolder>{


        @Override
        public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=inflater.inflate(R.layout.top_song_card,parent,false);

            return new SongHolder(v);
        }

        @Override
        public void onBindViewHolder(final SongHolder holder, final int position) {
            holder.title.setText(topSongs.get(position).getTitle());

            try {
                Picasso.with(getActivity().getApplicationContext()).load(topSongs.get(position).getCover()).fit().into(holder.background);
            }catch(Exception e){
            //    Log.d(TAG, "onBindViewHolder: "+e.toString());
                Picasso.with(getActivity().getApplicationContext()).load(R.drawable.photo).fit().into(holder.background);
            }


        }

        @Override
        public int getItemCount() {
            return topSongs.size();
        }
    }



}
