package com.ai.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DisplayAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int current = getIntent().getIntExtra("current", 1);
        switch (current) {
            case 1:
                setContentView(R.layout.one);
                break;
            case 2:
                setContentView(R.layout.two);
                break;
            default:
                setContentView(R.layout.three);
                break;
        }
    }
}
