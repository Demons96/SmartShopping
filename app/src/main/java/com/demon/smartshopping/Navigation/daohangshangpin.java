package com.demon.smartshopping.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.demon.smartshopping.R;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

/**
 * Created by gyp19 on 17-4-27.
 * 商品导航界面
 * 输入：商品名称，位置的二维码
 */

public class daohangshangpin extends Fragment implements TextView.OnClickListener{
    private EditText et_text;   //输入商品名称
    private TextView tv_text;   //显示结果
    private String result = "";   //存扫码结果

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_daohangshangpin, container, false);

        et_text = (EditText) view.findViewById(R.id.edit_text);

        tv_text = (TextView) view.findViewById(R.id.textView_1);

        view.findViewById(R.id.button_1).setOnClickListener(this);  //扫描位置二维码
        view.findViewById(R.id.button_2).setOnClickListener(this);  //确定

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_1:
                intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.button_2: //确定按钮
                tv_text.setText("");
                String com_name = et_text.getText().toString();
                if (result.equals("")) {
                    tv_text.append("未扫描位置二维码\n");
                }
                if (com_name.equals("")) {
                    tv_text.append("未输入商品名称\n");
                }

                if (!result.equals("") && !com_name.equals("")) {
                    intent = new Intent(getActivity(), LuJing.class);
                    intent.putExtra("commodity_name",com_name);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取扫码的结果
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    result = bundle.getString(CodeUtils.RESULT_STRING);
//                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
