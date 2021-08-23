package com.lefu.ppscale.ble.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.peng.ppscale.vo.PPBodyFatModel;

import java.util.List;

public class BodyDataDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);
        TextView data_detail_history = findViewById(R.id.data_detail_history);
        PPBodyFatModel bodyData = DataUtil.util().getBodyDataModel();
        if (bodyData != null && !TextUtils.isEmpty(bodyData.toString())) {
            textView.setText(bodyData.toString());
        }

        List<PPBodyFatModel> historyData = DataUtil.util().getHistoryData();

        StringBuffer buffer = new StringBuffer();
        buffer.append("历史数据" + "\r\n");
        if (historyData != null && !historyData.isEmpty()) {
            for (int i = 0; i < historyData.size(); i++) {
                PPBodyFatModel bodyFatModel = historyData.get(i);
                buffer.append("weightKg = " + bodyFatModel.getPpWeightKg());
                buffer.append("    impedace = " + bodyFatModel.getImpedance());
                buffer.append("\r\n");
            }
        }
        data_detail_history.setText(buffer.toString());

    }
}
