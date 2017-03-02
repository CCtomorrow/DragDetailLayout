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
        one.setOnClickListener(this);
        two.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, DisplayAct.class);
        if (v.getId() == R.id.one) {
            intent.putExtra("one", true);
        } else if (v.getId() == R.id.two) {
            intent.putExtra("one", false);
        }
        startActivity(intent);
    }
}
