package com.example.gioti.temperaturemonitor;

import android.util.Log;

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
             Log.d("MSG_Value_Formater", String.valueOf(value));
             if(value==-1){
                 return null;
             }
            if(mValues.length > (int) value) {
                return mValues[(int) value];
            } else return null;
        }



}
