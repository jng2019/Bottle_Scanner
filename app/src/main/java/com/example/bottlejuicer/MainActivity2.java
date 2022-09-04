package com.example.bottlejuicer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView textView = findViewById(R.id.textView);
        EditText editText = findViewById(R.id.editTextTextPersonName);
        TextView textView2 = findViewById(R.id.textView2);

        textView2.setText("Please enter the bottle's material type:");
        TextView textView3 = findViewById(R.id.textView3);

        EditText editText1 = findViewById(R.id.editTextNumber);
        editText1.setHint("i.e. plastic");


    }
}