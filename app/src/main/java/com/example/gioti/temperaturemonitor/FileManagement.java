package com.example.gioti.temperaturemonitor;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class FileManagement implements Serializable{

    private static ArrayList<SaveModel> temperatures = new ArrayList<>();
    private static ArrayList<SaveModel> saveModelForLoadFile ;
    static Integer time = 0;

    public static void setTemp(String temp, String location){

        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MMM");
        SimpleDateFormat date = new SimpleDateFormat("dd");
        Date second = new Date();
        String currentTime = DateFormat.getTimeInstance().format(second);

        temperatures.add(new SaveModel(year.format(new Date()),month.format(new Date()),date.format(new Date()),temp,currentTime,location));
        time +=10;
    }
    public static String getLastTime(){
        SaveModel pair = temperatures.get(temperatures.size()-1);
        //String seconds =  pair.getSeconds().replaceAll("[:]","");

        return pair.getSeconds();
    }
    public static Float getLastTemp (){
        SaveModel pair = temperatures.get(temperatures.size()-1);
         return Float.parseFloat(pair.getTemperature());
    }
    public  static String[] getAllSecond(){

        ArrayList <String> sa = new ArrayList<>();
        for (SaveModel pair: temperatures) {
          sa.add(pair.getSeconds());
        }
        String [] s = sa.toArray(new String[sa.size()]);
        return s;
    }
    public static ArrayList<String> getDate(){
        ArrayList<String> list = new ArrayList<>();
        for (SaveModel pair: temperatures) {
            list.add(pair.getDate());
        }
        return list;
    }
    public static ArrayList<String> getOnlyAllTemperatures(){
        ArrayList<String> list = new ArrayList<>();
        for (SaveModel pair: temperatures) {
            list.add(pair.getTemperature());
        }
        return list;
    }

    public static ArrayList<SaveModel> getAllData(){
       return temperatures;
    }

    /**
     * ****** Save to file *******
     * @param context ta periexomena tis klashs MainActivity.
     */
    public static void SaveToFile(Context context){
            if(ReadFromFile(context).size()>0){
                temperatures.addAll(ReadFromFile(context));
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("Shared preferences",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String stringToSaveAtFile = gson.toJson(temperatures);
            editor.putString("Graph data",stringToSaveAtFile);
            editor.apply();




    }

    /**
     * ****** Read From File *******
     * @return SaveModel type which contain information for old temperature measurements
     */
    public static ArrayList<SaveModel> ReadFromFile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String stringFromFile = sharedPreferences.getString("Graph data",null);
        Type type = new TypeToken<ArrayList<SaveModel>>(){}.getType();
        saveModelForLoadFile = gson.fromJson(stringFromFile, type);
        if(saveModelForLoadFile == null){
            saveModelForLoadFile = new ArrayList<>();
        }
        return saveModelForLoadFile;
    }
    public void deleteAllDataTemperatures(){
        temperatures.clear();
    }

}
