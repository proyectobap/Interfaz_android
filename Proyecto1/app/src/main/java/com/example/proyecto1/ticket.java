package com.example.proyecto1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ticket extends AppCompatActivity {
    TextView mTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        mTextview = (TextView)findViewById(R.id.ticket);

        mTextview.setText(getIntent().getStringExtra("texto"));
    }
}
