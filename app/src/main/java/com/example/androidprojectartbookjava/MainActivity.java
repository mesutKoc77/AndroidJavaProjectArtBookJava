package com.example.androidprojectartbookjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.androidprojectartbookjava.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //SIMDI ISE LAYOUT LARDIMIZDAKI VIEW LERE ERISIM ZAMANI VE ONLARI BULMA ZAMAMNI. ESKIDEN FINDBY... METHODU ILE ERISIOYRUK AMA ARTIK VIEWBINDING ILE BUNLARA ERISIYORU.
    //BUNUN IICNDE GEREKLI ISLEMLERI YAPMAMIZ GEREKIYOR.
    //SIMDI BIR ACTIVITYMAINBINDING OLUSTURACAGIZ. BIR NESNE.

    private ActivityMainBinding binding;  //herbi xml layoutu icin ayri bir Binding sinifi otomatik olusturulur. Vunku build boilumune viewvbindige true demis ve yuklemistik.  Bu baglama sinifinin bir ornegini olusturmak icin de inflate methodunu
    //kullanacagiz.

    ArrayList<Art> artArrayList;
    ArtAdapter artAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main); artik view bindge gectigimiz icin  bu satiri sikiyoru ve kullanmiyoruz. onun yerine aasgikaki binding variableini atayacagiz.setContentView methodu, kullaniciya ne gosterilecegini belirtir.Yani ilgili Layoutu alir ve Activity deki gorunumu olarak ayarlar.
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        artArrayList=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter=new ArtAdapter(artArrayList);
        binding.recyclerView.setAdapter(artAdapter);
        //simdi ise ArtAdpater e, yeni veri geldiginde lutfen kendini gunceller misin, demmeiz gereiyor. asagoda getData() methodunda artAdapter.notifyDataSetChanged();
        //ile bunu yapmis oolduk.

        getData();


    }
    
    //simdi ise kullanicidan almis oldugumn verileri gorecegim.
    

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        try {
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null); //Arts isimli DataBase imizi actik.
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts", null); //simdi arts isimli tablodan tum bilgileri cektik ve onu bir Cursor nesnesine atadik.
            //cursor, satir satir hareket eder, ornegin birinci satira once gider, o satirdaki sutun degerlerini talep ederek hareket etmemizi saglar.
            //simdi id ve artname lerin sutun numarlarini veya indekslerini alacagim.
            //cursor ile ilgili daha ayrintili biligiyi diger dosyaya kayit ettim.
            int nameIx = cursor.getColumnIndex("artname");
            int idIx=cursor.getColumnIndex("id");
            while(cursor.moveToNext()){
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Art art=new Art(name,id); //once Art isimli bir class olusturudk ve ordan da birer nesene olusturuduk, olusturudugumuz her bir neseneyi ise bir Arrayliste attik.
                artArrayList.add(art);
                //simdi ise bunlari kullaniciya recylce view ile gosterme zamani. Bunun iicnde bir reyclervioew olusturmmaiz gerekixor.

            }
            artAdapter.notifyDataSetChanged();//yani yeni veriler geldigin de kendini gunceller
            cursor.close();


        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    
    

    //simdi OptionMenu olusturuldugnda ne olacagini yazacagiz.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //olusturdugumuz layout u yani art_menu layoutunu simidi ise koda baglayacagiz. Bu koda baglama durumlarinda "inflater" i kullaniriz.
        //bize asagida Inflate ile Hangi menuyu baglamak istedigimizi soracak,  Biz de ona diyecegiz ki art_menu isimli bir layout olusturmus idim onu baglamak istiyorum.
        /*
        Android'de "inflate etmek", XML'de tanımlanmış bir layout dosyasını hafızada bir View nesnesine dönüştürmek için kullanılır. Bu işlem, XML'de tanımlanmış statik layoutların programatik olarak kullanılabilir hale getirilmesini sağlar.
        Android İnflate Etme Senaryosu:
Adım 1: XML Layout Tanımlama

Öncelikle, bir XML dosyasında görsel bir arayüz (örneğin, bir buton ve bir metin alanı içeren bir layout) tanımlarsınız. Bu, res/layout klasörü altında my_layout.xml adında bir dosya olabilir.
Adım 2: Inflate İşlemi

Uygulamanızın çalışma zamanında, bu XML layout'un bir View nesnesine dönüştürülmesi gerekmektedir. Bu dönüşüm işlemine "inflate etmek" denir. LayoutInflater sınıfı, bu işlemi gerçekleştirmek için kullanılır.
Adım 3: Inflate Edilen View'ı Kullanma

Inflate edilen View nesnesi artık bir Activity, Fragment veya RecyclerView'ın ViewHolder'ı gibi yerlerde kullanılabilir hale gelmiştir.
Günlük Hayattan Örnek:
Bu işlemi anlamak için, bir şişme yatak örneğini düşünebiliriz:

XML Layout: Bu, katlanmış ve paketlenmiş haldeki şişme yatağınızdır. Şu anda kullanılamaz durumda, sadece bir potansiyel olarak var.

Inflate Etme: Şişme yatağı bir pompa ile şişirmek gibi düşünebilirsiniz. Pompa, yatağı kullanılabilir hale getirir.

Inflate Edilen View Kullanma: Şişirilmiş yatak artık uyumak, oturmak gibi amaçlar için kullanılabilir.

Bu süreçte, "inflate etmek" adımı, XML layout'un (katlanmış şişme yatak) gerçek bir View nesnesine (şişirilmiş yatak) dönüştürülmesidir. Bu, Android'de arayüzlerin programatik olarak yönetilmesini ve dinamik bir şekilde oluşturulmasını sağlar.
"XML layout'un bir View nesnesine dönüştürülmesi" ifadesi, Android'de bir layout'un XML dosyasından, kodunuzda çalıştırılabilir ve görsel olarak gösterilebilir bir arayüz bileşenine (View) çevrilmesini ifade eder. Bu işlem, biraz bir tasarımın gerçek bir ürüne dönüştürülmesi gibi düşünülebilir. İşte bu süreci daha somut bir şekilde açıklayan bir örnek:

Örnek Senaryo: Bir Restoran Menüsü
Bir restorandaki menüyü düşünün. Menüde çeşitli yemeklerin adları ve açıklamaları yazılıdır. Bu menü, bir XML layout dosyasına benzer; içeriği sabittir ve yemeklerin nasıl göründüğünü veya tadının nasıl olduğunu doğrudan deneyimleyemezsiniz. Sadece yemeklerin adlarını ve açıklamalarını görebilirsiniz.

Bir garson, bu menüden sipariş alır ve mutfaktaki aşçılara iletir. Mutfakta aşçılar, bu siparişi alıp gerçek yemekleri hazırlarlar. Bu süreç, XML layout'un "inflate edilmesine" benzer. Burada garson, LayoutInflater gibi çalışır ve XML'deki bilgileri (menüdeki sipariş) gerçek bir formda (hazırlanan yemek) sunar.

Android'de Inflate Etme Süreci
Bu restoran senaryosunu Android'e uyarladığımızda:

XML Layout (Menü): XML layout, uygulamanızın kullanıcı arayüzünün tasarımını içerir. Bu dosya statiktir ve sadece tasarım bilgilerini içerir, interaktif değildir.

Inflate İşlemi (Garson): LayoutInflater sınıfı, XML dosyasındaki bilgileri alıp, bu bilgilere dayanarak gerçek, interaktif bir View (Görsel Arayüz Bileşeni) oluşturur. Bu View, kullanıcıların etkileşime girebileceği, dokunabileceği ve görebileceği gerçek bir arayüz elemanıdır.

Kullanılabilir View (Hazırlanan Yemek): Inflate edilen View, uygulamanızın Activity veya Fragment'ında kullanıcıya gösterilen ve etkileşimde bulunabilecekleri gerçek bir arayüz bileşenidir.

Bu şekilde, XML layout dosyasındaki tasarımlar, kullanıcının etkileşime girebileceği, görebileceği ve uygulama içinde gerçek zamanlı olarak tepki verebileceği gerçek View nesnelerine dönüşür.

         */
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu); //artik bizim layout xml imiz, Activtiy imize baglanmis oldu.
        return super.onCreateOptionsMenu(menu);
    }


    //burada ise optionlardan bir tanesine tiklandiginda ne oalcagini seciyoruz.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //biz layout umuzda bir tane kullandik ama  birden fazla olabilirdi.
        //eger bizimkisi secilmis ise bu duurmda ne oalcak onu yazagiz. bizim menjuyu tiklandiginda Artactivity class ina yonlendirecegiz.
        if (item.getItemId()==R.id.add_art){
            Intent intent = new Intent(MainActivity.this,ArtActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        // else if (item.getItemId()==) .....    Eger birden fazla MEnu muz olsa idi her bir menu icin ayri ayri direktifler verecektik. Ama suan bizim bir adet Menu muz var. Dolayisila
        // else if' e ihtiyacimiz suan icin yok.
        return super.onOptionsItemSelected(item);
    }

    //Ozellikle SQL lite ile calisriken ve test yaparken veya bug larla ugrasiyrken uygulamayi emulatorden silip yeninden yuklemek dhaa iyidir,
    //ayrica cursor u kapatmayi unutma.
}