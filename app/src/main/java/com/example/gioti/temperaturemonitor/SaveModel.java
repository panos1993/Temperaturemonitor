package com.example.gioti.temperaturemonitor;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gioti on 14/2/2018.
 */

public class SaveModel implements Serializable{
    private String year,month,date;
    private String temperature;
    private String location;
    private String seconds;

    public SaveModel(String year, String month, String date, String temperature, String seconds,String location) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.temperature = temperature;
        this.seconds = seconds;
        this.location=location;
    }

    public String getLocation() {
        return location;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSeconds() {

        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
}
