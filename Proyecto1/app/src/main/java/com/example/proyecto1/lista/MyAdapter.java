package com.example.proyecto1.lista;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.proyecto1.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    private View.OnClickListener mOnItemClickListener;




    //Este constructor nos da un set de datos (depende del tipo de datos que metamos, en este caso un array list
    public MyAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;

    }
    public void onItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    //Este método proporciona una referencia para las views para cada tipo de item.

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;


        public MyViewHolder(View v) {
            super(v);
            this.textView = itemView.findViewById(R.id.textView);
            v.setTag(this);
            v.setOnClickListener(mOnItemClickListener);


        }





    }

//Limpia los datos del set de datos
    public void clear() {

        mDataset.clear();

        notifyDataSetChanged();

    }








    // Este método remplaza el contenido de la view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String texto= mDataset.get(position);
        holder.textView.setText(texto);

    }

    //Este método devuelve el tamaño de los datos
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item, parent, false);

        return new MyViewHolder(v);
    }





}
