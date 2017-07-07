package com.wolfpack.mapalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Place extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        String[] place ;
        place = getIntent().getStringArrayExtra("PLACES");
        Toast.makeText(this,"hjsfa",Toast.LENGTH_LONG).show();
        Log.e("error","place act started");
        if(place.length == 0)
        {
            Toast.makeText(getApplicationContext(), "No courses in your timetable", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String s="";
            ArrayList<String> placess=new ArrayList<String>(place.length);
            for(int i=0;i<place.length;i++)
            {
                placess.add(place[i]);
                s+=place[i];
            }
            Toast.makeText(this,s,Toast.LENGTH_LONG).show();
            Log.e("error3","list made");
            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.listview,place);
            ListView listView = (ListView) findViewById(R.id.places_list);
            listView.setAdapter(adapter);
        }
    }
}
