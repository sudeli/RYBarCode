package com.rykj.hmccp.rybarcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BarCodeActivity extends AppCompatActivity {

    private Activity act;
    private Adapter_BarCodeList adapter;
    private ListView lvList;
    private TextView lblNoData, lblArea;
    private View footView;
    private boolean canShowAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.activity_barcode);
        AU.immersiveNotificationBar(act);
        Toolbar toolbar = findViewById(R.id.tbBCA_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.txt_title_capturelist);
        AU.checkPermission(act);
        if (AU.getScanAreaInfo().getList() == null || AU.getScanAreaInfo().getList().size() == 0) {
            canShowAdd = true;
        }
        footView = LayoutInflater.from(this).inflate(R.layout.listfooter_default, null);
        adapter = new Adapter_BarCodeList(act);
        adapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, Bundle bundle) {
                if (v.getId() == R.id.btnliBCL_Delete) {
                    doRefresh();
                } else {
                    AU.alert(act, String.valueOf(v.getId()));
                }
            }
        });
        lvList = act.findViewById(R.id.lvBCA_List);
        lblNoData = act.findViewById(R.id.lblBCA_NoData);
        lblArea = act.findViewById(R.id.lblBCA_Area);
        lblArea.setText(AU.getScanAreaInfo().getArea());
        lvList.setAdapter(adapter);

        lblArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldValue = lblArea.getText().toString();
                new SampleEditDialog(act, "采集区域", oldValue, new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        String value = bundle.getString("value");
                        lblArea.setText(value);
                        Model_AreaInfo addressInfo = AU.getScanAreaInfo();
                        addressInfo.setArea(value);
                        AU.setScanAreaInfo(addressInfo);
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                }).show();
            }
        });
        doRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_barcode, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == android.R.id.home) {
                doSave();
                finish();
            } else {
                switch (item.getItemId()) {
                    case R.id.mnuTBC_AddItem:
                        doAddItem();
                        break;
                    case R.id.mnuTBC_Export:
                        doExport();
                        break;
                    case R.id.mnuTBC_Save:
                        doSave();
                        break;
                    case R.id.mnuTBC_Delete:
                        new ConfirmDialog(act, new OnConfirmClickListener() {
                            @Override
                            public void OnAgreeClick(Bundle bundle) {
                                doDelete();
                            }

                            @Override
                            public void OnCancelClick() {

                            }
                        }).show("是否确认要删除采集区域[" + AU.getScanAreaInfo().getArea() + "]？");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doSave() {
        List<Model_AreaInfo> list = AU.getInfoList(act);
        Model_AreaInfo curInfo = AU.getScanAreaInfo();
        //region 将列表数据保存到区域信息
        List<Model_BarCodeInfo> barCodeInfos = adapter.getListData();
        curInfo.setList(barCodeInfos);
        curInfo.updateModifyTime();
        //endregion
        boolean hasInfo = false;
        for (int i = 0; i < list.size(); i++) {
            Model_AreaInfo areaInfo = list.get(i);
            if (areaInfo.getID().equals(curInfo.getID())) {
                list.set(i, curInfo);
                hasInfo = true;
                break;
            }
        }
        if (!hasInfo) {
            list.add(AU.getScanAreaInfo());
        }
        AU.saveInfoList(act, list);
        AU.toast(act, "采集数据已保存！");
    }

    private void doDelete() {
        List<Model_AreaInfo> list = AU.getInfoList(act);
        for (int i = 0; i < list.size(); i++) {
            Model_AreaInfo areaInfo = list.get(i);
            if (areaInfo.getID().equals(AU.getScanAreaInfo().getID())) {
                list.remove(i);
                break;
            }
        }
        AU.saveInfoList(act, list);
        AU.alert(act, "采集数据已删除！");
        finish();
    }

    private void doAddItem() {
        new SampleEditDialog(act, "编辑设备位置", "", new OnConfirmClickListener() {
            @Override
            public void OnAgreeClick(Bundle bundle) {
                canShowAdd = true;
                String roomName = bundle.getString("value");
                adapter.AddItem(roomName);
                doRefresh();
            }

            @Override
            public void OnCancelClick() {
                canShowAdd = false;
            }
        }).show();
    }

    private void doExport() {
        if (!AU.isGrantExtrnalRW(act)) {
            AU.toast(act, "没有存储权限！");
            return;
        }
        if (adapter.getCount() == 0) {
            AU.alert(act, "没有数据可以导出！");
            return;
        }
        String fileName = getFileName();
        String[] titles = new String[]{"设备位置", "表编号", "阀编号"};
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //boolean isWrite = file.canWrite();
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);
            //创建标题栏
            for (int i = 0; i < titles.length; i++) {
                sheet.addCell(new Label(i, 0, titles[i]));
            }
            //设置行高
            sheet.setRowView(0, 350);
            //填充数据
            for (int i = 0; i < adapter.getCount(); i++) {
                Model_BarCodeInfo item = adapter.getItem(i);
                sheet.addCell(new Label(0, i + 1, item.getRoom()));
                sheet.addCell(new Label(1, i + 1, item.getMeter()));
                sheet.addCell(new Label(2, i + 1, item.getValve()));
                sheet.setRowView(i + 1, 350);
            }
            workbook.write();
            AU.alert(act, "文件【" + fileName + "】已导出！");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
            lvList.setVisibility(View.GONE);
        }
        if (canShowAdd) {
            doAddItem();
        }
    }

    private String getFileName() {
        File file = new File(AU.getSDPath() + "/RongYao/BarCode");
        AU.makeDir(file);
        String fileName = file.toString() + "/" + AU.getScanAreaInfo().getArea() + "_" + AU.CAS() + ".xls";
        AU.log(fileName);
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                AU.toast(this, "已取消扫描");
            } else {
                Model_BarCodeInfo info = AU.getScanBarCodeInfo();
                if (AU.isScanIsMeter()) {
                    info.setMeter(result.getContents());
                } else {
                    info.setValve(result.getContents());
                }
                adapter.UpdateItem(info);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
