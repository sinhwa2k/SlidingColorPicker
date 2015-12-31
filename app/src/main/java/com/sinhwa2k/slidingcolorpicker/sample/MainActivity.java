package com.sinhwa2k.slidingcolorpicker.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sinhwa2k.slidingcolorpicker.ColorPickerView;
import com.sinhwa2k.slidingcolorpicker.OnColorChange;
import com.sinhwa2k.slidingcolorpicker.R;

public class MainActivity extends AppCompatActivity implements OnColorChange {

    TextView tvTitle;
    ColorPickerView cpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = (TextView)findViewById(R.id.MainActivity_tvTitle);
        cpv = (ColorPickerView)findViewById(R.id.MainActivity_cpv);
        cpv.setOnColorChange(this);
    }

    @Override
    public void changeColor(int color) {
        tvTitle.setBackgroundColor(color);
    }
}
