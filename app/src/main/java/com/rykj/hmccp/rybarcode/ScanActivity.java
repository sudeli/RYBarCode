package com.rykj.hmccp.rybarcode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScanActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;//取景框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        String area = "";
        String room = "";
        try {
            if (AU.getScanAreaInfo() == null) {
                area = "测试区域";
                room = "测试位置";
            } else {
                area = AU.getScanAreaInfo().getArea();
                String vo = AU.isScanIsMeter() ? "【表】" : "【阀】";
                room = AU.getScanBarCodeInfo().getRoom() + vo;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ((TextView) findViewById(R.id.lblScan_Address)).setText(area);
        ((TextView) findViewById(R.id.lblScan_RoomName)).setText(room);
        barcodeScannerView = findViewById(R.id.dbv_custom);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
