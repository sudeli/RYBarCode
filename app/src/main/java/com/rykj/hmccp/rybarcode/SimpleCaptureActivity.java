package com.rykj.hmccp.rybarcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SimpleCaptureActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_capture);
        AU.immersiveNotificationBar(this);
        Toolbar toolbar = findViewById(R.id.tbSCA_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("简单扫描测试");
        AU.checkPermission(this);
        AU.setScanAreaInfo(null);
        AU.setScanBarCodeInfo(null);
        findViewById(R.id.btnSCA_Scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(SimpleCaptureActivity.this)
                        .setCaptureActivity(ScanActivity.class)
                        .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                        .setPrompt("请对准条形码")
                        .setCameraId(0)
                        .setBeepEnabled(true)
                        .setBarcodeImageEnabled(false)
                        .initiateScan();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == android.R.id.home) {
                finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                setValue("已取消扫描");
                AU.toast(this, "Cancelled");
            } else {
                setValue("扫描结果：\n\n" + result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setValue(String value) {
        ((TextView) findViewById(R.id.lblSCA_ScannedValue)).setText(value);
    }
}
