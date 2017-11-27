package com.demon.smartshopping.MyClass;

/**
 * Created by gyp19 on 17-5-5.
 * 商品类
 */

public class Commodity {
    public String bar_code;
    public String name;
    public String price;
    public String produced_date;
    public String expiration_date;
    public String weight;

    public Commodity() {
        this.bar_code = "";
        this.name = "";
        this.price = "";
        this.produced_date = "";
        this.expiration_date = "";
        this.weight = "";
    }
    public Commodity(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Commodity(String bar_code, String name, String price) {
        this.bar_code = bar_code;
        this.name = name;
        this.price = price;
    }

    public String getBar_code() {
        return bar_code;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
