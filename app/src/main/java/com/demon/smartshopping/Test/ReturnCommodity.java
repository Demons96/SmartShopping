package com.demon.smartshopping.Test;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.demon.smartshopping.MyClass.Commodity;
import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.MyClass.HttpUtils;
import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.R;

/**
 * 此乃商品退货功能，道理跟加货一个样。
 */
public class ReturnCommodity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;          //显示清单的数据的listView
    private CommodityAdapter adapter;   //上面数据的适配器
    private List<Commodity> commodityList = new ArrayList<>();  //传给适配器的list数据

    private DatabaseHelper dbHelper;    //数据库

    private Commodity commodity_click;  //= new Commodity() 所点击的商品类的对象，就是要退的那个

    private Double weightD;             //商品重量
    private int coumt = 0;              //蓝牙收发信息计数器
    private String number = "";         //拼接蓝牙传过来的商品重量信息
    private final int PANDUAN = 130;    //重量的误差值，需根据测试结果给予相应的调整

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_commodity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);

        //数据初始化
        dbHelper = new DatabaseHelper(ReturnCommodity.this, "SmartDB.db", null, DatabaseHelper.VERSION);

        //商品清单数据初始化
        initCommodity();
        adapter = new CommodityAdapter(ReturnCommodity.this, R.layout.commodity_item, commodityList);
        listView.setAdapter(adapter);

        //添加监听事件
        listView.setOnItemClickListener(this);
    }

    /**
     * 商品退货列表初始化
     * 读取商品清单的数据将其显示在列表上供用户选择
     */
    private void initCommodity() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("inventory", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndex("bar_code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                Commodity commodity = new Commodity(code, name, price);
                commodityList.add(commodity);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }

//        Commodity Apple = new Commodity("6920698424015", "测试商品1", "123");
//        commodityList.add(Apple);
//        Commodity Orange = new Commodity("6920698424015", "测试商品2", "123");
//        commodityList.add(Orange);
//        Commodity Watermelon = new Commodity("6920698424015", "测试商品3", "123");
//        commodityList.add(Watermelon);
//        Commodity Pear = new Commodity("6920698424015", "测试商品4", "123");
//        commodityList.add(Pear);
    }

    /**
     * 获取点击的商品
     * 将其二维码信息发给下一个界面，并希望他返回重量数据。
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        commodity_click = commodityList.get(position);
        String code = commodity_click.bar_code;

        Log.d("ReturnCommodity", "点击了" + commodity_click.name + " code=" + code);

        //向下一个活动发条码以显示所选中商品的信息
        Intent intent = new Intent(ReturnCommodity.this, ReturnCommodityTow.class);
        intent.putExtra("code", code);
        startActivityForResult(intent, 1);
//        Toast.makeText(ReturnCommodity.this, commodity.getName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 接收界面返回的退货商品重量信息
     * 开启线程接收硬件所称出来的重量信息
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "正在退货", Toast.LENGTH_SHORT).show();
                String weightS = data.getStringExtra("Weight");
                weightD = Double.valueOf(weightS);              //商品实际重量字符串数值化
                //开启监听线程
                new MyReturnIS().start();
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ++coumt;   
            number += String.valueOf(msg.obj);

            // Log.d("ReturnCommodity", "当前所接收的数据：" + number);
            
            if (coumt >= 4) {
                Double weight = Double.valueOf(number); //实际测量的重量
                
                Log.d("ReturnCommodity", "实际重量：" + weight.toString() + "  标准重量：" + weightD.toString());
                
                if ((weight > (weightD - PANDUAN)) && (weight < (weightD + PANDUAN))) {
                    //记录退货商品的价格
                    String Price = commodity_click.price;

                    //删除本地数据库上的信息
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String delete_code = commodity_click.bar_code;
                    db.delete("inventory", "bar_code=?", new String[]{delete_code});

                    //删除云上的数据库
                    //http://123.206.23.219/SmartShopping/API/DeleteUserCommodityCode.php?phone=15640999813&code=9787111478218
                    String delete_phone = HttpUtils.LOGIN_PHONE;    //删除当前账号（电话）中对应选中的商品
                    String url_delete_code = "http://123.206.23.219/SmartShopping/API/DeleteUserCommodityCode.php?phone=" + 
                                                delete_phone + "&code=" + delete_code;
                    HttpUtils.getInstance().doGet(url_delete_code, new HttpUtils.HttpResponseListernr() {
                        @Override
                        public void onResponse(String result) {
                            Toast.makeText(ReturnCommodity.this, result, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String msg) {
                            Toast.makeText(ReturnCommodity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // sendRequestWithOkHttp(url_delete_code);  //老方法v1.0

                    //删除列表上显示的数据
                    commodityList.remove(commodity_click);  //移除点击的商品
                    listView.setAdapter(adapter); //刷新

                    //提示框
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ReturnCommodity.this);
                    dialog.setTitle("退货信息");
                    dialog.setMessage("已退还" + Price + "元");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ReturnCommodity.this, "退货成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                    MyBluetoothIO.SendAddC();
                } else {
                    Toast.makeText(ReturnCommodity.this, "退货失败", Toast.LENGTH_SHORT).show();
                    MyBluetoothIO.SendAddD();
                }

                number = "";
                coumt = 0;
            }
        }
    };

    private class MyReturnIS extends Thread {
        InputStream is;

        MyReturnIS() {
            is = MyBluetoothIO.is_c;
        }

        @Override
        public void run() {
            super.run();
            try {
                for (int i = 0; i < 4; i++) {
                    byte[] buffer = new byte[128];
                    int count = is.read(buffer);    //如果蓝牙客户端没有发送数据则会被组赛
                    Message msg = new Message();
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    handler.sendMessage(msg);

                    // Log.d("ReturnCommodity", "退货商品线程跑的第" + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // private void sendRequestWithOkHttp(final String myurl) {
    //     new Thread(new Runnable() {
    //         @Override
    //         public void run() {
    //             try {
    //                 OkHttpClient client = new OkHttpClient();
    //                 Request request = new Request.Builder()
    //                         .url(myurl)
    //                         .build();
    //                 Response response = client.newCall(request).execute();
    //                 String responseData = response.body().string();
    //             } catch (Exception e) {

    //             }
    //         }
    //     }).start();
    // }

}
