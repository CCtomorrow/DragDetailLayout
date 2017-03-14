package com.ai.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ai.dragdetaillayout.FitAvoidView;

public class DisplayAct extends AppCompatActivity implements View.OnClickListener {

    private boolean change = false;
    private FitAvoidView mFitAvoidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int current = getIntent().getIntExtra(MainActivity.EXTRA, 1);
        switch (current) {
            case 1:
                setContentView(R.layout.one);
                break;
            case 2:
                setContentView(R.layout.two);
                break;
            case 3:
                setContentView(R.layout.three);
            case 4:
                setContentView(R.layout.yiji);
                break;
            default:
                setContentView(R.layout.one);
                break;
        }
        initYiji(current);
    }

    private void initYiji(int current) {
        if (current == 4) {
            mFitAvoidView = (FitAvoidView) findViewById(R.id.fitAvoidView);
            Button button = (Button) findViewById(R.id.change_text);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.change_text) {
            if (!change) {
                mFitAvoidView.setText("通过自定义属性可以达到自定义的控件也能像原生的控件一样实现可配置。但是在实际的项目开发中，像本文介绍的这种自定义控件使用频率并不是最高的，使用频率较高的是通过自定义一个组合控件的方式，来达到布局文件的复用，以减少项目维护成本以及开发成本，下篇文章将重点介绍如何自定义控件组合。");
                change = !change;
            } else {
                mFitAvoidView.setText("字少");
                change = !change;
            }
        }
    }

}
