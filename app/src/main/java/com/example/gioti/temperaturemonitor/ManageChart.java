package com.example.gioti.temperaturemonitor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gioti on 23/2/2018.
 */

public class ManageChart {
    public static LineDataSet set1;
    public static LineData data;
    public static ArrayList <ILineDataSet> dataSets;
    public float quart=0f;
    public ManageChart() {

    }
    public void setStyleChart(LineChart chart){
        chart.setDrawBorders(false);
        chart.setMaxVisibleValueCount(1000);
        Description d = new Description();
        d.setText("Temperature Monitor");
        chart.setDescription(d);
        chart.setBackgroundColor(Color.rgb(27,120,196));
        chart.setBorderColor(Color.RED);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false); // if disabled, scaling can be done on x- and y-axis separately
        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);
        chart.setBorderWidth(3f);
        XAxis x = chart.getXAxis();
        x.setEnabled(true);
        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setEnabled(true);
        // add data
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        // dont forget to refresh the drawing
        chart.invalidate();
    }

    public void InitializeChart(LineChart chart){
            ArrayList<Entry> tempValue = new ArrayList<>();
            tempValue.add(new Entry(0f,0f)); //ftiaxnoume mia nea kataxwrisi gia to grafima
            set1 = new LineDataSet(tempValue,"Temp Val"); //dimiourgoume enan neo komvo me plirofories x kai y
            set1.setFillAlpha(110);
            dataSets=new ArrayList<>();//dimiourgoumr mia array list tupou LineDataSet
            dataSets.add(set1);//prosthetoume sto array list pou dimiourgisame parapanw mia kataxwrish
            // me plhrofories gia thermokrasia kai ton xrono pou egine h metrisi
            data = new LineData(dataSets);//Dimiourgoume thesh panw stin grammh tou grafimatos kai
            // prosthetoume tin nea metrish
            chart.setData(data);
            resetGraph(chart);
            chart.invalidate();
    }
    public void resetGraph(LineChart chart){
        set1.clear();
        dataSets.clear();
        data.clearValues();
        chart.clearValues();
        chart.invalidate();
    }

    public void setData(LineChart chart, final FileManagement fm) {
        set1.addEntry(new Entry(quart,fm.getLastTemp()));
        dataSets.add(set1);
        data.addDataSet(dataSets.get(dataSets.size()-1));
        chart.setData(data);
        if (data.getEntryCount() == 1) {
            chart.fitScreen();
        }
        chart.notifyDataSetChanged();
        if(quart>0){
            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return FileManagement.getAllSecond()[(int) value];
                }


            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
        }

        chart.invalidate();
        quart++;
    }
    public void setGraphData(final ArrayList<SaveModel> selectedMeasurements, LineChart chart, FileManagement fm, Context context){
        float i=0f;
        final ArrayList <String> s = new ArrayList<>();
        for(SaveModel pair: selectedMeasurements){
            set1.addEntry(new Entry(i,Float.valueOf(pair.getTemperature())));
            dataSets.add(set1);
            data.addDataSet(dataSets.get(dataSets.size()-1));
            chart.setData(data);
            if (data.getEntryCount() == 1) {
                        chart.fitScreen();
            }
            s.add(pair.getSeconds());

            chart.notifyDataSetChanged();
            i++;

        }
        if(s.size()>1){
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return s.toArray(new String[s.size()])[(int) value];
                }


            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(0f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
        }
        chart.invalidate();
    }


}
