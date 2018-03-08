package com.example.gioti.temperaturemonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gioti on 6/3/2018.d
 */

class ShowMaterialDialog {
    private static CharSequence[] selectedMeasurements ={};
    private static CharSequence addressLocation, year1, month1, date1;
    private ArrayList<ArrayList<SaveModel>> data = new ArrayList<>();
    void ManageOpenFile(final Context context, final LineChart chart, final ManageChart mChart) {
        ArrayList<String> address = new ArrayList<>();
        addressLocation = null;
        year1 = null;
        month1 = null;
        date1 = null;
        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
            address.add(pair.getLocation());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(address);
        address.clear();
        address.addAll(hs);

        new MaterialDialog.Builder(context)
                .title("Επιλέξτε την τοποθεσία που πραγματοποιήθηκε η μέτρηση θερμοκρασίας!!!")
                .items(address)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if(text==null){
                            alertDialogForNoDataCanLoad(context);
                            return false;
                        }
                        addressLocation = text;
                        final ArrayList<String> year = new ArrayList<>();
                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                            if (pair.getLocation().equals(addressLocation.toString())) {
                                year.add(pair.getYear());
                            }
                        }
                        if(year.size()<0){
                            return false;
                        }
                        Set<String> hs = new HashSet<>();
                        hs.addAll(year);
                        year.clear();
                        year.addAll(hs);
                        Collections.sort(year, new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // TODO Auto-generated method stub
                                return obj1.compareToIgnoreCase(obj2);
                            }
                        });
                        new MaterialDialog.Builder(context)
                                .title("Επιλέξτε το έτος που πραγματοποιήθηκε η μέτρηση!!!")
                                .items(year)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        if(text==null){
                                            return false;
                                        }
                                        year1 = text;
                                        ArrayList<String> month = new ArrayList<>();
                                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                                            if ((pair.getLocation().equals(addressLocation.toString()) && (pair.getYear().equals(year1.toString())))) {
                                                month.add(pair.getMonth());
                                            }
                                        }

                                        Set<String> hs = new HashSet<>();
                                        hs.addAll(month);
                                        month.clear();
                                        month.addAll(hs);
                                        new MaterialDialog.Builder(context)
                                                .title("Επιλέξτε το μήνα που πραγματοποιήθηκε η μέτρηση")
                                                .items(month)
                                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                                    @Override
                                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                        if(text==null){
                                                            return false;
                                                        }
                                                        month1 = text;

                                                        Log.d("msgDAta_Size",Integer.toString(data.size()));
                                                        final ArrayList<String> date = new ArrayList<>();
                                                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                                                            if ((pair.getLocation().equals(addressLocation.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString()))) {
                                                                //elegxos gia na min mporoume na anoiksoume sto grafima tin idia mera.
                                                                if(data.size()>0){//elegxoume an exoume anoixth kapoia metrish sto grafima. an den exoume anoixti metrish den xreiazete na ginei h sugkrish.
                                                                    for(ArrayList<SaveModel> pair2: data){
                                                                        Log.d("msg","\nprwti for\n\n\n");
                                                                        for(SaveModel pair3: pair2){
                                                                            Log.d("Msg",Boolean.toString((pair3.getDate().equals(pair.getDate())) && (pair3.getMonth().equals(pair.getMonth())) &&
                                                                                    (pair3.getYear().equals(pair.getYear())) && (pair3.getLocation().equals(pair.getLocation())) &&
                                                                                    (pair3.getSeconds().equals(pair.getSeconds())) &&
                                                                                    (pair3.getTemperature().equals(pair.getTemperature()))));
                                                                            if(!((pair3.getDate().equals(pair.getDate())) && (pair3.getMonth().equals(pair.getMonth())) &&
                                                                                    (pair3.getYear().equals(pair.getYear())) && (pair3.getLocation().equals(pair.getLocation())) &&
                                                                                    (pair3.getSeconds().equals(pair.getSeconds())) &&
                                                                                    (pair3.getTemperature().equals(pair.getTemperature())))) {
                                                                                date.add(pair.getDate());
                                                                            }else{
                                                                                alertDialogForSameDataLoad(context,month1);
                                                                                return false;
                                                                            }
                                                                        }
                                                                    }
                                                                }else{
                                                                    date.add(pair.getDate());
                                                                }

                                                            }
                                                        }
                                                        Set<String> hs = new HashSet<>();
                                                        hs.addAll(date);
                                                        date.clear();
                                                        date.addAll(hs);
                                                        Collections.sort(date, new Comparator<String>() {
                                                            public int compare(String obj1, String obj2) {
                                                                // TODO Auto-generated method stub
                                                                return obj1.compareToIgnoreCase(obj2);
                                                            }
                                                        });
                                                        new MaterialDialog.Builder(context)
                                                                .title("Επιλέξτε την ημέρα του μήνα " + month1.toString() + " που πραγματοποιήθηκε η μέτρηση")
                                                                .items(date)
                                                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                                                    @Override
                                                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                                        date1 = text;
                                                                        ArrayList<String> seconds = new ArrayList<>();
                                                                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                                                                            if ((pair.getLocation().equals(addressLocation.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString())) && (pair.getDate().equals(date1.toString()))) {
                                                                                if(selectedMeasurements!=null){
                                                                                    if(selectedMeasurements.length>0){
                                                                                        for(CharSequence pair2 : selectedMeasurements){
                                                                                            if(pair2.toString().equals(pair.getSeconds())){
                                                                                                seconds.add(pair.getSeconds());
                                                                                            }
                                                                                        }
                                                                                    }else{
                                                                                        seconds.add(pair.getSeconds());
                                                                                    }

                                                                                }else{
                                                                                    seconds.add(pair.getSeconds());
                                                                                }

                                                                            }
                                                                        }
                                                                        if(seconds.size()>0) {
                                                                            Collections.sort(seconds, new Comparator<String>() {
                                                                                public int compare(String obj1, String obj2) {
                                                                                    // TODO Auto-generated method stub
                                                                                    return obj1.compareToIgnoreCase(obj2);
                                                                                }
                                                                            });

                                                                            new MaterialDialog.Builder(context)
                                                                                    .title("Επιλέξτε τις μετρήσεις που θέλεται να εμφανίσεται για την ημέρα " + date1.toString() + " " + month1.toString() + "του έτους " + year1.toString())
                                                                                    .items(seconds)
                                                                                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                                                                                        @Override
                                                                                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                                                                            ArrayList<SaveModel> measurements = new ArrayList<>();
                                                                                            selectedMeasurements = text;
                                                                                            for (CharSequence selectedMeasurement : selectedMeasurements) {
                                                                                                Log.d("TAG", selectedMeasurement.toString());
                                                                                            }
                                                                                            for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                                                                                                if ((pair.getLocation().equals(addressLocation.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString())) && (pair.getDate().equals(date1.toString()))) {
                                                                                                    for (CharSequence pair2 : selectedMeasurements) {
                                                                                                        if (pair.getSeconds().equals(pair2.toString())) {
                                                                                                            measurements.add(pair);

                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            //Sorting student roll number Ascending Order
                                                                                            Collections.sort(measurements, new Comparator<SaveModel>() {
                                                                                                public int compare(SaveModel obj1, SaveModel obj2) {
                                                                                                    // TODO Auto-generated method stub
                                                                                                    return (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) < Integer.valueOf(obj2.getSeconds().replaceAll(":", ""))) ? -1 : (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) > Integer.valueOf(obj2.getSeconds().replaceAll(":", ""))) ? 1 : 0;
                                                                                                }
                                                                                            });
                                                                                            data.add(measurements);
                                                                                            if (selectedMeasurements.length > 0) {
                                                                                                mChart.refreshGraph(chart, measurements);
                                                                                            }
                                                                                            return true;
                                                                                        }
                                                                                    })
                                                                                    .positiveText(android.R.string.ok)
                                                                                    .negativeText(android.R.string.cancel)
                                                                                    .show();
                                                                            return true;
                                                                        }else{
                                                                            alertDialogForNoDataCanLoad(context);
                                                                            return false;
                                                                        }
                                                                    }
                                                                })
                                                                .positiveText(android.R.string.ok)
                                                                .negativeText(android.R.string.cancel)
                                                                .show();
                                                        return true;
                                                    }
                                                })
                                                .positiveText(android.R.string.ok)
                                                .negativeText(android.R.string.cancel)
                                                .show();

                                        return true;
                                    }
                                })
                                .positiveText(android.R.string.ok)
                                .negativeText(android.R.string.cancel)
                                .show();


                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }
    private void alertDialogForNoDataCanLoad(Context context){
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Δεν βρέθηκαν δεδομένα!!!")
                .setMessage("Προσοχή για να πραγματοποιηθεί η σύγκριση 2 ή περισσότερων μετρήσεων είναι αναγκαίο να έχουν πραγματοποιηθεί τις ίδιες χρονικές στιγμές στο παρελθόν")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    private void alertDialogForSameDataLoad(Context context, CharSequence month1){
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Δεν βρέθηκαν δεδομένα για άνοιγμα!!!")
                .setMessage("Όλες οι μέρες του μήνα " + month1 + " που επιλέξατε έχουν ήδη φορτωθεί στον γράφο."
                        + "\nΠαρακαλώ ξανά προσπαθήστε με διαφορετικά δεδομένα!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    void initializeData(){
        data.clear();
        selectedMeasurements=null;
    }

    static void mainMaterialDialog(final Menu mMenu, final Context context, final int switcher){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setTitle("Αποθήκευση των μετρήσεων")
                .setMessage("Θέλετε να γίνει αποθήκευση των μετρήσεων που έχουν ληφθεί μέχρι στιγμής;")
                .setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMenu.findItem(R.id.action_open_file).setEnabled(true);
                        FileManagement.SaveToFile(context);
                        if(switcher==2){
                            Intent i = new Intent(context, MapsActivity.class);
                            context.startActivity(i);    
                        }else if(switcher == 4){
                            Intent i = new Intent(context, OpenSaveCharts.class);
                            context.startActivity(i);
                        }else if(switcher ==3){
                            //do nothing because we are in the same class. we can delete this if but
                            // we are keep it for the better understanding the code
                        }
                        FileManagement.deleteAllDataTemperatures();
                    }

                })
                .setNegativeButton("ΌΧΙ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(switcher==2){
                            Intent i = new Intent(context, MapsActivity.class);
                            context.startActivity(i);
                        }else if(switcher == 4){
                            Intent i = new Intent(context, OpenSaveCharts.class);
                            context.startActivity(i);
                        }else if(switcher == 3){
                            //do nothing because we are in the same class. we can delete this if but
                            // we are keep it for the better understanding the code
                        }
                        FileManagement.deleteAllDataTemperatures();
                    }

                })
                .show();
    }
   static void aboutAsFunction(final Context context){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setCancelable(false)
                .setTitle("About As")
                .setMessage("Ομαδική πτυχιακή εργασία 2 ατόμων" +
                        "\nΣτοιχεία των ατόμων που συμμετείχαν: " +
                        "\nΣιακαμπέτη Ιωάννα ΑΜ: 202520100081 " +
                        "\nΚαλφόπουλος Παναγιώτης ΑΜ: 2025201100025" +
                        "\nΘέμα πτυχιακής: Συσκευή παρακολούθησης θερμοκρασίας από Android κινητό ή tablet")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}
