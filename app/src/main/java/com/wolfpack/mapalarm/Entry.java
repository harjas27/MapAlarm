package com.wolfpack.mapalarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Entry extends AppCompatActivity
{

    private EditText loc;
    private Button sub;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        loc=(EditText)findViewById(R.id.location);
        sub=(Button)findViewById(R.id.submit);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Entry.this,MainActivity.class);
                i.putExtra("LOCATION", loc.getText().toString());
                startActivity(i);
            }
        });
    }
}
