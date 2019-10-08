package com.rykj.hmccp.rybarcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

/**
 * 主活动列表
 */
class Adapter_MainList extends BaseAdapter {
    private Activity act;
    private List<Model_AreaInfo> listData;
    private OnListItemClickListener listener;

    public Adapter_MainList(Activity activity) {
        act = activity;
        listData = AU.getInfoList(act);
    }

    public void updateList() {
        listData = AU.getInfoList(act);
        notifyDataSetChanged();
        if (listener != null) {
            listener.onListItemClick(null, null);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Model_AreaInfo getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(act, R.layout.listitem_mainlist, null);
        }
        final Model_AreaInfo item = listData.get(position);
        AU.setText(view, R.id.lblliML_Area, item.getArea());
        AU.setText(view, R.id.lblliML_CreateTime, item.getCreateTime());
        AU.setText(view, R.id.lblliML_ModifyTime, item.getModifyTime());
        view.findViewById(R.id.btnliML_Detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.setScanAreaInfo(item);
                Intent intent = new Intent(act, BarCodeActivity.class);
                act.startActivity(intent);
            }
        });
        return view;
    }
}

/**
 * 条形码数据采集列表
 */
class Adapter_BarCodeList extends BaseAdapter {
    private Activity act;
    private List<Model_BarCodeInfo> listData;
    private OnListItemClickListener listener;

    public Adapter_BarCodeList(Activity activity) {
        act = activity;
        Model_AreaInfo areaInfo = AU.getScanAreaInfo();
        if (areaInfo.getList() == null) {
            listData = new ArrayList<>();
        } else {
            listData = areaInfo.getList();
        }
    }

    public void AddItem(String roomName) {
        listData.add(new Model_BarCodeInfo(roomName));
        notifyDataSetChanged();
    }

    public void UpdateItem(Model_BarCodeInfo item) {
        for (int i = 0; i < listData.size(); i++) {
            Model_BarCodeInfo info = listData.get(i);
            if (info.getID().equals(item.getID())) {
                info = item;
                break;
            }
        }
        notifyDataSetChanged();
    }

    public List<Model_BarCodeInfo> getListData() {
        return listData;
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Model_BarCodeInfo getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(act, R.layout.listitem_barcodelist, null);
        }
        final Model_BarCodeInfo item = listData.get(position);
        AU.setText(view, R.id.txtliBCL_RoomName, item.getRoom());
        AU.setText(view, R.id.txtliBCL_MeterCode, item.getMeter());
        AU.setText(view, R.id.txtliBCL_ValveCode, item.getValve());

        view.findViewById(R.id.txtliBCL_RoomName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SampleEditDialog(act, "编辑设备位置", item.getRoom(), new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        String value = bundle.getString("value");
                        item.setRoom(value);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                }).show();
            }
        });

        view.findViewById(R.id.txtliBCL_MeterCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SampleEditDialog(act, "编辑热表编号", item.getMeter(), new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        String value = bundle.getString("value");
                        item.setMeter(value);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void OnCancelClick() {
                    }
                }).show();
            }
        });
        view.findViewById(R.id.txtliBCL_ValveCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SampleEditDialog(act, "编辑阀门编号", item.getValve(), new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        String value = bundle.getString("value");
                        item.setValve(value);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void OnCancelClick() {
                    }
                }).show();
            }
        });
        view.findViewById(R.id.btnliBCL_Meter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.setScanBarCodeInfo(item);
                AU.setScanIsMeter(true);
                new IntentIntegrator(act)
                        .setCaptureActivity(ScanActivity.class)
                        .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                        .setPrompt("请对准条形码")
                        .setCameraId(0)
                        .setBeepEnabled(true)
                        .setBarcodeImageEnabled(false)
                        .initiateScan();

            }
        });
        view.findViewById(R.id.btnliBCL_Valve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.setScanBarCodeInfo(item);
                AU.setScanIsMeter(false);
                new IntentIntegrator(act)
                        .setCaptureActivity(ScanActivity.class)
                        .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                        .setPrompt("请对准条形码")
                        .setCameraId(0)
                        .setBeepEnabled(true)
                        .setBarcodeImageEnabled(false)
                        .initiateScan();

            }
        });
        view.findViewById(R.id.btnliBCL_Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ConfirmDialog(act, new OnConfirmClickListener() {
                    @Override
                    public void OnAgreeClick(Bundle bundle) {
                        listData.remove(item);
                        notifyDataSetChanged();
                        if (listener != null) {
                            listener.onListItemClick(v, bundle);
                        }
                    }

                    @Override
                    public void OnCancelClick() {

                    }
                }).show("是否删除[" + item.getRoom() + "][" + item.getMeter() + "][" + item.getValve() + "]");
            }
        });

        return view;
    }
}