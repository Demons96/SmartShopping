package com.demon.smartshopping.Test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.demon.smartshopping.MyClass.MyBluetoothIO;
import com.demon.smartshopping.R;

/**
 * 搜索蓝牙设备并选择链接
 */
public class lianjielanya extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<String> bluetoothDevicesName = new ArrayList<>();    //所有搜索到的蓝牙名称
    private List<String> bluetoothDevicesAddress = new ArrayList<>();    //所有搜索到的蓝牙地址
    private ArrayAdapter<String> arrayAdapter;  //数组适配器显示列表

    //客户端
    private BluetoothAdapter bluetoothAdapter;  //蓝牙适配器
    private BluetoothSocket cSocket;
    private BluetoothDevice device;     //设备的信息

    private OutputStream os_c;
    private InputStream is_c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lianjielanya);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lvDevices = (ListView) findViewById(R.id.lvDevices);
        lvDevices.setOnItemClickListener(this);

        //打开蓝牙
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();

        arrayAdapter = new ArrayAdapter<>(lianjielanya.this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                bluetoothDevicesName);
        lvDevices.setAdapter(arrayAdapter);

        //注册每搜到一个蓝牙设备的广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);

        //停止当前搜索重新搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    //广播接收器
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //判断广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //如果找到这个设备来获得这个设备的信息
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //判断当前设备是否被绑定
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    bluetoothDevicesName.add("*" + device.getName());
                    bluetoothDevicesAddress.add(device.getAddress());
                    arrayAdapter.notifyDataSetChanged();
                } else if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    bluetoothDevicesName.add(device.getName());
                    bluetoothDevicesAddress.add(device.getAddress());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        String address = bluetoothDevicesAddress.get(i);//获取蓝牙设备的地址

        try {
            //如果还在搜索则暂停
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                if (device == null) {
                    device = bluetoothAdapter.getRemoteDevice(address); //获得远程设备，但是并没有链接
                }
                if (cSocket == null) {
                    cSocket = device.createRfcommSocketToServiceRecord(MyBluetoothIO.MY_UUID);
                    cSocket.connect(); //开始正式链接
                    os_c = cSocket.getOutputStream();
                    is_c = cSocket.getInputStream();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //如果成功获取输出流
            if (os_c != null) {
                MyBluetoothIO.setOs_c(os_c);
                MyBluetoothIO.setIs_c(is_c);
                MyBluetoothIO.SendClean();      //清零硬件购物车的称
                Toast.makeText(lianjielanya.this, "蓝牙链接成功", Toast.LENGTH_SHORT).show();
                finish();   //结束当前活动
            } else {
                Toast.makeText(lianjielanya.this, "好像没有链接成功23336666", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
