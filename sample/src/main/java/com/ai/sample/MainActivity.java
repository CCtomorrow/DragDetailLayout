package com.ai.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button one = (Button) findViewById(R.id.one);
        Button two = (Button) findViewById(R.id.two);
        Button three = (Button) findViewById(R.id.three);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, DisplayAct.class);
        if (v.getId() == R.id.one) {
            intent.putExtra("current", 1);
        } else if (v.getId() == R.id.two) {
            intent.putExtra("current", 2);
        } else {
            intent.putExtra("current", 3);
        }
        startActivity(intent);
    }
}
