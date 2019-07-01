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




    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;

    }
    public void onItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView= itemView.findViewById(R.id.textView);


        public MyViewHolder(View v) {
            super(v);
            v.setTag(this);
            v.setOnClickListener(mOnItemClickListener);


        }





    }


    public void clear() {

        mDataset.clear();

        notifyDataSetChanged();

    }



// Add a list of items -- change to type used

    public void addAll(ArrayList<String> list) {

        mDataset.addAll(list);

        notifyDataSetChanged();

    }


    // Create new views (invoked by the layout manager)


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String texto= mDataset.get(position);
        holder.textView.setText(texto);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
        MyViewHolder vh = new MyViewHolder(v);


        return vh;
    }





}
