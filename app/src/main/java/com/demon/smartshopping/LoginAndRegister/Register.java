package com.demon.smartshopping.LoginAndRegister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demon.smartshopping.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 注册功能
 */
public class Register extends AppCompatActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etPhone;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = (EditText) findViewById(R.id.name);
        etPhone = (EditText) findViewById(R.id.phonenumber);
        etPassword = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                String name = etName.getText().toString();
                String Phone = etPhone.getText().toString();
                String Password = etPassword.getText().toString();
                if(name.equals("") || Phone.equals("") || Password.equals("")){
                    Toast.makeText(this, "属性为空", Toast.LENGTH_SHORT).show();
                }else {
                    String my_url = "http://123.206.23.219/SmartShopping/API/AddUser.php?name=" + name + "&phone=" + Phone + "&password=" + Password;
                    sendRequestWithOkHttp(my_url);
                    Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void sendRequestWithOkHttp(final String myurl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(myurl)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                } catch (Exception e) {

                }
            }
        }).start();
    }
}