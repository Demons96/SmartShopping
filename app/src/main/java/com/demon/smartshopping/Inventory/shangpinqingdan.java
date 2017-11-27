package com.demon.smartshopping.Inventory;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.MyClass.HttpUtils;
import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.R;


/**
 * Created by gyp19 on 17-4-27.
 * 商品清单，显示已购买的商品
 */

public class shangpinqingdan extends Fragment implements View.OnClickListener {

    private TextView textView;
    private TextView textView2;

    private DatabaseHelper dbHelper;

    private double allPrice = 0;    //订单总价格

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shangpinqingdan, container, false);

        textView = (TextView) view.findViewById(R.id.textView);     //显示清单信息
        textView2 = (TextView) view.findViewById(R.id.textView_2);  //显示总价

        view.findViewById(R.id.button1).setOnClickListener(this);   //结账

        dbHelper = new DatabaseHelper(getActivity(), "SmartDB.db", null, DatabaseHelper.VERSION);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("inventory", null, null, null, null, null, null);

        allPrice = 0;
        textView.setText("");   //添加前删除原有视图
        textView2.setText("总价：");

        //将商品的清单显示出来
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                textView.append("商品名称:" + name + "\n");
                textView.append("价格:" + price + "\n");
                textView.append("----------------------\n");
                try {
                    Double DPrice = Double.valueOf(price);
                    allPrice += DPrice;
                } catch (Exception e) {

                }
            } while (cursor.moveToNext());

            textView2.setText("总价：" + allPrice);

            cursor.close();
            db.close();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("请确认订单");
                dialog.setMessage("总价：" + allPrice);
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AddInventoryToClound();     //将商品订单上传至服务器

                        Toast.makeText(getActivity(), "结账成功", Toast.LENGTH_SHORT).show();

                        MyBluetoothIO.SendFinish();     //完成购物时向硬件发送开门信息

                        textView.setText("");   //清楚显示界面
                        textView2.setText("总价：");
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "已放弃购物", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
                break;
            default:
                break;
        }
    }

    //将清单信息上传到云
    private void AddInventoryToClound(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("inventory", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String phone = pref.getString("account", "");
                String code = cursor.getString(cursor.getColumnIndex("bar_code"));
                String url = "http://123.206.23.219/SmartShopping/API/AddToUserPhone.php?phone="
                        + phone + "&code=" + code;

                HttpUtils.getInstance().doGet(url, new HttpUtils.HttpResponseListernr() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

            } while (cursor.moveToNext());

            db.close();
        }
    }

}
