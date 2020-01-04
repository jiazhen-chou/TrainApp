package com.daohang.trainapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.daohang.trainapp.R;
import com.daohang.trainapp.interfaces.OnMyClickListener;

public class LoginItem extends LinearLayout {

    final static int TYPE_STUDENT = 0;
    final static int TYPE_COACH = 1;

    int loginType;
    boolean loginStatus;
//    String loginName, loginId;
//    int btnTextRes, btnColor;

    private ImageView mIvLoginStatus;
    private TextView mTvLoginName, mTvLoginIdentification, mTvLoginNameTitle;
    private Button btnLogin;
    private OnMyClickListener mListener;

    public LoginItem(Context context) {
        this(context, null);
    }

    public LoginItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoginItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoginItem);

        loginType = array.getInt(R.styleable.LoginItem_loginType, 0);
        loginStatus = array.getBoolean(R.styleable.LoginItem_loginStatus, false);
//        loginName = array.getString(R.styleable.LoginItem_loginName);
//        loginId = array.getString(R.styleable.LoginItem_loginId);
//        btnTextRes = array.getInt(R.styleable.LoginItem_loginBtnText, -1);
//        btnColor = array.getInt(R.styleable.LoginItem_loginBtnColor, -1);


        array.recycle();

        initView();
    }

    private void initView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_login, this, true);
        mIvLoginStatus = view.findViewById(R.id.ivLoginStatus);
        mTvLoginName = view.findViewById(R.id.tvLoginName);
        mTvLoginNameTitle = view.findViewById(R.id.tvLoginNameTitle);
        mTvLoginIdentification = view.findViewById(R.id.tvLoginId);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onClick();
        });

        if (loginType == TYPE_STUDENT) {
            mTvLoginNameTitle.setText(R.string.text_stu_name);
        } else {
            mTvLoginNameTitle.setText(R.string.text_coach_name);
        }

        changeStatus();
    }

    public LoginItem setOnClick(OnMyClickListener listener) {
        this.mListener = listener;
        return this;
    }

    public LoginItem setLoginStatus(boolean status) {
        loginStatus = status;
        changeStatus();
        return this;
    }

    public LoginItem setName(String name) {
        if (name != null)
            mTvLoginName.setText(name);
        return this;
    }

    public LoginItem setId(String id){
        if (id != null){
            mTvLoginIdentification.setText(id);
        }
        return this;
    }

    public LoginItem setEnable(Boolean enable){
        btnLogin.setClickable(enable);
        return this;
    }

    public boolean getStatus(){
        return loginStatus;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return ;
//    }

    private void changeStatus() {
        //已登录
        if (loginStatus) {
            //学员
            if (loginType == TYPE_STUDENT) {
                mIvLoginStatus.setImageResource(R.drawable.stu_login);
                btnLogin.setBackground(getContext().getDrawable(R.drawable.btn_border_login_blue));
                btnLogin.setText(R.string.student_logout);
            }
            //教练
            else if (loginType == TYPE_COACH) {
                mIvLoginStatus.setImageResource(R.drawable.coach_login);
                btnLogin.setBackground(getContext().getDrawable(R.drawable.btn_border_login_yellow));
                btnLogin.setText(R.string.coach_logout);
            }
        }
        //未登录
        else {
            //学员
            if (loginType == TYPE_STUDENT) {
                mIvLoginStatus.setImageResource(R.drawable.stu_unlogin);
                btnLogin.setBackground(getContext().getDrawable(R.drawable.btn_gardient_blue));
                btnLogin.setText(R.string.student_login);
            }
            //教练
            else if (loginType == TYPE_COACH) {
                mIvLoginStatus.setImageResource(R.drawable.coach_unlogin);
                btnLogin.setBackground(getContext().getDrawable(R.drawable.btn_gardient_yellow));
                btnLogin.setText(R.string.coach_login);
            }
        }
    }
}
