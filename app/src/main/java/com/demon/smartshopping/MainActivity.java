package com.demon.smartshopping;

import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.demon.smartshopping.MyClass.Commodity;
import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.MyClass.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.demon.smartshopping.Information.sousuoshangpin;
import com.demon.smartshopping.Inventory.shangpinqingdan;
import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.Navigation.daohangshangpin;
import com.demon.smartshopping.Promotion.ShowPromotion;
import com.demon.smartshopping.Test.ReturnCommodity;
import com.demon.smartshopping.Test.ShowAllCommodity;
import com.demon.smartshopping.Test.lianjielanya;

/**
 * 主活动，用来加载云端数据到本地数据库
 */
public class MainActivity extends AppCompatActivity {
    //全部商品信息的网址
    private static String URL_ALLCOMMODITY = "http://123.206.23.219/SmartShopping/API/ShowAllcommodity.php";
    private static final String TAG = "MainActivity";
    private DatabaseHelper dbHelper;    //数据库

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this, "SmartDB.db", null, DatabaseHelper.VERSION);

        //请求全部商品信息
        LoadCommodity(URL_ALLCOMMODITY);

        //加载fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //底部导航按钮
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 加载全部商品信息，得到返回的json数据
     * 调用函数解析json数据
     */
    private void LoadCommodity(final String url){
        HttpUtils.getInstance().doGet(url, new HttpUtils.HttpResponseListernr() {
            @Override
            public void onResponse(String result) {
                parseJSONWithJSONObject(result);
//                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, result);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * @param jsonData 服务器返回的json数据
     * 使用JSONObject解析json
     * 解析完之后的结果存入数据库
     */
    private void parseJSONWithJSONObject(String jsonData) {
        Commodity cd = new Commodity();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                cd.bar_code = jsonObject.getString("bar_code");
                cd.name = jsonObject.getString("name");
                cd.price = jsonObject.getString("price");
                cd.produced_date = jsonObject.getString("produced_date");
                cd.expiration_date = jsonObject.getString("expiration_date");
                cd.weight = jsonObject.getString("weight");

                //将其同步本地数据库
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put("bar_code", cd.bar_code);
                values.put("name", cd.name);
                values.put("price", cd.price);
                values.put("produced_date", cd.produced_date);
                values.put("expiration_date", cd.expiration_date);
                values.put("weight", cd.weight);

                db.insert("commodity", "null", values);
                values.clear();
                db.close();
            }
        } catch (Exception e) {
        }
    }

    //每次程序正常结束时删除本地数据库。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("commodity", null, null);
        db.delete("inventory", null, null);
        db.close();
    }

    //右上角小菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.lianjiely:    //链接蓝牙
                intent = new Intent (MainActivity.this, lianjielanya.class);
                startActivity(intent);
                break;
            case R.id.showallcommodity: //显示所有商品
                intent = new Intent (MainActivity.this, ShowAllCommodity.class);
                startActivity(intent);
                break;
            case R.id.return_commodity: //退还商品
                MyBluetoothIO.SendClean();  //清零硬件购物车的称
                intent = new Intent (MainActivity.this, ReturnCommodity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    //底部导航
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new sousuoshangpin();
                case 1:
                    return new daohangshangpin();
                case 2:
                    return new ShowPromotion();
                case 3:
                    return new shangpinqingdan();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "信息";
                case 1:
                    return "导航";
                case 2:
                    return "促销";
                case 3:
                    return "清单";
            }
            return null;
        }
    }
}
