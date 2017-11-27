package com.demon.smartshopping.Test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.R;

/**
 * 显示所有的商品
 */
public class ShowAllCommodity extends AppCompatActivity {
    private TextView textView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_commodity);

        textView = (TextView)findViewById(R.id.textView);

        dbHelper = new DatabaseHelper(this, "SmartDB.db", null, DatabaseHelper.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("commodity", null, null, null, null, null, null);

        if(cursor.moveToFirst()){
            do{
                String code = cursor.getString(cursor.getColumnIndex("bar_code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                String produced_data = cursor.getString(cursor.getColumnIndex("produced_date"));
                String expiration_data = cursor.getString(cursor.getColumnIndex("expiration_date"));
//                String weight = cursor.getString(cursor.getColumnIndex("weight"));

                textView.append("条形码信息:" + code+"\n");
                textView.append("商品名称:" + name+"\n");
                textView.append("价格:" + price+"\n");
                textView.append("生产日期:" + produced_data+"\n");
                textView.append("保质期:" + expiration_data+"\n");
//                textView.append("重量:" + weight+"\n");
                textView.append("------------------------------\n");
            }while(cursor.moveToNext());
            
            cursor.close();
            dbHelper.close();
        }
    }
}
