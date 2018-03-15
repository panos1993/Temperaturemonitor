package com.example.gioti.temperaturemonitor;

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
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 */

public class ManageChart {

    private  LineDataSet set1;
    public  LineData data;
    private  ArrayList <ILineDataSet> dataSets;
    float  quart=0f;
    private ArrayList<ArrayList<SaveModel>> dataMeasurements = new ArrayList<>();
   private ArrayList<hasDataHelpCompareCharts> allDataFromChartSorted= new ArrayList<>();
    ManageChart() {
    }

    void setStyleChart(LineChart chart){
        chart.setDrawBorders(false);
        chart.setMaxVisibleValueCount(1000);
        Description d = new Description();
        d.setText("Temperature Monitor");
        chart.setDescription(d);
        chart.setBackgroundColor(Color.rgb(211,211,211));
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
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setAxisLineColor(Color.BLACK);
        chart.getAxisRight().setEnabled(true);
        // add data
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        // dont forget to refresh the drawing
        chart.invalidate();
    }

    void InitializeChart(LineChart chart){
        chart.fitScreen();
        ArrayList<Entry> tempValue = new ArrayList<>();
        tempValue.add(new Entry(0f,0f)); //ftiaxnoume mia nea kataxwrisi gia to grafima
        set1 = new LineDataSet(tempValue,"Temp Val"); //dimiourgoume enan neo komvo me plirofories x kai y
        set1.setColor(Color.rgb(57,57,57));
        set1.setFillAlpha(110);
        dataSets=new ArrayList<>();//dimiourgoumr mia array list tupou LineDataSet
        dataSets.add(set1);//prosthetoume sto array list pou dimiourgisame parapanw mia kataxwrish
        // me plhrofories gia thermokrasia kai ton xrono pou egine h metrisi
        data = new LineData(dataSets);//Dimiourgoume thesh panw stin grammh tou grafimatos kai
        // prosthetoume tin nea metrish
        set1.clear();
        dataSets.clear();
        data.clearValues();
        chart.setData(data);
        chart.clearValues();
       // resetGraph(chart);
        chart.invalidate();
        quart=0f;
    }

    void resetGraph(LineChart chart){
        chart.fitScreen();
        data.clearValues();
        chart.clearValues();
        chart.setData(data);
        chart.invalidate();
        quart=0f;
    }

    void setData(LineChart chart) {
        set1.addEntry(new Entry(quart,FileManagement.getLastTemp()));
        dataSets.add(set1);
        data.addDataSet(dataSets.get(dataSets.size()-1));
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(0f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        if(quart>0){

            xAxis.setValueFormatter(new MyXAxisValueFormatter(FileManagement.getAllSecond()));
        }

        chart.setData(data);

        if (data.getEntryCount() == 1) {
            chart.fitScreen();
        }

        chart.notifyDataSetChanged();
        chart.invalidate();
        quart++;
    }

    void cleanListsForSaveChart(){
        dataMeasurements.clear();
        allDataFromChartSorted.clear();
    }

    void drawSaveCharts(LineChart chart, ArrayList<SaveModel> tempDataList){
        resetGraph(chart);
        boolean TheMeasurementAlreadyIsOpen=false;
        int [] colorLineChart =new int [3];
        for(ArrayList<SaveModel> pair : dataMeasurements){ //check if a measurement is already open. If is already open add the new temp data in this measurement but keep the old who we had opened
            for (SaveModel pair2 : pair) {
                for (SaveModel pair3 : tempDataList) {
                    if (pair3.getLocation().equals(pair2.getLocation()) && pair3.getYear().equals(pair2.getYear()) && pair3.getMonth().equals(pair2.getMonth()) && pair3.getDate().equals(pair2.getDate())) {
                        pair3.setGreen(pair2.getGreen());
                        pair3.setBlue(pair2.getBlue());
                        TheMeasurementAlreadyIsOpen = true;
                    }
                }
            }
            if(TheMeasurementAlreadyIsOpen){ //if measurement already is open add the new data in list  which contain the old data who we han opened.
                pair.addAll(tempDataList);
                break;// break the first for and continue
            }
        }

        if(!TheMeasurementAlreadyIsOpen) { //if the measurement didn't opened we are give random color which we will use for apear the measurement in graph with this color.
            for (int i = 0; i < 3; i++) {
                colorLineChart[i] = new Random().nextInt(255); //the first time take red value the second time take green value and third time take blue value.
            }
            for (SaveModel pair : tempDataList) {
                pair.setRed(colorLineChart[0]);
                pair.setGreen(colorLineChart[1]);
                pair.setBlue(colorLineChart[2]);
            }
            dataMeasurements.add(tempDataList);//add new measuremnt in dataMeasurements list
        }
        int quart=0;
        if(dataMeasurements.size()>1){ //if has at least one measurement in list dataMeasurements.
            for(ArrayList<SaveModel> pair : dataMeasurements){
               for(SaveModel pair2 : pair){
                   allDataFromChartSorted.add(new hasDataHelpCompareCharts(pair2.getSeconds(),0)); //add all data from dataMeasurements list to allDataFromChartSorted for sorted based on time.
               }
            }

            Collections.sort(allDataFromChartSorted, (hasDataHelpCompareCharts obj1, hasDataHelpCompareCharts obj2) -> (Integer.valueOf(obj1.getTime().replaceAll(":", ""))
                    < Integer.valueOf(obj2.getTime().replaceAll(":", "")))
                    ? -1 :(Integer.valueOf(obj1.getTime().replaceAll(":", "")) > Integer.valueOf(obj2.getTime().replaceAll(":", "")))
                    ? 1 : 0);

            for(hasDataHelpCompareCharts pair : allDataFromChartSorted){
                pair.setQuart(quart);
                quart++;
            }
            for(ArrayList<SaveModel> pair : dataMeasurements) {
                for (SaveModel pair2 : pair) {
                    for(hasDataHelpCompareCharts pair3 : allDataFromChartSorted){
                       if(pair2.getSeconds().equals(pair3.getTime())){
                           pair2.setQuart(pair3.getQuart());
                       }
                    }
                }
            }
        }

        for(ArrayList<SaveModel> pair : dataMeasurements) {
            Collections.sort(pair, (SaveModel obj1, SaveModel obj2) -> (obj1.getQuart() < obj2.getQuart()) ? -1: (obj1.getQuart() > obj2.getQuart()) ? 1:0);
        }

        for(ArrayList<SaveModel> pair : dataMeasurements) {
            for (SaveModel pair2 : pair) {
                Log.d("MSG", "Year" + pair2.getYear() + "Loc" + pair2.getLocation() + "Month" + pair2.getMonth() +"date" + pair2.getDate() + "Quart" + pair2.getQuart());
            }
        }

        ArrayList <String> s = new ArrayList<>();
        List<List<Entry>> tempValue1 = new ArrayList<>();
        for(ArrayList<SaveModel> pair : dataMeasurements) {
            List<Entry> tempValue = new ArrayList<>();
            for (SaveModel pair2 : pair) {
                if(dataMeasurements.size()>1){
                    tempValue.add(new Entry(pair2.getQuart(), Float.valueOf(pair2.getTemperature())));
                    s.add(pair2.getSeconds());
                }else{
                    tempValue.add(new Entry(quart, Float.valueOf(pair2.getTemperature())));
                    s.add(pair2.getSeconds());
                    quart++;
                }

            }
            tempValue1.add(tempValue);
        }
        int x=0;
        for(List<Entry> pair : tempValue1){
            LineDataSet setTempValue1 = new LineDataSet(pair, dataMeasurements.get(x).get(0).getDate() + " / " + dataMeasurements.get(x).get(0).getMonth() + " / " + dataMeasurements.get(x).get(0).getYear());
            setTempValue1.setColor(Color.rgb(dataMeasurements.get(x).get(0).getRed(),dataMeasurements.get(x).get(0).getGreen(),dataMeasurements.get(x).get(0).getBlue()));
            setTempValue1.setLineWidth(3f);
            ILineDataSet dataSets = setTempValue1;
            data.addDataSet(dataSets);
            x++;
        }



        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(0f); // minimum axis-step (interval) is 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Collections.sort(s, (String obj1, String obj2) -> (Integer.valueOf(obj1.replaceAll(":", "")) < Integer.valueOf(obj2.replaceAll(":", "")))
                ? -1 : (Integer.valueOf(obj1.replaceAll(":", "")) > Integer.valueOf(obj2.replaceAll(":", "")))
                ? 1 : 0);
        if(s!=null){
            if(s.size()>1){
                xAxis.setValueFormatter(new MyXAxisValueFormatter(s.toArray(new String[s.size()])));
            }
        }

        chart.setData(data);
        chart.notifyDataSetChanged();
        //chart.setData(data);
        chart.invalidate();
        allDataFromChartSorted.clear();
        s.clear();
        OpenSaveCharts.mMenu.findItem(R.id.action_clear_graph).setEnabled(true);

    }
}
