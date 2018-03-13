package com.example.gioti.temperaturemonitor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static android.content.Context.MODE_PRIVATE;

class FileManagement implements Serializable{

    private static ArrayList<SaveModel> temperatures = new ArrayList<>();

    static void setTemp(String temp, String location){
        Calendar cal1 = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat year = new SimpleDateFormat("yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat month = new SimpleDateFormat("MMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd");
       // Date second = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = dateFormat.format(cal1.getTime());
        //String currentTime = DateFormat.getTimeInstance().format(dateFormat);
        temperatures.add(new SaveModel(year.format(new Date()),month.format(new Date()),date.format(new Date()),temp,currentTime,location));
    }

    static Float getLastTemp(){
        SaveModel pair = temperatures.get(temperatures.size()-1);
         return Float.parseFloat(pair.getTemperature());
    }
    static String[] getAllSecond(){
        ArrayList <String> sa = new ArrayList<>();
        for (SaveModel pair: temperatures) {
          sa.add(pair.getSeconds());
        }
        return sa.toArray(new String[sa.size()]);
    }

    /**
     * ****** Save to file *******
     * @param context ta periexomena tis klashs MainActivity.
     */
    static void SaveToFile(Context context){
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
    static ArrayList<SaveModel> ReadFromFile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String stringFromFile = sharedPreferences.getString("Graph data",null);
        Type type = new TypeToken<ArrayList<SaveModel>>(){}.getType();
        ArrayList<SaveModel> saveModelForLoadFile = gson.fromJson(stringFromFile, type);
       /* ArrayList<SaveModel> saveModelForLoadFile = new ArrayList<>();
        SaveModel x =new SaveModel("2018","03","07","20.1","3:14:12","Kilkis");
        SaveModel z =new SaveModel("2018","03","07","19.5","3:15:12","Kilkis");
        SaveModel t =new SaveModel("2018","03","07","13.7","3:16:12","Kilkis");
        SaveModel y =new SaveModel("2017","02","015","19.3","3:14:12","Kilkis");
        SaveModel y1 =new SaveModel("2017","02","015","12.3","3:15:12","Kilkis");
        SaveModel y2 =new SaveModel("2017","02","015","26.1","3:16:12","Kilkis");
        SaveModel y3 =new SaveModel("2017","03","015","19.9","3:14:12","Kilkis");
        SaveModel y4 =new SaveModel("2017","03","015","21.16","3:15:12","Kilkis");
        SaveModel y14 =new SaveModel("2017","03","015","21.20","3:15:22","Kilkis");
        SaveModel y15 =new SaveModel("2017","03","015","21.32","3:15:32","Kilkis");
        SaveModel y6 =new SaveModel("2017","12","18","15.6","3:15:29","Tripoli");
        SaveModel y7 =new SaveModel("2017","12","18","18.3","3:12:13","Tripoli");
        SaveModel y8 =new SaveModel("2017","12","18","19.9","3:25:22","Tripoli");
        SaveModel y9 =new SaveModel("2017","12","18","21.03","3:19:25","Tripoli");
        SaveModel y10 =new SaveModel("2017","12","18","20.00","3:15:12","Tripoli");
        SaveModel y11 =new SaveModel("2017","03","015","25.32","3:16:12","Larisa");
        SaveModel y12 =new SaveModel("2017","03","015","25.36","3:16:23","Larisa");
        SaveModel y13 =new SaveModel("2017","03","015","25.39","3:16:33","Larisa");
        SaveModel y5 =new SaveModel("2017","03","015","22.21","3:16:53","Larisa");

        saveModelForLoadFile.add(x);
        saveModelForLoadFile.add(y);
        saveModelForLoadFile.add(z);
        saveModelForLoadFile.add(t);
        saveModelForLoadFile.add(y1);
        saveModelForLoadFile.add(y2);
        saveModelForLoadFile.add(y3);
        saveModelForLoadFile.add(y4);
        saveModelForLoadFile.add(y5);
        saveModelForLoadFile.add(y6);
        saveModelForLoadFile.add(y7);
        saveModelForLoadFile.add(y8);
        saveModelForLoadFile.add(y9);
        saveModelForLoadFile.add(y10);
        saveModelForLoadFile.add(y11);
        saveModelForLoadFile.add(y12);
        saveModelForLoadFile.add(y13);
        saveModelForLoadFile.add(y14);
        saveModelForLoadFile.add(y15);*/
        if(saveModelForLoadFile == null){
            saveModelForLoadFile = new ArrayList<>();
        }
        return saveModelForLoadFile;
    }

    static void deleteAllDataTemperatures(){
        temperatures.clear();
    }

}
