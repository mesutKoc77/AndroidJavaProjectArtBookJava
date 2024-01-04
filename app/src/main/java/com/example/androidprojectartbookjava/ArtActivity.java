package com.example.androidprojectartbookjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.androidprojectartbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_art); view bindige gectigimiz icin artik isimiz kalmadi bununla.
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


    }

    public void save (View view) {


    }

    public void selectImage (View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Erisim icin izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("lutfen Izin Veriniz", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission

                    }
                }).show();

            } else {
                //request permission

            }

        }


        else { //o zaman izin verilmis.
            //gallery e git


        }


    }




}