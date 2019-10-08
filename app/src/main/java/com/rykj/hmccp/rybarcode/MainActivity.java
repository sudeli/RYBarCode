package com.rykj.hmccp.rybarcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private Activity act;
    private Adapter_MainList adapter;
    private ListView lvList;
    private TextView lblNoData;
    private View footView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.activity_main);
        AU.immersiveNotificationBar(act);
        Toolbar toolbar = findViewById(R.id.tbMain_Toolbar);
        setSupportActionBar(toolbar);
        AU.checkPermission(act);

        footView = LayoutInflater.from(this).inflate(R.layout.listfooter_default, null);
        adapter = new Adapter_MainList(act);
        adapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, Bundle bundle) {
                doRefresh();
            }
        });
        lvList = act.findViewById(R.id.lvMain_List);
        lblNoData = act.findViewById(R.id.lblMain_NoData);
        lvList.setAdapter(adapter);

        findViewById(R.id.btnMain_New).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SampleEditDialog(act, "采集区域", "", new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        String value = bundle.getString("value");
                        AU.setScanAreaInfo(new Model_AreaInfo(value));
                        Intent intent = new Intent(act, BarCodeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                }).show();
            }
        });
        findViewById(R.id.btnMain_Refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.updateList();
            }
        });
        doRefresh();
    }

    @Override
    protected void onRestart() {
        adapter.updateList();
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == android.R.id.home) {
                finish();
            } else {
                switch (item.getItemId()) {
                    case R.id.mnuTM_SimpleCapture:
                        Intent intent = new Intent(act, SimpleCaptureActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }


    private void doRefresh() {
        lvList.removeFooterView(footView);
        int count = adapter.getCount();
        if (count > 0) {
            lblNoData.setVisibility(View.GONE);
            lvList.setVisibility(View.VISIBLE);
            if (count > 5) {
                lvList.addFooterView(footView);
            }
        } else {
            lblNoData.setVisibility(View.VISIBLE);
            lvList.setVisibility(View.INVISIBLE);
        }
    }
}
