package com.example.gioti.temperaturemonitor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by gioti on 14/2/2018.
 */

public class SaveModel implements Serializable{
    private Date datetime;
    private String temperature;
    private int seconds;

    public SaveModel(Date datetime, String temperature, int seconds) {
        this.datetime = datetime;
        this.temperature = temperature;
        this.seconds = seconds;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
