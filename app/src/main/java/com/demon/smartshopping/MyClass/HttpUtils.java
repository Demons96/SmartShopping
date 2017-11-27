package com.demon.smartshopping.MyClass;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by gyp19 on 17-4-4.
 * 网络请求
 */


public class HttpUtils {
    public static String LOGIN_PHONE = "";  //暂时用来退货删数据

    private Handler mHandler = null;
    private static HttpUtils mInstance;

    private HttpUtils() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static HttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    public void doGet(final String url, final HttpResponseListernr listernr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);

                    InputStream response = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response));
                    StringBuilder sb = new StringBuilder();

                    String len = "";
                    while ((len = reader.readLine()) != null) {
                        sb.append(len);
                    }

                    sendRequestSuccessCallback(listernr, sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    sendRequestErrorCallback(listernr, "网络错误");
                } catch (IOException e) {
                    e.printStackTrace();
                    sendRequestErrorCallback(listernr, "网络错误");
                } finally {
                    connection.disconnect();
                }
            }
        }).start();
    }

    public void doPost(final String url, final Map<String, String> params, final HttpResponseListernr mListernr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.setDoInput(true);

                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(appendParam(params));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    sendRequestSuccessCallback(mListernr, sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    sendRequestErrorCallback(mListernr, "网络出错");
                } finally {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     * @param params username=xxx&password=xxxxx
     * @return
     * 拼接表单
     */
    private String appendParam(Map<String, String> params) {
        int modCout = 0;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            if (modCout != params.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    private void sendRequestSuccessCallback(final HttpResponseListernr listernr, final String result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listernr.onResponse(result);
            }
        });
    }

    private void sendRequestErrorCallback(final HttpResponseListernr listernr, final String result) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listernr.onError(result);
            }
        });
    }

//    public static void login(final String url) throws MalformedURLException {
//        final URL url1 = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
//
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//
//                    connection.setRequestMethod("GET");
//                    connection.setReadTimeout(5000);
//                    connection.setConnectTimeout(5000);
//                    connection.setDoOutput(true);
//                    connection.setDoInput(true);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    connection.disconnect();
//                }
//            }
//        }).start();
//
////        new Thread(new Runnable(){
////            @Override
////            public void run() {
////
////                try {
////
////                    Log.d("HttpUtils", "发送的链接："+ url);
////                    connection = (HttpURLConnection) new URL(url).openConnection();
////                    connection.setRequestMethod("GET");
////                    connection.getInputStream();
////                    Thread.sleep(8000);
////                    connection.disconnect();
////                    Log.d("HttpUtils", "链接已关闭");
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        }).start();
//    }

    public interface HttpResponseListernr {
        void onResponse(String result);
        void onError(String msg);
    }
}

