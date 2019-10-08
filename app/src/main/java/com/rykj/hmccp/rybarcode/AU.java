package com.rykj.hmccp.rybarcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AU {

    //region 设置沉浸式通知栏
    public static void immersiveNotificationBar(Activity activity) {
        try {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            ViewGroup group = window.findViewById(Window.ID_ANDROID_CONTENT);
            View view = group.getChildAt(0);
            if (view != null) {
                view.setFitsSystemWindows(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //endregion

    //region 检查权限
    public static boolean isGrantExtrnalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    public static void checkPermission(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            List<String> permissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permissions[i]);
                }
            }
            if (!permissionList.isEmpty()) {
                String[] pArray = permissionList.toArray(new String[permissionList.size()]);
                activity.requestPermissions(pArray, 1);
            }
        }
    }
    //endregion

    //region 路径相关
    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public static String getSDPath() {
        File sdDir = null;
        sdDir = Environment.getExternalStorageDirectory();
        boolean sdCardExist = Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }
    //endregion

    //region 设置View文本
    public static String getText(View v, int resID) {
        View xView = v.findViewById(resID);
        String result;
        if (xView instanceof TextView) {
            result = ((TextView) xView).getText().toString();
        } else if (xView instanceof EditText) {
            result = ((EditText) xView).getText().toString();
        } else {
            result = "";
        }
        return result;
    }

    public static void setText(View v, int idRes, String value) {
        View xView = v.findViewById(idRes);
        if (xView instanceof TextView) {
            ((TextView) xView).setText(value);
        } else if (xView instanceof EditText) {
            ((EditText) xView).setText(value);
        }
    }

    public static void setTextColor(View v, int idRes, int colorRes) {
        View xView = v.findViewById(idRes);
        if (xView instanceof TextView) {
            ((TextView) xView).setTextColor(colorRes);
        }
    }
    //endregion

    //region 输出
    public static void log(String msg) {
        Log.d("ndrea", msg);
    }

    public static void toast(Context c, int idRes) {
        toast(c, getResourceString(c, idRes));
    }

    public static void toast(Context c, String msg) {
        toast(c, msg, Toast.LENGTH_SHORT);
    }

    public static void toast(Context c, String msg, int toastTime) {
        log("AU = = = Toast ：【" + msg + "】");
        Toast t = Toast.makeText(c, msg, toastTime);//.show();
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

    public static void alert(Context c, int resID) {
        alert(c, getResourceString(c, resID));
    }

    public static void alert(Context c, String msg) {
        log("AU = = = Alert :【" + msg + "】");
        AlertDialog.Builder dialog = new AlertDialog.Builder(c);
        //dialog.setTitle("消息");
        dialog.setMessage(msg);
        dialog.setPositiveButton("确定", null);
        dialog.show();
    }

    public static String getResourceString(Context c, int idRes) {
        return c.getResources().getString(idRes);
    }

    public static String getCurTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    //endregion

    public static String CAS() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    //region 扫码传参
    private static Model_AreaInfo ScanAreaInfo;

    public static Model_AreaInfo getScanAreaInfo() {
        return ScanAreaInfo;
    }
    private static boolean ScanIsMeter;

    public static void setScanAreaInfo(Model_AreaInfo scanAreaInfo) {
        ScanAreaInfo = scanAreaInfo;
    }

    private static Model_BarCodeInfo ScanBarCodeInfo;

    public static Model_BarCodeInfo getScanBarCodeInfo() {
        return ScanBarCodeInfo;
    }

    public static void setScanBarCodeInfo(Model_BarCodeInfo scanBarCodeInfo) {
        ScanBarCodeInfo = scanBarCodeInfo;
    }

    public static boolean isScanIsMeter() {
        return ScanIsMeter;
    }

    public static void setScanIsMeter(boolean scanIsMeter) {
        ScanIsMeter = scanIsMeter;
    }
    //endregion

    //region 数据持久化

    /**
     * 获取持久化对象
     */
    private static SharedPreferences getPreferences_Settings(Context c) {
        return c.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * 保存数据采集列表
     */
    public static void saveInfoList(Context c, List<Model_AreaInfo> list) {
        String key = "list";
        try {
            SharedPreferences.Editor editor = getPreferences_Settings(c).edit();
            String json = getJsonList(list);
            editor.putString(key, json);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取数据采集列表
     */
    public static List<Model_AreaInfo> getInfoList(Context c) {
        String key = "list";
        String value = getPreferences_Settings(c).getString(key, "");
        List<Model_AreaInfo> list = new ArrayList<>();
        if (value.equals("")) {
            return list;
        }
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String area = obj.getString("area");
                String cTime = obj.getString("createTime");
                String mTime = obj.getString("modifyTime");
                JSONArray objList = obj.getJSONArray("list");
                Model_AreaInfo addressInfo = new Model_AreaInfo(id, area, cTime, mTime);
                List<Model_BarCodeInfo> barCodeInfos = new ArrayList<>();
                for (int j = 0; j < objList.length(); j++) {
                    JSONObject listItem = objList.getJSONObject(j);
                    String room = listItem.getString("room");
                    String meter = listItem.getString("meter");
                    String valve = listItem.getString("valve");
                    barCodeInfos.add(new Model_BarCodeInfo(room, meter,valve));
                }
                addressInfo.setList(barCodeInfos);
                list.add(addressInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 获取JSON数据
     */
    private static String getJsonList(List<Model_AreaInfo> list) {
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject obj = new JSONObject();
                Model_AreaInfo addressInfo = list.get(i);
                obj.put("id", addressInfo.getID());
                obj.put("area", addressInfo.getArea());
                obj.put("createTime", addressInfo.getCreateTime());
                obj.put("modifyTime", addressInfo.getModifyTime());
                JSONArray objList = new JSONArray();
                List<Model_BarCodeInfo> barCodeInfos = addressInfo.getList();
                if (barCodeInfos == null) {
                    barCodeInfos = new ArrayList<>();
                }
                for (int j = 0; j < barCodeInfos.size(); j++) {
                    Model_BarCodeInfo barCodeInfo = barCodeInfos.get(j);
                    JSONObject listItem = new JSONObject();
                    listItem.put("room", barCodeInfo.getRoom());
                    listItem.put("meter", barCodeInfo.getMeter());
                    listItem.put("valve", barCodeInfo.getValve());
                    objList.put(j, listItem);
                }
                obj.put("list", objList);
                jsonArray.put(i, obj);
            }
            jsonObject.put("list", jsonArray);
            json = jsonObject.toString();
            AU.log(json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
    //endregion
}
