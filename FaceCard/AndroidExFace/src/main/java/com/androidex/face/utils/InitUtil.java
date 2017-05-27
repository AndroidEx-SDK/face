package com.androidex.face.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.androidex.face.PermissionsManager;
import com.synjones.idcard.IDCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by cts on 17/5/26.
 */

public class InitUtil {
    // 要校验的权限
    public static String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    /**
     * 初始化权限管理类
     */
    public static void initPermissionManager(final Activity context) {
        PermissionsManager mPermissionsManager = new PermissionsManager(context) {
            @Override
            public void authorized(int requestCode) {
                Toast.makeText(context, "权限通过！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("缺少相机权限！");
                builder.setPositiveButton("设置权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsManager.startAppSettings(context);
                    }
                });
                builder.create().show();
            }

            @Override
            public void ignore() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("Android 6.0 以下系统不做权限的动态检查\n如果运行异常\n请优先检查是否安装了 OpenCV Manager\n并且打开了 CAMERA 权限");
                builder.setPositiveButton("确认", null);
                builder.setNeutralButton("设置权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsManager.startAppSettings(context);
                    }
                });
                //builder.create().show();
            }
        };
        mPermissionsManager.checkPermissions(0, PERMISSIONS);// 检查权限
    }

    /**
     * 保存方法
     */
    public static void saveBitmap(String path, String picName, Bitmap bmp) {
        Log.e(TAG, "保存图片");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File f = new File(path, picName);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "====已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用JSON文件保存数组
     */
    public static void saveJsonStringArray(String[] data) {
        String str = getString2Txt();

        JSONObject allData = new JSONObject();//建立最外面的节点对象
        JSONArray sing = new JSONArray();//定义数组

        for (int x = 0; x < data.length; x++) {//将数组内容配置到相应的节点
            JSONObject temp = new JSONObject();//JSONObject包装数据,而JSONArray包含多个JSONObject
            try {

                temp.put("name" + x, data[x]); //JSONObject是按照key:value形式保存
                Log.e(TAG, "====data:  " + x + ":::" + data[x]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sing.put(temp);//保存多个JSONObject
        }
        try {

            allData.put("cardinfo", sing);//把JSONArray用最外面JSONObject包装起来

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {//SD卡不存在则不操作
            return;//返回到程序的被调用处
        }
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "urldata"
                + File.separator + "json.txt");//要输出的文件路径
        if (!file.getParentFile().exists()) {//文件不存在
            file.getParentFile().mkdirs();//创建文件夹
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            if (str!=null&&str!=""){

                out.append("["+str+","+allData.toString()+"]");//将数据变为字符串后保存
            }else {
                out.print(allData.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();//关闭输出
            }
        }
    }


    /**
     * [{"cardinfo":[{"name0":"李永平"},{"name1":"\/sdcard\/face\/1495891410072.png"},{"name2":"男"},{"name3":"汉"},{"name4":"19910210"},{"name5":"河南省济源市下冶镇三教村"},{"name6":"410881199102106519"},{"name7":"\/sdcard\/face\/1495891410127.png"}]},
     {"cardinfo":[{"name0":"李永平"},{"name1":"\/sdcard\/face\/1495891422651.png"},{"name2":"男"},{"name3":"汉"},{"name4":"19910210"},{"name5":"河南省济源市下冶镇三教村"},{"name6":"410881199102106519"},{"name7":"\/sdcard\/face\/1495891422739.png"}]}]

     * 解析JSON文件的简单数组
     */
    public static List<Map<String, String>> parseJson(String idnum) throws Exception {
        String data = getString2Txt();
        List<Map<String, String>> all = new ArrayList<Map<String, String>>();
        if (data != null) {
            JSONArray array = new JSONArray(data);//是数组
            for (int i = 0; i < array.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject temp = (JSONObject) array.get(i);
                JSONArray urldata = temp.getJSONArray("cardinfo");
                JSONObject jsonobj = urldata.getJSONObject(6);
                if (jsonobj.getString("name6").equals(idnum)){
                    jsonobj = urldata.getJSONObject(0);
                    map.put("name", jsonobj.getString("name0"));
                    jsonobj = urldata.getJSONObject(1);
                    map.put("photo", jsonobj.getString("name1"));
                    jsonobj = urldata.getJSONObject(2);
                    map.put("sex", jsonobj.getString("name2"));
                    jsonobj = urldata.getJSONObject(3);
                    map.put("nation", jsonobj.getString("name3"));
                    jsonobj = urldata.getJSONObject(4);
                    map.put("birthday", jsonobj.getString("name4"));
                    jsonobj = urldata.getJSONObject(5);
                    map.put("address", jsonobj.getString("name5"));
                    jsonobj = urldata.getJSONObject(6);
                    map.put("idnum", jsonobj.getString("name6"));
                    jsonobj = urldata.getJSONObject(7);
                    map.put("head", jsonobj.getString("name7"));
                    all.add(map);
                    return all;
                }
            }
        }
        return null;
    }

    /**

     * @return
     */
    public static String getString2Txt() {
        String str = "";
        try {
            File urlFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "urldata"
                    + File.separator + "json.txt");//Environment.getExternalStorageDirectory()+ File.separator +"json.txt"
            if (!urlFile.exists()) {
                urlFile.mkdir();
            }
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine;
                Log.e(TAG, "====str:  " + str);
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (str != null && str != "") {
                return str;
            }
        }
        Log.e(TAG, "===文件为空");
        return null;
    }

    public static JSONObject getJsonSimple(IDCard idCard, Bitmap bmp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", idCard.getName());
            jsonObject.put("photo", idCard.getPhoto());
            jsonObject.put("sex", idCard.getSex());
            jsonObject.put("nation", idCard.getNation());
            jsonObject.put("birthday", idCard.getBirthday());
            jsonObject.put("address", idCard.getAddress());
            jsonObject.put("idnum", idCard.getIDCardNo());
            jsonObject.put("head", bmp);

        } catch (JSONException e) {
            throw new RuntimeException("getJsonSimple JSONException:" + e);
        }
        Log.d(TAG, "getJsonSimple jsonObject:" + jsonObject.toString());
        return jsonObject;
    }
}
