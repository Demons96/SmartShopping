package com.demon.smartshopping.Information;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.R;

/**
 * 显示商品信息
 */
public class ShowCommodity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;  //显示数据
    private Intent intent;      //接收上一个活动数据
    private String result;      //存储数据
    private DatabaseHelper dbHelper;    //数据库

    private String code;
    private String name;
    private String price;
    private String produced_date;
    private String expiration_date;
    private String weight;
//    private String myurl = "http://123.206.23.219/SmartShopping/API/SelectCommodity.php?code=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commodity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);

        findViewById(R.id.button_1).setOnClickListener(this);   //加入购物车
        findViewById(R.id.button_2).setOnClickListener(this);   //返回

        //接收上一活动的数据
        intent = getIntent();
        result = intent.getStringExtra("result_data");

        //数据库
        dbHelper = new DatabaseHelper(this, "SmartDB.db", null, DatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = db.query("commodity", null, "bar_code=?", new String[]{result}, null, null, null);
            cursor.moveToFirst();
            code = cursor.getString(cursor.getColumnIndex("bar_code"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            price = cursor.getString(cursor.getColumnIndex("price"));
            produced_date = cursor.getString(cursor.getColumnIndex("produced_date"));
            expiration_date = cursor.getString(cursor.getColumnIndex("expiration_date"));
            weight = cursor.getString(cursor.getColumnIndex("weight"));
            cursor.close();

            textView.append("条形码信息:" + code + "\n");
            textView.append("商品名称:" + name + "\n");
            textView.append("价格:" + price + "\n");
            textView.append("生产日期:" + produced_date + "\n");
            textView.append("保质期:" + expiration_date + "\n");
//            textView.append("重量:" + weight + "\n");
        } catch (Exception e) {
            Toast.makeText(ShowCommodity.this, "没有在本地数据库找到商品。", Toast.LENGTH_SHORT).show();
//            myurl = myurl + result;
//            sendRequestWithOkHttp();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_1: //加入购物车
                //发送一至蓝牙
                MyBluetoothIO.SendAdd();

                //向上一活动返回数据让其开启线程监听
                Intent intent = new Intent();
                intent.putExtra("Weight", weight);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.button_2:
                finish();
                break;
            default:
                break;
        }
    }

    //OkHttp
//    private void sendRequestWithOkHttp() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url(myurl)
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String responseData = response.body().string();
//                    parseJSONWithJSONObject(responseData);
//                } catch (Exception e) {
////                    Toast.makeText(ShowCommodity.this, "没有在云数据库找到商品。", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).start();
//    }

    //使用JSONObject解析JSON
//    private void parseJSONWithJSONObject(String jsonData) {
//        try {
//            JSONArray jsonArray = new JSONArray("[" + jsonData + "]");
//            JSONObject jsonObject = jsonArray.getJSONObject(0);
//            showResponse(jsonObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //将数据显示在TextView上
//    private void showResponse(final JSONObject jO) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                textView.setText(response);
//                try {
//                    //从JSON中解析
//                    code = jO.getString("bar_code");
//                    name = jO.getString("name");
//                    price = jO.getString("price");
//                    produced_date = jO.getString("produced_date");
//                    expiration_date = jO.getString("expiration_date");
//                    weight = jO.getString("weight");
//
//                    //将其加入用户界面中
//                    textView.append("条形码信息:" + code + "\n");
//                    textView.append("商品名称:" + name + "\n");
//                    textView.append("价格:" + price + "\n");
//                    textView.append("生产日期:" + produced_date + "\n");
//                    textView.append("保质期:" + expiration_date + "\n");
//                    textView.append("重量:" + weight + "\n");
//
//                    //将其同步本地数据库
//                    SQLiteDatabase db = dbHelper.getWritableDatabase();
//                    ContentValues values = new ContentValues();
//                    values.put("bar_code", code);
//                    values.put("name", name);
//                    values.put("price", price);
//                    values.put("produced_date", produced_date);
//                    values.put("expiration_date", expiration_date);
//                    values.put("weight", weight);
//                    db.insert("commodity", "null", values);
//                    values.clear();
//                    db.close();
////                    Toast.makeText(ShowCommodity.this, "同步至本地数据库成功", Toast.LENGTH_SHORT).show();
//
//                } catch (Exception e) {
//                    Toast.makeText(ShowCommodity.this, "JSON数据解析失败。", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

}
