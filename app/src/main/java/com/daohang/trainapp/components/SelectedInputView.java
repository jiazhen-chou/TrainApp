package com.daohang.trainapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.daohang.trainapp.R;

import wang.relish.widget.vehicleedittext.VehicleKeyboardHelper;

public class SelectedInputView extends LinearLayout {

    public static final int STYLE_INPUT = 0;
    public static final int STYLE_CHOOSE = 1;

    String title = "";
    int inputStyle;
    boolean hideStar;
    boolean bind;
    boolean etFocusable;
    View view;

    LinearLayout rootView;
    TextView tvTitle;
    EditText etInput;
    TextView tvStar;
    public SelectedInputView(Context context) {
        this(context, null);
    }

    public SelectedInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SelectedInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SelectedInputView);
        title = array.getString(R.styleable.SelectedInputView_title);
        inputStyle = array.getInt(R.styleable.SelectedInputView_inputStyle, STYLE_INPUT);
        hideStar = array.getBoolean(R.styleable.SelectedInputView_hideStar, false);
        bind = array.getBoolean(R.styleable.SelectedInputView_bind,false);
        etFocusable = array.getBoolean(R.styleable.SelectedInputView_etFocusable,true);

        initView(context);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.item_selected_input, this, true);
        rootView = view.findViewById(R.id.rootView);
        tvTitle = view.findViewById(R.id.tvTitle);
        etInput = view.findViewById(R.id.etInput);
        tvStar = view.findViewById(R.id.tvStar);

        tvTitle.setText(title);
        if (inputStyle == STYLE_INPUT) {
            etInput.setHintTextColor(getResources().getColor(R.color.edittext_hint));
            etInput.setEnabled(true);
        } else if (inputStyle == STYLE_CHOOSE) {
            etInput.setHintTextColor(getResources().getColor(R.color.selfcheck_blue));
            etInput.setEnabled(false);
        }
        if (hideStar)
            tvStar.setVisibility(INVISIBLE);

        if (bind)
            VehicleKeyboardHelper.bind(etInput);

        etInput.setFocusableInTouchMode(etFocusable);
        etInput.setFocusable(etFocusable);
    }

    /**
     * 设置点击事件
     *
     * @param listener
     */
    public void setClickListener(OnClickListener listener) {
        if (inputStyle == STYLE_CHOOSE && listener != null) {
            view.setOnClickListener(listener);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (inputStyle == STYLE_INPUT)
            return false;
        else
            return true;
    }

    /**
     * 获取标题
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取edittext内容
     * @return
     */
    public String getContent(){
        if (etInput.getText() != null && !etInput.getText().toString().isEmpty()){
            return etInput.getText().toString();
        }else {
            return null;
        }
    }

    public void setContent(String s){
        etInput.setText(s);
    }

    /**
     * 是否必填项
     *
     * @return
     */
    public boolean isNeedToFill() {
        return !hideStar;
    }

    public int getInputStyle() {
        return inputStyle;
    }
}
