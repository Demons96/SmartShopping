package com.demon.smartshopping.Test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import  com.demon.smartshopping.MyClass.Commodity;
import  com.demon.smartshopping.R;

import java.util.List;

/**
 * Created by gyp19 on 17-5-5.
 * 商品数据适配器，用于列表显示商品数据
 */

public class CommodityAdapter extends ArrayAdapter<Commodity> {
    private int resourceId;

    public CommodityAdapter(Context context, int textViewResourceId, List<Commodity> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Commodity commodity = getItem(position);    //获取commodity实例
        View view;
        ViewHolder viewHolder;  //不用每次都调用控件的实例
        
        //判断是否有缓存
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.commodityName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.commodityPrice = (TextView) view.findViewById(R.id.tv_price);
            view.setTag(viewHolder);    //将viewHolder存在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.commodityName.setText(commodity.name);
        viewHolder.commodityPrice.setText(commodity.price);
        return view;
    }

    class ViewHolder {
        TextView commodityName;
        TextView commodityPrice;
    }
}
