package com.example.gioti.temperaturemonitor;

import java.io.Serializable;

/**
 * Save model for temperatures measurements.
 */

class SaveModel implements Serializable{
    private String year,month,date;
    private String temperature;
    private String location;
    private String seconds;

    SaveModel(String year, String month, String date, String temperature, String seconds, String location) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.temperature = temperature;
        this.seconds = seconds;
        this.location=location;
    }

    String getLocation() {
        return location;
    }

    String getYear() {
        return year;
    }

    String getMonth() {
        return month;
    }

    String getDate() {
        return date;
    }

    String getTemperature() {
        return temperature;
    }

    String getSeconds() {

        return seconds;
    }

}
