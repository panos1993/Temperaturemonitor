package com.example.gioti.temperaturemonitor;

import java.io.Serializable;

/**
 * This model saves all the information about every measurement which is realized
 */

class SaveModel implements Serializable{
    private String year,month,date;
    private String temperature;
    private String location;
    private String seconds;
    private int quart;
    private int red;
    private int green;
    private int blue;


    SaveModel(String year, String month, String date, String temperature, String seconds, String location) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.temperature = temperature;
        this.seconds = seconds;
        this.location=location;
    }

    int getRed() {
        return red;
    }

    int getGreen() {
        return green;
    }

    int getBlue() {
        return blue;
    }

    void setRed(int red) {
        this.red = red;
    }

    void setGreen(int green) {
        this.green = green;
    }

    void setBlue(int blue) {
        this.blue = blue;
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

    String getSeconds() {return seconds;}

    void setQuart(int quart) {
        this.quart = quart;
    }

    int getQuart() {return quart;}

    @Override
    public boolean equals(Object obj) {

        return (this.location.equals(((SaveModel) obj).location)
                && this.year.equals(((SaveModel) obj).year)
                && this.month.equals(((SaveModel) obj).month)
                && this.date.equals(((SaveModel) obj).date)
                && this.seconds.equals(((SaveModel) obj).seconds)
                && this.temperature.equals(((SaveModel) obj).temperature));
    }
}
