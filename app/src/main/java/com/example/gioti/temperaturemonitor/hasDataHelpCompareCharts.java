package com.example.gioti.temperaturemonitor;

/**
 * Created by gioti on 9/3/2018.d
 */

public class hasDataHelpCompareCharts {
    private String time;
    private int quart;

    hasDataHelpCompareCharts(String time, int quart) {

        this.time = time;
        this.quart = quart;
    }
    public String getTime() {
        return time;
    }


    int getQuart() {
        return quart;
    }

    public void setTime(String time) {

        this.time = time;
    }

    void setQuart(int quart) {
        this.quart = quart;
    }

}

