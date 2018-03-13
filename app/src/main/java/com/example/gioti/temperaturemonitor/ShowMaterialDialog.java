package com.example.gioti.temperaturemonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gioti on 6/3/2018.d
 */

class ShowMaterialDialog {
    private static CharSequence[] selectedMeasurements ={};
    private static CharSequence addressLocation, year1, month1, date1;
    private static ArrayList<SaveModel> data1 = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    void ManageOpenFile(final Context context, final LineChart chart, final ManageChart mChart) {
        List <String> address = new ArrayList<>();
        addressLocation = null;
        year1 = null;
        month1 = null;
        date1 = null;
        List <SaveModel> result0 = new ArrayList<>();
        for (SaveModel pair : data1) {
            address.add(pair.getLocation());
            result0.add(pair);
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(address);
        address.clear();
        address.addAll(hs);
        if(address.size()==0){
            alertDialogForNoDataCanLoad(context);
        }else {
            Log.d("MSG", String.valueOf(address.size()));
            new MaterialDialog.Builder(context)
                    .title("Επιλέξτε την τοποθεσία που πραγματοποιήθηκε η μέτρηση θερμοκρασίας!!!")
                    .items(address)
                    .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                        if (text == null) {
                            alertDialogForNoDataCanLoad(context);
                            return false;
                        }
                        addressLocation = text;
                        final ArrayList<String> year = new ArrayList<>();
                        List<SaveModel> result1 = new ArrayList<>();
                        for (SaveModel pair : result0) {
                            if (pair.getLocation().contains(addressLocation)) {
                                result1.add(pair);
                                year.add(pair.getYear());
                            }
                        }
                        if (year.size() == 0) {
                            return false;
                        }
                        Set<String> hs13 = new HashSet<>();
                        hs13.addAll(year);
                        year.clear();
                        year.addAll(hs13);
                        // TODO Auto-generated method stub
                        Collections.sort(year, String::compareToIgnoreCase);
                        new MaterialDialog.Builder(context)
                                .title("Επιλέξτε το έτος που πραγματοποιήθηκε η μέτρηση!!!")
                                .items(year)
                                .itemsCallbackSingleChoice(-1, (dialog14, itemView13, which14, text14) -> {

                                    if (text14 == null) {
                                        return false;
                                    }

                                    year1 = text14;
                                    ArrayList<String> month = new ArrayList<>();
                                    List<SaveModel> result2 = new ArrayList<>();

                                    for (SaveModel pair : result1) {
                                        if (pair.getYear().contains(year1)) {
                                            result2.add(pair);
                                            month.add(pair.getMonth());
                                        }
                                    }

                                    Set<String> hs12 = new HashSet<>();
                                    hs12.addAll(month);
                                    month.clear();
                                    month.addAll(hs12);
                                    new MaterialDialog.Builder(context)
                                            .title("Επιλέξτε το μήνα που πραγματοποιήθηκε η μέτρηση")
                                            .items(month)
                                            .itemsCallbackSingleChoice(-1, (dialog13, itemView12, which13, text13) -> {

                                                if (text13 == null) {
                                                    return false;
                                                }

                                                month1 = text13;
                                                ArrayList<String> date = new ArrayList<>();
                                                List<SaveModel> result3 = new ArrayList<>();
                                                for (SaveModel pair : result2) {
                                                    if (pair.getMonth().contains(month1)) {
                                                        result3.add(pair);
                                                        date.add(pair.getDate());
                                                    }
                                                }
                                                if (date.size() == 0) {
                                                    return false;
                                                }
                                                Set<String> hs1 = new HashSet<>();
                                                hs1.addAll(date);
                                                date.clear();
                                                date.addAll(hs1);
                                                // TODO Auto-generated method stub
                                                Collections.sort(date, String::compareToIgnoreCase);
                                                new MaterialDialog.Builder(context)
                                                        .title("Επιλέξτε την ημέρα του μήνα " + month1.toString() + " που πραγματοποιήθηκε η μέτρηση")
                                                        .items(date)
                                                        .itemsCallbackSingleChoice(-1, (dialog12, itemView1, which12, text12) -> {

                                                            if (text12 == null) {
                                                                return false;
                                                            }

                                                            date1 = text12;
                                                            ArrayList<String> seconds = new ArrayList<>();
                                                            List<SaveModel> result4 = new ArrayList<>();
                                                            for (SaveModel pair : result3) {
                                                                if (pair.getDate().contains(date1)) {
                                                                    result4.add(pair);
                                                                    seconds.add(pair.getSeconds());
                                                                }
                                                            }
                                                            if (seconds.size() > 0) {

                                                                // TODO Auto-generated method stub
                                                                Collections.sort(seconds, String::compareToIgnoreCase);

                                                                new MaterialDialog.Builder(context)
                                                                        .title("Επιλέξτε τις μετρήσεις που θέλεται να εμφανίσεται για την ημέρα " + date1.toString() + " " + month1.toString() + "του έτους " + year1.toString())
                                                                        .items(seconds)
                                                                        .itemsCallbackMultiChoice(null, (dialog1, which1, text1) -> {
                                                                            if (text1 == null) {
                                                                                return false;
                                                                            }
                                                                            ArrayList<SaveModel> measurements = new ArrayList<>();
                                                                            selectedMeasurements = text1;
                                                                            for (SaveModel pair : result4) {
                                                                                for (CharSequence pair2 : selectedMeasurements) {
                                                                                    if (pair.getSeconds().equals(pair2.toString())) {
                                                                                        measurements.add(pair);
                                                                                        if (data1.contains(pair)) {
                                                                                            data1.remove(pair);//svinoume tis kataxwriseis pou prosthetoume ston grafw gia na min mporoume na tis ksana epileksoume.
                                                                                        }

                                                                                    }
                                                                                }
                                                                            }
                                                                            if (measurements.size() == 0) {
                                                                                return false;
                                                                            }
                                                                            //Sorting measurements roll time which received temperature
                                                                            Collections.sort(measurements, (obj1, obj2) -> {
                                                                                // TODO Auto-generated method stub
                                                                                return (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) < Integer.valueOf(obj2.getSeconds().replaceAll(":", "")))
                                                                                        ? -1 : (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) > Integer.valueOf(obj2.getSeconds().replaceAll(":", "")))
                                                                                        ? 1 : 0;
                                                                            });
                                                                            mChart.drawSaveCharts(chart, measurements);
                                                                            return true;

                                                                        })
                                                                        .positiveText(android.R.string.ok)
                                                                        .negativeText(android.R.string.cancel)
                                                                        .show();
                                                                return true;
                                                            } else {
                                                                alertDialogForNoDataCanLoad(context);
                                                                return false;
                                                            }
                                                        })
                                                        .positiveText(android.R.string.ok)
                                                        .negativeText(android.R.string.cancel)
                                                        .show();
                                                return true;
                                            })
                                            .positiveText(android.R.string.ok)
                                            .negativeText(android.R.string.cancel)
                                            .show();
                                    return true;
                                })
                                .positiveText(android.R.string.ok)
                                .negativeText(android.R.string.cancel)
                                .show();
                        return true;
                    })
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .show();
        }
    }
    private void alertDialogForNoDataCanLoad(Context context){
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Δεν βρέθηκαν δεδομένα!!!")
                .setMessage("Όλες οι μετρίσης έχουν τοποθετηθεί στο γράφημα. Επιλέξτε 'clear data' " +
                        "από το menu για να τοπεθετίσεται διαφορετικά set μετρήσεων")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                })
                .show();
    }

    void initializeData(Context context){
        selectedMeasurements=null;
        data1.clear();
        data1.addAll(FileManagement.ReadFromFile(context));
        if(data1!=null){
            Log.d("MSG","Den einai null");
        }else{
            Log.d("MSG", "Einai null");
        }
    }

    static void mainMaterialDialog(final Menu mMenu, final Context context, final int switcher){
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setTitle("Αποθήκευση των μετρήσεων")
                .setMessage("Θέλετε να γίνει αποθήκευση των μετρήσεων που έχουν ληφθεί μέχρι στιγμής;")
                .setPositiveButton("NAI", (dialog, which) -> {
                    mMenu.findItem(R.id.action_open_file).setEnabled(true);
                    FileManagement.SaveToFile(context);
                    Toast.makeText(context, "File saving is successful", Toast.LENGTH_LONG).show();
                    if(switcher==2){
                        //Intent i = new Intent(context, MapsActivity.class);
                        //context.startActivity(i);
                    }else if(switcher == 4){
                        Intent i = new Intent(context, OpenSaveCharts.class);
                        context.startActivity(i);
                    }else if(switcher ==3){
                        //do nothing because we are in the same class. we can delete this if but
                        // we are keep it for the better understanding the code
                    }
                    FileManagement.deleteAllDataTemperatures();
                })
                .setNegativeButton("ΌΧΙ", (dialog, which) -> {
                    if(switcher==2){
                        //Intent i = new Intent(context, MapsActivity.class);
                        //context.startActivity(i);
                    }else if(switcher == 4){
                        Intent i = new Intent(context, OpenSaveCharts.class);
                        context.startActivity(i);
                    }else if(switcher == 3){
                        //do nothing because we are in the same class. we can delete this if but
                        // we are keep it for the better understanding the code
                    }
                    FileManagement.deleteAllDataTemperatures();
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
                        "\nΣιακαμπέτη Ιωάννα " +
                        "\nΑΜ: 2025201000080 " +
                        "\nΚαλφόπουλος Παναγιώτης " +
                        "\nΑΜ: 2025201100025" +
                        "\nΘέμα πτυχιακής: " +
                        "\nΣυσκευή παρακολούθησης θερμοκρασίας από Android κινητό ή tablet")
                .setPositiveButton("OK", (dialog, which) -> {
                })
                .show();
    }

}
