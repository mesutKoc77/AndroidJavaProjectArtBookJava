package com.example.androidprojectartbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.androidprojectartbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher; //bunu bir yere yani bir Intent e gittigimde ne olacagini anlatmak icin olusturdum.
    ActivityResultLauncher<String> permissionLauncher;     //bunu da bir, izin aldigimda bana String donecegini bildigim icin  bu duurumda ne olacagini anlatiyorum.

    Bitmap selectedImage; //donen data yerininn, image e donusturulmesi

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_art); view bindige gectigimiz icin artik isimiz kalmadi bununla.
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();
        database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null); //ARTS isimli bir genel bir database olusturduk. Asagida ise sheet lerini olusturacguz.


        Intent intent=getIntent();
        String info = intent.getStringExtra("info");
        if (info.matches("new")){
            //yani kullanici yeni bir art eklemek istiyor.
            binding.nameText.setText("");
            binding.artistText.setText("");
            binding.yearText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            Bitmap selectImage=BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.selectimage);
            binding.imageView.setImageBitmap(selectImage);

        }else{
            //degilse, demek ki bir id yolladi yani ilgili resime tikladi ve orda belki duzenleme veya gormek istiyor.
            int artId = intent.getIntExtra("artId", 1);
            binding.button.setVisibility(View.INVISIBLE);
            //database den cekerken bilgileri, try catch yapmak daha guvenli.
            try {
                Cursor cursor =database.rawQuery("SELECT * FROM arts WHERE id=?",new String[]{String.valueOf(artId)});
                int artNameIx=cursor.getColumnIndex("artname");
                int painterNameIx=cursor.getColumnIndex("paintername");
                int yearIx=cursor.getColumnIndex("year");
                int imageIx=cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(artNameIx));
                    binding.artistText.setText(cursor.getString(painterNameIx));
                    binding.yearText.setText(cursor.getString(yearIx));

                    byte [] bytes=cursor.getBlob(imageIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }
                cursor.close();

            } catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    public void save (View view) {
        String name=binding.nameText.getText().toString();
        String artistName=binding.artistText.getText().toString();
        String year=binding.yearText.getText().toString();
        //bitmap i alirken, kuculterek lammiz lazim yoksa coker. Bunu da bir method ile yapacagiz. Methodumuzun adi; makeSmallerImage
        //Uygulamada kayit edilen foto vb objeler, sifirilar ve birler olarak kyit edilirler, Dolyasiyla biz kullanicidan bunu aldigiimda sifir bir olarak aliyoruz ama onu tabi ki
        //tekrardan image e de cevirmemiz gerekecektir. Tabii ki bunu SQL lite a kayit etmek icin  bunlari alip birlere sifirlara cevirmemiz lazim cunku SQL ta direkt image i
        //kayit etmemis, birler sifirlar olarak kayit edecegiz.
        //bunun kodu da su sekilde

        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();// Bu nesne, daha sonra sıkıştırılmış görüntü verilerini saklamak için kullanılacak.
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);//ve burada sikistirilmis olan nesneyi, outputStream nesnesi icersisine sakladik.
        byte[] byteArray = outputStream.toByteArray();//sonra bu nesneyi aliriz ve byte dizisine ceviririz. ve istersen biz bunu sunucuya gondeririz veya dosya olarak bir yerlerde kayit
        //ederiz
        //simdo aldigimiz image i birlere ve sifirlara cevirmis olduk.

        try {
            //yukarida onCreate altinda zaten yazdigim icin veya olusutrudugumuz icin Database'i, buradan yeninden yazma ihtiaci hissetmedik
            database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null); //ARTS isimli bir genel bir database olusturduk. Asagida ise sheet lerini olusturacguz.

            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");//simdi arts isimli bir sheet oluruldu bu Arts isimli database icersiinde. //paintername, artistname  iicin kullanildik. artname=name

            //simdi kullanicidan bilgileri aliyorum.
            String sqlString="INSERT INTO arts (artname, paintername, year, image) VALUES(?,?,?,?)";
            //sqlStatemnet olusturacgiz ve bu; bizim bindiging islemlerimizi rahat yapabilmek icin olusturdugumuz bir obje.
            //simdi asagida sunu soyluyoruz. Yukaridaki sqlstring'i aliyorum ve database icerisiinde calistiracagim ben demek.

            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString); //ve calistirdim
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artistName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        } catch (Exception e){
            e.printStackTrace();
        }

        //artik kayit ettik ve sonrada artik mainActivity e donus yapmamiz gerekecek.
       // finish(); //bu da burayi kapatir ve main Activity e geri doner.
        Intent intent=new Intent(ArtActivity.this,MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //buraya gelmek icin mesela, mainActivity i acmis ama kapatmamis idim. //simdi ise acilmis olan butun aktiviteleri, ki iceirisnde bulundugum aktivite dahil, tum aktiviteleri kapat ve asagida verilen komutu uygula. Asagikdaki komut zaten MainAvtivity e gidiyorud.
        startActivity(intent);
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize){
        int width=image.getWidth();
        int height=image.getHeight();

        float bitmapRatio=(float) width/(float)height;
        if (bitmapRatio>1){
            //landscape image
            width=maximumSize;
            height=(int)(width/bitmapRatio);

        } else {
            //portrait image
            height=maximumSize;
            width=(int)(height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
        //burada image. deyince createScaledBitmap cikmiyor ama Bitmap.createSclaedBitmap deyince cikiyor. Dikkat.
    }

    public void selectImage (View view) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //yeni getirirlen duzenlemeler sonrasi bu kontrolu yapmak gerekiyor
            //yani bu Android 33 ve ustu ise
            //Bu durumda Read MEdia Iamges' i istmememiz gerekiyor
            //yani izin yoksa istiyoruz, yoksa galeriye gidiyoruz.
            //ama izin vermisse tabii ki direkt yine giemiyoruz, ne yapacagimizi soylemek icin de bir regosterLauncer methodu iceriisnde kendisine ne yapagimi belirtecegiz.
            //oncelikle asagidakini Manifest e ekledim
            // <uses-permission android:name="android.permission.READ_MEDIA_IMAGES"></uses-permission>
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != //burada dikkat et, manifets i secerken android den import et, java dan degil
                    PackageManager.PERMISSION_GRANTED){
                //eger kendisine zorunlu olarak bir aciklama gostermeli isek...(bu android'in inisiyatifinde olan bir sey)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Erisim icin izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("lutfen Izin Veriniz", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES); //burada dikkat et, manifets i secerken android den import et, java dan degil

                        }
                    }).show();

                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

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
                //ama oncelikle yukaridaki variable lare tabii ki kayit ettirmem gerekiyor bunun iicn de register Launcer (reggisterLauncer)gibi bir method yazacagim. Bu Launcer larin ne yapacagini hep asagidaki
                //method da tanimlayacagm. ve daha sonra da onlari onCreate altinda cagiracagim.Yani bunlarin ben ne yapacgini tanimlamis olacagim onCreate altinda.
                //yani bizim Launcer imiz varsa, bunun ne yapacagini Android e onCreate altinda soylememeiz gerekiyor.
                activityResultLauncher.launch(intentToGallery); //gallery e git
            }

        } else {
            //33 altinda ise O zaman Read External Storage istiyoruz.
            //yani asagikdaki kodu

            //yani izin yoksa istiyoruz, yoksa galeriye gidiyoruz.
            //ama izin vermisse tabii ki direkt yine giemiyoruz, ne yapacagimizi soylemek icin de bir regosterLauncer methodu iceriisnde kendisine ne yapagimi belirtecegiz.
            //oncelikle asagidakini Manifest e ekledim
            // <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != //burada dikkat et, manifets i secerken android den import et, java dan degil
                    PackageManager.PERMISSION_GRANTED){
                //eger kendisine zorunlu olarak bir aciklama gostermeli isek...(bu android'in inisiyatifinde olan bir sey)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Erisim icin izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("lutfen Izin Veriniz", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); //burada dikkat et, manifets i secerken android den import et, java dan degil

                        }
                    }).show();

                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

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
                //ama oncelikle yukaridaki variable lare tabii ki kayit ettirmem gerekiyor bunun iicn de register Launcer (reggisterLauncer)gibi bir method yazacagim. Bu Launcer larin ne yapacagini hep asagidaki
                //method da tanimlayacagm. ve daha sonra da onlari onCreate altinda cagiracagim.Yani bunlarin ben ne yapacgini tanimlamis olacagim onCreate altinda.
                //yani bizim Launcer imiz varsa, bunun ne yapacagini Android e onCreate altinda soylememeiz gerekiyor.
                activityResultLauncher.launch(intentToGallery); //gallery e git
            }
        }


    }
    //evet simdi ben iizin verilirse veya verilmezse ne yapacigmi belirtecgiom bir method yaziyorum.
    private void registerLauncher() {
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //eger kullanici galerisine gitti ve ordan bir foto secti ise. Result aslinda kullanici galerisine gitti ve ne oldu demekle ayni sey.
                if (result.getResultCode()==RESULT_OK) //dmeek ki kullanici bir image secti demektir bu.
                {
                    Intent intentFromResult=result.getData();
                    //eger intenFromResult bossa veya hicbir sey secilmemessse durumlari da olabilir. bundan dolayi bir parantez acilmalidir.
                    if (intentFromResult != null) //eger bos donmemisse yani icerisinde bir image ile donuyorsa.
                    {
                        Uri imageData = intentFromResult.getData(); //kullaniciinin bu datasi nerde ?
                       //binding.imageView.setImageURI(imageData); //bu sunu yapiyor. ImageView de gorunecek image in data yerini kaydetmis oluyoruz ama bize bu lazim degil, cunku ben
                        //kullanicinin bu image ini kayit etmek istiyorum
                        // o halde ben kullanicidan almis oldugum image'i (yani image in kayit edildigi yeri) bitmap e (yani bir image'e) cevirio artik kayit edecegim.

                        try {
                            assert imageData != null;
                            ImageDecoder.Source source = ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                            selectedImage = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);
                            //burada hoca analtirken Android Version lari farkli cihazlar icin hata veriyordu ve farkli kodlamalara gitti ama ben de hata vermedi ben direkt devam ettim.
                            //Video 117.Launcher

                        } catch (Exception e){
                            e.printStackTrace();

                        }

                    }

                }

            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){ //result==true
                    //permission granted
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //gallery e gittim ve ordan bir image alip getirevegim.
                    activityResultLauncher.launch(intentToGallery);



                } else {
                    //permission denied
                    Toast.makeText(ArtActivity.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }





    

}