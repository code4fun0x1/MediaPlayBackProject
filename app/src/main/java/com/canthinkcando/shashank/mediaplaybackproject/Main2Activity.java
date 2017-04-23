package com.canthinkcando.shashank.mediaplaybackproject;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class Main2Activity extends AppCompatActivity {

    public static final String TAG="MAIN2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);
        String p[]={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
        Nammu.init(Main2Activity.this);
        Nammu.askForPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                startActivity(new Intent(Main2Activity.this,Main3Activity.class));
                finish();
            }

            @Override
            public void permissionRefused() {
                Log.d(TAG, "permissionRefused: PERMISSION REFUSED");
                finish();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
