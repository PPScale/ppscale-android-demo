package com.lefu.ppscale.wifi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lefu.ppscale.wifi.DBManager;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.adapter.DeviceListAdapter;
import com.lefu.ppscale.wifi.model.DeviceModel;


import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        final List<DeviceModel> list = DBManager.manager().getDeviceList();
        final DeviceListAdapter adapter = new DeviceListAdapter(DeviceListActivity.this, R.layout.list_view_device, list);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.manager().deleteDevice(list.get(position));
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("取消", null);
                builder.show();
                return false;
            }

        });
    }
}
