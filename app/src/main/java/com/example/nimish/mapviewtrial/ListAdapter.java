package com.example.nimish.mapviewtrial;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by NIMISH on 25-07-2016.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<Cafe> data = Collections.emptyList();

    public ListAdapter(Context context, List<Cafe> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
        Log.d("Data",""+data);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_recycler_view,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Cafe current = data.get(position);
        holder.cafeName.setText(current.name);
        holder.cafeRating.setText(""+current.rating);

    }

    @Override
    public int getItemCount() {
        Log.d("Size",""+data.size());
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView cafeName,cafeRating;

        public MyViewHolder(View itemView) {
            super(itemView);
            cafeName = (TextView)itemView.findViewById(R.id.cafe_name);
            cafeRating = (TextView)itemView.findViewById(R.id.cafe_rating);
        }
    }
}
