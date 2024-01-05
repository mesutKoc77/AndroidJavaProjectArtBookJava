package com.example.androidprojectartbookjava;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.androidprojectartbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; //bunu bir yere yani bir Intent e gittigimde ne olacagini anlatmak icin olusturdum.
    ActivityResultLauncher<String> permissionLauncher;     //bunu da bir, izin aldigimda bana String donecegini bildigim icin  bu duurumda ne olacagini anlatiyorum.



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
        //yani izin yoksa istiyoruz, yoksa galeriye gidiyoruz.
        //oncelikle asagidakini Manifest e ekledim
        // <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED){
            //eger kendisine zorunlu olarak bir aciklama gostermeli isek...(bu android'in inisiyatifinde olan bir sey)
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
            //simdi ise biz baska bir yere gideegimizden oturu intenti kullancagiz. Ama bu sefer ne yapacginizi sececgiz cunku artik cihazin iceriisne gidiyoru ve cihazin iceriisnde ne yapacgimiz
            //Action class i ile soyleyecegiz.
            //ACTion= ne yapacagiz.
            //Uri ile de ayni url de oldug gibi nereye gidecegimizi belirtecegiz.
            //bu senaryo da pick yani biz gidip gallery den bir foto yu tutatcgiz.
            //uri ile de ben gallery e gidecegm ve ordan bir gorsek alacgim diyecegim.

            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //gallery e gittim ve ordan bir image alip getirevegim.
            //evet ben galeriye gittim simdi ne yapacagim? Bunun icin de REsultLAuncer gibi class lari kullanacagiz. Yani sekilde izin verildi mesela simdi ne yapacgim gibi durumlrda da ResultLAuncer
            //i kullanacgiz.
            //yukarida Global degisken olarak tanimlanan deguiskenlere bakabilirisn.
            //ama oncelikle yukaridaki variable lare tabii ki kayit ettirmem gerekiyor bunun iicn de register Launcer gibi bir method yazacagim. Bu Launcer larin ne yapacagini hep asagidaki
            //method da tanimlayacagm. ve daha sonra da onlari onCreate altinda cagiracagim.Yani bunlarin ben ne yapacgini tanimlamis olacagim onCreate altinda.
            //yani bizim Launcer imiz varsa, bunun ne yapacagini Android e onCreate altinda soylememeiz gerekiyor.
        }

    }





    

}