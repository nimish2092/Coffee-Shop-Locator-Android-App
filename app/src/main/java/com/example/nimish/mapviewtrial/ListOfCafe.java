package com.example.nimish.mapviewtrial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ListOfCafe extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter adapter;
    List<Cafe> ListOfCafe = new ArrayList<Cafe>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_cafe);
        ListOfCafe = getIntent().getParcelableArrayListExtra("mylist");
        Log.d("debug","List of cafe"+ListOfCafe);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        adapter = new ListAdapter(this,ListOfCafe);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
