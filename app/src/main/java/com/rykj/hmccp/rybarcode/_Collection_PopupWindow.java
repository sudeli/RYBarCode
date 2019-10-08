package com.rykj.hmccp.rybarcode;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 确认消息
 */
class ConfirmDialog extends AlertDialog {

    private Context context;
    private AlertDialog.Builder dialog;
    private OnConfirmClickListener listener;

    protected ConfirmDialog(@NonNull Context context, OnConfirmClickListener listener) {
        super(context);
        this.context = context;
        dialog = new AlertDialog.Builder(context);
        this.listener = listener;
    }

    public void show(String msg) {
        dialog.setTitle("确认信息");
        dialog.setIcon(R.mipmap.ic_launcher_round);
        dialog.setMessage(msg);
        dialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.OnAgreeClick(null);
                    dismiss();
                }
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AU.toast(context, "操作已取消！");
            }
        });
        dialog.show();
    }
}

/**
 * 简单文本编辑
 */
class SampleEditDialog extends AlertDialog {

    private AlertDialog.Builder dialog;
    private String dialogTitle;
    private EditText editView;

    protected SampleEditDialog(@NonNull Context context, String value, OnConfirmClickListener listener) {
        super(context);
        init(context, "确认信息", value, listener);
    }

    protected SampleEditDialog(@NonNull Context context, String title, String value, OnConfirmClickListener listener) {
        super(context);
        init(context, title, value, listener);
    }

    private void init(@NonNull final Context context, String title, String value, final OnConfirmClickListener listener) {
        dialog = new AlertDialog.Builder(context);
        dialogTitle = title;
        editView = new EditText(context);
        editView.setText(value);
        dialog.setView(editView);
        dialog.setTitle(dialogTitle);
        dialog.setIcon(R.mipmap.ic_launcher_round);
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("value", editView.getText().toString());
                    listener.OnAgreeClick(bundle);
                    dismiss();
                }
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.OnCancelClick();
                }
                AU.toast(context, "操作已取消！", Toast.LENGTH_SHORT);
            }
        });
    }

    public void show() {
        dialog.show();
    }
}