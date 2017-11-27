package com.demon.smartshopping.Promotion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demon.smartshopping.MyClass.DatabaseHelper;
import com.demon.smartshopping.R;
import com.demon.smartshopping.MyClass.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by gyp19 on 17-4-27.
 * 显示所有的促销商品
 */

public class ShowPromotion extends Fragment {
    private TextView textView;
    private DatabaseHelper dbHelper;    //数据库

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_promotion, container, false);

        textView = (TextView) view.findViewById(R.id.textView);
        dbHelper = new DatabaseHelper(getActivity(), "SmartDB.db", null, DatabaseHelper.VERSION);

        String thisurl = "http://123.206.23.219/SmartShopping/API/ShowAllPromotion.php";
        LoadPromotion(thisurl);

        return view;
    }

    /**
     * 加载所有促销商品，得到json数据并解析
     * 此json数据是促销商品的条形码集合
     */
    private void LoadPromotion(final String url){
        HttpUtils.getInstance().doGet(url, new HttpUtils.HttpResponseListernr() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Log.d("ShowPromotion", "2第" + (i + 1) + "遍解析Json" + jsonObject.toString());
                        
                        //促销商品的条形码
                        String pCode = jsonObject.getString("bar_code");
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        try {
                            Cursor cursor = db.query("commodity", null, "bar_code=?", new String[]{pCode}, null, null, null);
                            Log.d("ShowPromotion", "3根据解析出来的商品号" + pCode + "到本地数据库查找商品");
                            cursor.moveToFirst();
                            String code = cursor.getString(cursor.getColumnIndex("bar_code"));
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            String price = cursor.getString(cursor.getColumnIndex("price"));
                            String produced_date = cursor.getString(cursor.getColumnIndex("produced_date"));
                            String expiration_date = cursor.getString(cursor.getColumnIndex("expiration_date"));
            //                String weight = cursor.getString(cursor.getColumnIndex("weight"));
                            cursor.close();
                            Log.d("ShowPromotion", "4本地数据库返回结果->视图" + code + name);

                            textView.append("商品名称:" + name + "\n");
                            textView.append("价格:" + price + "\n");
                            textView.append("生产日期:" + produced_date + "\n");
                            textView.append("保质期:" + expiration_date + "\n");
                            textView.append("-------------------------------\n");
                            Log.d("ShowPromotion", "5添加数据至视图完成");
                        } catch (Exception e) {
            //                Toast.makeText(getActivity(), "没有在本地数据库找到商品。", Toast.LENGTH_SHORT).show();
                        } finally {
                            db.close();
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(String msg) {
            }
        });
    }

    //OkHttp
//     private void sendRequestWithOkHttp(final String myurl) {
//         new Thread(new Runnable() {
//             @Override
//             public void run() {
//                 try {
//                     OkHttpClient client = new OkHttpClient();
//                     Request request = new Request.Builder()
//                             .url(myurl)
//                             .build();
//                     Response response = client.newCall(request).execute();
//                     String responseData = response.body().string();

//                     Log.d("ShowPromotion", "1请求的促销商品数据" + responseData);
//                     //解析Json格式数据
//                     try {
//                         JSONArray jsonArray = new JSONArray(responseData);
//                         for (int i = 0; i < jsonArray.length(); i++) {
//                             JSONObject jsonObject = jsonArray.getJSONObject(i);
//                             Log.d("ShowPromotion", "2第" + (i + 1) + "遍解析Json" + jsonObject.toString());
//                             //促销商品的条形码
//                             String pCode = jsonObject.getString("bar_code");
//                             Message msg = new Message();
//                             msg.obj = pCode;
//                             handler.sendMessage(msg);
//                         }

//                     } catch (Exception e) {
//                     }
//                 } catch (Exception e) {
//                 }
//             }
//         }).start();
//     }

//     private Handler handler = new Handler() {
//         public void handleMessage(Message msg) {
//             super.handleMessage(msg);
//             String pCode = msg.obj.toString();
//             SQLiteDatabase db = dbHelper.getWritableDatabase();
//             try {
//                 Cursor cursor = db.query("commodity", null, "bar_code=?", new String[]{pCode}, null, null, null);
//                 Log.d("ShowPromotion", "3根据解析出来的商品号" + pCode + "到本地数据库查找商品");
//                 cursor.moveToFirst();
//                 String code = cursor.getString(cursor.getColumnIndex("bar_code"));
//                 String name = cursor.getString(cursor.getColumnIndex("name"));
//                 String price = cursor.getString(cursor.getColumnIndex("price"));
//                 String produced_date = cursor.getString(cursor.getColumnIndex("produced_date"));
//                 String expiration_date = cursor.getString(cursor.getColumnIndex("expiration_date"));
// //                String weight = cursor.getString(cursor.getColumnIndex("weight"));
//                 cursor.close();
//                 Log.d("ShowPromotion", "4本地数据库返回结果->视图" + code + name);

//                 textView.append("商品名称:" + name + "\n");
//                 textView.append("价格:" + price + "\n");
//                 textView.append("生产日期:" + produced_date + "\n");
//                 textView.append("保质期:" + expiration_date + "\n");
//                 textView.append("-------------------------------\n");
//                 Log.d("ShowPromotion", "5添加数据至视图完成");
//             } catch (Exception e) {
// //                Toast.makeText(getActivity(), "没有在本地数据库找到商品。", Toast.LENGTH_SHORT).show();
//             } finally {
//                 db.close();
//             }
//         }
//     };
}
