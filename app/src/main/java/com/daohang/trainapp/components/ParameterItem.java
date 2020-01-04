package com.daohang.trainapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.daohang.trainapp.R;

public class ParameterItem extends LinearLayout {

    String title;
    String content;
    boolean locked;
    int titleRes;

    TextView tvTitle, tvContent;
    ImageView ivLock;

    OnClickListener mListener;

    public ParameterItem(Context context) {
        this(context, null);
    }

    public ParameterItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParameterItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ParameterItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ParameterItem);
        content = array.getString(R.styleable.ParameterItem_paramContent);
        locked = array.getBoolean(R.styleable.ParameterItem_paramLocked, true);

        TypedValue v = new TypedValue();
        array.getValue(R.styleable.ParameterItem_paramTitle, v);
        if (v.type == TypedValue.TYPE_REFERENCE) {
            titleRes = array.getInt(R.styleable.ParameterItem_paramTitle, -1);
        } else if (v.type == TypedValue.TYPE_STRING) {
            title = array.getString(R.styleable.ParameterItem_paramTitle);
        }

        array.recycle();

        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_parameter, this, true);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvContent = view.findViewById(R.id.tvContent);
        ivLock = view.findViewById(R.id.ivLock);

        if (title != null) {
            tvTitle.setText(title);
        } else if (titleRes != -1) {
            tvTitle.setText(titleRes);
        }

        setContent(content);

        setLocked(locked);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v);
            }
        });
    }

    public void setOnClick(OnClickListener listener){
        if (listener != null)
            mListener = listener;
    }

    public void setContent(String content) {
        if (content != null)
            tvContent.setText(content);
    }

    public void setLocked(boolean locked) {
        if (locked) {
            ivLock.setImageResource(R.mipmap.locked);
        } else {
            ivLock.setImageResource(R.mipmap.unlock);
        }
    }
}
