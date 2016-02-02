package com.sinhwa2k.slidingcolorpicker.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinhwa2k.slidingcolorpicker.ColorPickerView;
import com.sinhwa2k.slidingcolorpicker.OnColorChange;
import com.sinhwa2k.slidingcolorpicker.R;

public class MainActivity extends AppCompatActivity implements OnColorChange, View.OnClickListener{

    TextView tvTitle;
    ColorPickerView cpv;
    Button btnToggleArrow;
    Button btnToggleCenterLine;
    Button btnGetCenterColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = (TextView)findViewById(R.id.MainActivity_tvTitle);

        cpv = (ColorPickerView)findViewById(R.id.MainActivity_cpv);
        cpv.addColor(getResources().getStringArray(R.array.colors));
        cpv.setOnColorChange(this);

        btnToggleArrow = (Button)findViewById(R.id.MainActivity_btnToggleArrow);
        btnToggleArrow.setOnClickListener(this);
        btnToggleCenterLine = (Button)findViewById(R.id.MainActivity_btnToggleCenterLine);
        btnToggleCenterLine.setOnClickListener(this);
        btnGetCenterColor = (Button)findViewById(R.id.MainActivity_btnGetCenterColor);
        btnGetCenterColor.setOnClickListener(this);
    }

    @Override
    public void changeColor(String color) {
        Log.v("SlidingColorPicker", color);
        tvTitle.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.MainActivity_btnToggleArrow:
                if(cpv.isVisibleArrow()) {
                    cpv.setVisibleArrow(false);
                } else {
                    cpv.setVisibleArrow(true);
                }
                break;
            case R.id.MainActivity_btnToggleCenterLine:
                if(cpv.isVisibleCenterLine()) {
                    cpv.setVisibleCenterLine(false);
                } else {
                    cpv.setVisibleCenterLine(true);
                }
                break;
            case R.id.MainActivity_btnGetCenterColor:
                Toast.makeText(this, cpv.getCenterColor(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
