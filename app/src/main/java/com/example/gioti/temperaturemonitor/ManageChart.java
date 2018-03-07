package com.example.gioti.temperaturemonitor;

import android.graphics.Color;
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
import java.util.Random;

/**
 * Created by gioti on 23/2/2018.d
 */

public class ManageChart {

    private  LineDataSet set1;
    public  LineData data;
    private  ArrayList <ILineDataSet> dataSets;
    float  quart=0f;

    ManageChart() {
    }

    void setStyleChart(LineChart chart){
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
        chart.setMaxHighlightDistance(300);
        chart.setBorderWidth(3f);
        XAxis x = chart.getXAxis();
        x.setEnabled(true);
        YAxis y = chart.getAxisLeft();
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setEnabled(true);
        // add data
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        // dont forget to refresh the drawing
        chart.invalidate();
    }

    void InitializeChart(LineChart chart){
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

    void resetGraph(LineChart chart){
        set1.clear();
        dataSets.clear();
        data.clearValues();
        chart.clearValues();
        chart.invalidate();
        quart=0f;
    }

    void setData(LineChart chart) {
        set1.addEntry(new Entry(quart,FileManagement.getLastTemp()));
        dataSets.add(set1);
        data.addDataSet(dataSets.get(dataSets.size()-1));
        chart.setData(data);
        if (data.getEntryCount() == 1) {
            chart.fitScreen();
        }
        chart.notifyDataSetChanged();
       if(FileManagement.getAllSecond().length>1){
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

    void refreshGraph(LineChart chart, ArrayList<SaveModel> tempDataList){
        final ArrayList <String> s = new ArrayList<>();
        List<Entry> tempValue = new ArrayList<>();
        float i=0f;

        for (SaveModel pair : tempDataList) {
            tempValue.add(new Entry(i, Float.valueOf(pair.getTemperature())));
            s.add(pair.getSeconds());
            i++;
        }

        LineDataSet setTempValue1 = new LineDataSet(tempValue, tempDataList.get(0).getDate()+tempDataList.get(0).getMonth()+tempDataList.get(0).getYear());
        setTempValue1.setColor(Color.rgb(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
        setTempValue1.setLineWidth(3f);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setTempValue1);
        data.addDataSet(dataSets.get(0));

        if (data.getEntryCount() == 1) {
            chart.fitScreen();
        }

        chart.setData(data);
        chart.invalidate();

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
    }
}
