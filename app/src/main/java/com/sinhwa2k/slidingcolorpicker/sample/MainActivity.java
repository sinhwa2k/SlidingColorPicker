package com.sinhwa2k.slidingcolorpicker.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        cpv.addColor(new String[] {
                "#f9552e","#ff7800","#ffb400","#6cd128","#07b017",
                "#00b37e","#33cccc","#0066ff","#0c74a5","#6075de",
                "#ce5bf5","#9e37e3","#fa53a7","#9a8170","#996600"
        });
        cpv.setOnColorChange(this);
    }

    @Override
    public void changeColor(String color) {
        Log.v("SlidingColorPicker", color);
        tvTitle.setBackgroundColor(Color.parseColor(color));
    }

}
