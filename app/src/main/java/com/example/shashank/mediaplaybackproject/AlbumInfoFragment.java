package com.example.shashank.mediaplaybackproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumInfoFragment extends Fragment {


    public AlbumInfoFragment() {
        // Required empty public constructor
    }

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
        return inflater.inflate(R.layout.fragment_album_info, container, false);
    }

}
