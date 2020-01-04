package com.daohang.trainapp.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.daohang.trainapp.R;

public class ProjectItemView extends LinearLayout {
    TextView tvProjectName;

    public ProjectItemView(Context context) {
        super(context);
        initView(context);
    }

    public ProjectItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProjectItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ProjectItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.item_project,this,true);
        tvProjectName = findViewById(R.id.tvProjectName);
    }

    public void setName(String name){
        tvProjectName.setText(name);
    }

    public void setBackground(boolean selected){
        if (selected)
            tvProjectName.setBackgroundColor(getResources().getColor(R.color.button_blue));
        else
            tvProjectName.setBackgroundColor(getResources().getColor(R.color.bg_black));
    }
}
