package com.demon.smartshopping.Test;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.R;

/**
 * 显示所选退货商品的信息
 */
public class ReturnCommodityTow extends AppCompatActivity implements View.OnClickListener{
    private DatabaseHelper dbHelper;
    private String weight;  //所选择商品的重量信息，传给上一活动判断

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_commodity_tow);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.button_1).setOnClickListener(this);   //确认退货
        findViewById(R.id.button_2).setOnClickListener(this);   //返回

        TextView textView = (TextView)findViewById(R.id.textView);

        dbHelper = new DatabaseHelper(this, "SmartDB.db", null, DatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();    //接收选中退货商品的条形码
        String code = intent.getStringExtra("code");

        //将所点击的商品的信息显示在界面上
        Cursor cursor = db.query("commodity", null, "bar_code=?", new String[]{code}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                String produced_date = cursor.getString(cursor.getColumnIndex("produced_date"));
                String expiration_date = cursor.getString(cursor.getColumnIndex("expiration_date"));
                weight = cursor.getString(cursor.getColumnIndex("weight"));
                //添加数据到界面
                textView.append("商品名称:" + name+"\n");
                textView.append("价格:" + price+"\n");
                textView.append("生产日期:" + produced_date+"\n");
                textView.append("保质期:" + expiration_date+"\n");
            }while(cursor.moveToNext());
            cursor.close();
            dbHelper.close();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_1:           //确认退货
                Intent intent = new Intent();
                intent.putExtra("Weight", weight);
                setResult(RESULT_OK, intent);

                MyBluetoothIO.SendAdd();  //发送让硬件快快开门
                finish();
                break;
            case R.id.button_2:           //返回
                finish();
                break;
        }
    }
}
