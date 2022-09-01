package com.huozige.lab.config;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;

import java.util.List;

public class StartActivity extends AppCompatActivity {

    static final String LOG_TAG="StartActivity";
    static final String CONFIG_BROADCAST_EXTRA_ENTRY = "entry";

    ActivityResultLauncher<Intent> _arcZxingLite; // 用来弹出ZXingLite扫码页面的调用器，用来代替旧版本的startActivityForResult方法。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button cmdSave =  findViewById(R.id.cmdSave);
        cmdSave.setOnClickListener(Save);

        Button cmdReset = findViewById(R.id.cmdReset);
        cmdReset.setOnClickListener(Reset);

        Button cmdScan = findViewById(R.id.cmdUrlQrCode);
        cmdScan.setOnClickListener(ScanForUrl);

        // 创建到ZXingLite的调用器
        _arcZxingLite= this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            // 按照ZXingLite文档获取和解析扫码结果数据，如果出错或者取消，默认为空字符串，同官方APP
            Intent data = result.getData();

            if( null != data ){
                String resultS= CameraScan.parseScanResult(data);
                EditText urlE =  StartActivity.this.findViewById(R.id.editUrl);
                urlE.getText().append(resultS);
            }
        });
    }

    View.OnClickListener ScanForUrl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            XXPermissions.with(StartActivity.this)
                    .permission(Permission.CAMERA)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                Toast.makeText(StartActivity.this,"请允许应用利用摄像头扫描二维码",Toast.LENGTH_LONG).show();
                                } else {
                                Toast.makeText(StartActivity.this,"请允许应用利用摄像头扫描二维码",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            // 调用ZXingLite的扫码页面
            _arcZxingLite.launch(new Intent(StartActivity.this, CaptureActivity.class));

        }
    };

    View.OnClickListener Reset = view -> {
        EditText urlE =  StartActivity.this.findViewById(R.id.editUrl);
        urlE.getText().clear();
    };

    View.OnClickListener Save = view -> {

        EditText urlE =  StartActivity.this.findViewById(R.id.editUrl);
        String url = urlE.getText().toString();

        Boolean isValidated = true;

        // 校验
        if(!url.toLowerCase().startsWith("http")){
            isValidated = false;
            Toast.makeText(StartActivity.this,"请设置有效的Web应用地址", Toast.LENGTH_LONG).show();
        }

        // 校验通过
        if(isValidated){

            Log.v(LOG_TAG,"校验通过，即将发送配置更新广播，新的URL： "+url);

            // 通过发送广播，配置HAC
            Intent intent = new Intent();

            intent.setAction(getString(R.string.app_config_broadcast_filter));

            // 接入点
            intent.putExtra(CONFIG_BROADCAST_EXTRA_ENTRY, url);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

            // 发送给HAC应用
            sendBroadcast(intent);

            Toast.makeText(StartActivity.this,"配置更新的广播已发送，点击HAC的【首页】菜单即可生效。", Toast.LENGTH_LONG).show();

            Log.v(LOG_TAG,"配置更新的广播已发送，Action： "+getString(R.string.app_config_broadcast_filter));
        }

    };
}