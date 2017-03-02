package com.ai.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DisplayAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("one", true)) {
            setContentView(R.layout.one);
        } else {
            setContentView(R.layout.two);
        }
    }
}
