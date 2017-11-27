package com.demon.smartshopping.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.demon.smartshopping.MainActivity;
import com.demon.smartshopping.MyClass.HttpUtils;
import com.demon.smartshopping.R;

/**
 * 带记住密码的登录功能
 */
public class MyLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyLogin";

    private ProgressDialog progressDialog;  //登录中的进度条

    private SharedPreferences pref;     //读
    private SharedPreferences.Editor editor;    //写

    private EditText accountEdit;
    private EditText passwordEdit;

    private CheckBox rememberPass;

    private String responseData = "0";
    private String account_save;
    private String password_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        accountEdit = (EditText) findViewById(R.id.account);

        passwordEdit = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);

        findViewById(R.id.login).setOnClickListener(this);      //登录
        findViewById(R.id.register).setOnClickListener(this);   //注册

        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            account_save = pref.getString("account", "");
            password_save = pref.getString("password", "");
            accountEdit.setText(account_save);
            passwordEdit.setText(password_save);
            rememberPass.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                progressDialog = new ProgressDialog(MyLogin.this);
//                progressDialog.setTitle("登录");
                progressDialog.setMessage("正在登录...");
                progressDialog.setCancelable(true);
                progressDialog.show();

                StartLogin();
                break;
            case R.id.register:
                Intent intent = new Intent(MyLogin.this, Register.class);
                startActivity(intent);
                break;
        }
    }

    private void StartLogin() {
        final String account = accountEdit.getText().toString();    //电话
        final String password = passwordEdit.getText().toString();  //密码

        if(account.equals("") || password.equals("")){
            Toast.makeText(this, "手机号或者密码为空...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();   //关闭登录进度条
            return;
        }

        //此请求向服务器获取与登录名对应的密码
        String my_url = "http://123.206.23.219/SmartShopping/API/selectPassFromUser.php?name=" + account;

        HttpUtils.getInstance().doGet(my_url, new HttpUtils.HttpResponseListernr() {
            @Override
            public void onResponse(String result) {
                responseData = result;

                if (password.equals(responseData)) {
                    editor = pref.edit();
                    if (rememberPass.isChecked()) { // 检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                    //登录成功后删除服务器上原有商品清单
                    String url_deletUserCommodity =
                            "http://123.206.23.219/SmartShopping/API/DeleteUserCommodity.php?phone=" + account;
                    DeleteInventoryOfClound(url_deletUserCommodity);

                    //暂时这么写存储用户手机号,后来突然加的退货功能用
                    HttpUtils.LOGIN_PHONE = account;

                    progressDialog.dismiss();   //关闭登录进度条

                    //进入主界面
                    Intent intent = new Intent(MyLogin.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();   //关闭登录进度条
                    Toast.makeText(MyLogin.this, "用户名或者密码错误！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String msg) {
                progressDialog.dismiss();   //关闭登录进度条
                Toast.makeText(MyLogin.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DeleteInventoryOfClound(final String url) {
        HttpUtils.getInstance().doGet(url, new HttpUtils.HttpResponseListernr() {
            @Override
            public void onResponse(String result) {
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(MyLogin.this, "msg", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
