package com.example.bluetooth01;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter bluetoothAdapter=null;

    private BluetoothReceiver bluetoothReceiver=null;
   // public static StringBuffer enableDevices=new StringBuffer();
    private TextView display;
    private TextView display1;
    private Button  enable=null;
    private Button discover=null;
    private Button bounded=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display=(TextView)findViewById(R.id.display);
        display1=(TextView)findViewById(R.id.display1);
        discover=(Button)findViewById(R.id.discoverButtonId);
        discover.setOnClickListener(this);
        bounded=(Button)findViewById(R.id.boundedButtonId);
        bounded.setOnClickListener(this);
        enable=(Button)findViewById(R.id.enableButtonId);
        enable.setOnClickListener(this);

    }
    private class BluetoothReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //传入一个键
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                display1.append(device.getAddress()+"\n");
                Log.d("MainActivity",device.getAddress());
            }
        }
    }

    @Override
    public void onClick(View view) {
        //得到BluetoothAdapter对象
        switch (view.getId()){
            case R.id.boundedButtonId:
                //bluetoothAdapter.startDiscovery();
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //判断BluetoothAdapter是否为空，如果为空，则表明本机没有蓝牙设备
                if (bluetoothAdapter != null) {
                    Log.d("MainActivity", "本机拥有蓝牙设备");
                    //调用isEnabled()方法，判断当前蓝牙设备是否可用（打开）
                    if (!bluetoothAdapter.isEnabled()) {
                        //创建一个intent对象，该对象用于启动一个Activity，提示用户开启蓝牙设备
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivity(intent);
                    }
                    //得到所有已经配对的蓝牙适配器对象
                    Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                    StringBuffer bounedDevices=new StringBuffer();
                    if (devices.size() > 0) {
                        for (Iterator iterator = devices.iterator(); iterator.hasNext(); ) {
                            BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
                            //得到远程蓝牙设备的地址
                            bounedDevices.append(bluetoothDevice.getAddress()+'\n');
                            Log.d("MainActivity", bluetoothDevice.getAddress());
                        }
                        display.setText(bounedDevices);
                    }
                } else {
                    Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", "没有蓝牙设备");
                }
                break;
            case R.id.discoverButtonId:
                //创建一个Intent对象，并将其action的值设置为BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE，请求修改可见性
                Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //设置可见状态持续时间
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,500);
                //这个Activity是android里自带的Activity
                startActivity(discoverableIntent);
                break;
            case R.id.enableButtonId:
                //创建一个intentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
                IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                //创建一个蓝牙的广播接收器
                bluetoothReceiver=new BluetoothReceiver();
                //注册广播接收器
                registerReceiver(bluetoothReceiver,intentFilter);
                //获得蓝牙适配器实例
                bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.startDiscovery();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }
}
