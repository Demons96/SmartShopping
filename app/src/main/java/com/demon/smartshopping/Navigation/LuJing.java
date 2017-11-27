package com.demon.smartshopping.Navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.demon.smartshopping.R;

/**
 * 商品导航功能
 * 就是选择实现做好的路径图片
 */
public class LuJing extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lu_jing);

        imageView = (ImageView) findViewById(R.id.imageVew);

        //显示地图后的确定返回按钮
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String commodityName = intent.getStringExtra("commodity_name");

        switch (commodityName) {
            case "红烧牛肉面":
                imageView.setImageResource(R.drawable.mymap1);
                break;
            case "凉白开":
                imageView.setImageResource(R.drawable.mymap2);
                break;
            case "商品1":
                imageView.setImageResource(R.drawable.mymap3);
                break;
            case "商品2":
                imageView.setImageResource(R.drawable.mymap4);
                break;
            default:
                Toast.makeText(LuJing.this, "未存储该商品", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
