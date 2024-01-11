package com.example.androidprojectartbookjava;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidprojectartbookjava.databinding.RecyclerRowwBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter <ArtAdapter.ArtHolder> {


    ArrayList<Art> artArrayList;

    public ArtAdapter(ArrayList<Art> artArrayList) {
        this.artArrayList = artArrayList;
    }



    //2. yapilacak !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowwBinding recyclerRowwBinding=RecyclerRowwBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new ArtHolder(recyclerRowwBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(artArrayList.get(position).name);

    }

    @Override
    public int getItemCount() //kac adet recyckerview yani satir olusturualacgini burda soyley<ecgz.artArraylist icinde ne kadar varsa olustuturlacak.
    {
        return artArrayList.size();
    }


    //1. yapilacak !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    //ArtADapter icin bu class i olusturuduk.

    //ve ArtHolder iisimli baska bir class i asagoida olusttruduk.
    public static class ArtHolder extends RecyclerView.ViewHolder //gorunum tutucugu olusturuduk.
    {
        RecyclerRowwBinding binding;
        //onun da constructor i asagida.
        public ArtHolder(RecyclerRowwBinding binding )    //simdi recylcer View Xml imiz ne ise onu baglayacagiz. Ve simdi recylcer view xml imiz olustutracguz. layout a gittik ve olusutrudk.
                //ve onu buraya cektik.
        {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
