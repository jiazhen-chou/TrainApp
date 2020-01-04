package com.daohang.trainapp.components;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daohang.trainapp.R;

public class InputDialog extends Dialog implements View.OnClickListener {

    public static final String TAG = "InputDialog";
    private Context mContext;
    private String ssid = "";
    private EditText etPassword;
    private ImageView ivPwdVisible;
    private boolean pwdVisible = false;

    public InputDialog(@NonNull Context context, String ssid) {
        super(context);
        this.mContext = context;
        this.ssid = ssid;

        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_input);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnCancel = findViewById(R.id.btnBack);
        etPassword = findViewById(R.id.etPassword);
        ivPwdVisible = findViewById(R.id.ivPasswordVisible);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        ivPwdVisible.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (etPassword.getText() != null && !etPassword.getText().toString().isEmpty()) {
                    new Thread() {
                        @Override
                        public void run() {
                            connectWifi(ssid, etPassword.getText().toString());
                        }
                    }.start();
                    cancel();
                } else
                    Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnBack:
                cancel();
                break;
            case R.id.ivPasswordVisible:
                changeVisibility();
                break;
            default:
                break;
        }
    }

    // TODO: 2019-11-04 此处连接wifi会导致anr(已解决)
    // TODO: 2019-11-04 此处应考虑无需密码的情况，是直接连接，还是弹出确认框点击连接

    /**
     * 连接wifi（未考虑不需要输入密码的情况）
     *
     * @param ssid     wifi名称
     * @param password wifi密码
     */
    private void connectWifi(String ssid, String password) {
        try{
            WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wc.SSID = "\"" + ssid + "\"";
            wc.preSharedKey = "\"" + password + "\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            wifiManager.setWifiEnabled(true);
            int netId = wifiManager.addNetwork(wc);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示/隐藏密码
     */
    private void changeVisibility() {
        pwdVisible = !pwdVisible;
        if (pwdVisible) {
            ivPwdVisible.setImageResource(R.drawable.pwd_visible);
            etPassword.setTransformationMethod(null);
        } else {
            ivPwdVisible.setImageResource(R.drawable.pwd_invisible);
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
        }
    }
}
