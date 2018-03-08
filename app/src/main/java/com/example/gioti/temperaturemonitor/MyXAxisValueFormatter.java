package com.example.gioti.temperaturemonitor;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by gioti on 8/3/2018.d
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {


        private String[] mValues;

    MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }



}
