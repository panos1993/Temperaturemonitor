package com.example.gioti.temperaturemonitor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by gioti on 23/2/2018.
 */

public class ManageChart {
    public static LineDataSet set1;
    public static LineData data;
    public static ArrayList <ILineDataSet> dataSets;
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
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        ArrayList<Entry> tempValue = new ArrayList<>();
        tempValue.add(new Entry(0f,0f)); //ftiaxnoume mia ne kataxwrisi gia to grafima
        set1 = new LineDataSet(tempValue,"Temp Val"); //dimiourgoume enan neo komvo me plirofories x kai y
        set1.setFillAlpha(110);
        dataSets=new ArrayList<>();//dimiourgoumr mia array list tupou LineDataSet
        dataSets.add(set1);//prosthetoume sto array list pou dimiourgisame parapanw mia kataxwrish
        // me plhrofories gia thermokrasia kai ton xrono pou egine h metrisi
        data = new LineData(dataSets);//Dimiourgoume thesh panw stin grammh tou grafimatos kai
        // prosthetoume tin nea metrish
        chart.setData(data);
        resetGraph(chart);
    }
    public void resetGraph(LineChart chart){
        set1.clear();
        dataSets.clear();
        data.clearValues();
        chart.clearValues();
        chart.invalidate();
    }

    public void setData(LineChart chart, FileManagement fm) {
        //SharedPreferences sharedPreferences = getSharedPreferences("Shared preferences",MODE_PRIVATE);
        set1.addEntry(new Entry(fm.getLastTime(),fm.getLastTemp()));
        dataSets.add(set1);
        data.addDataSet(dataSets.get(dataSets.size()-1));
        chart.setData(data);
        if (data.getEntryCount() == 1) {
            chart.fitScreen();
        }
        chart.notifyDataSetChanged();
        chart.invalidate();

    }
    public void setGraphData(CharSequence[] selectedMesurments, LineChart chart, FileManagement fm, Context context){
        for (int i = 0; i <selectedMesurments.length; i++){
            for(SaveModel pair: fm.ReadFromFile(context)){
                Log.d("Msgss",pair.getDatetime().toString());
                if(pair.getDatetime().toString().equals(selectedMesurments[i])){
                    set1.addEntry(new Entry(pair.getSeconds(),Float.valueOf(pair.getTemperature())));
                    dataSets.add(set1);
                    data.addDataSet(dataSets.get(dataSets.size()-1));
                    chart.setData(data);
                    if (data.getEntryCount() == 1) {
                        chart.fitScreen();
                    }
                    chart.notifyDataSetChanged();

                }
            }
        }
        chart.invalidate();
    }
}
