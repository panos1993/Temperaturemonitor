package com.example.gioti.temperaturemonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
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
 * this class is used for showing material and alert dialogs only
 */

class ShowMaterialDialog {
    static final int MESSAGE_KILL_MAIN_ACTIVITY = 1;
    static final int MESSAGE_GO_OPEN_SAVE_FILE_ACTIVITY=2;
    private static ArrayList<SaveModel> data1 = new ArrayList<>();

    /**
     * This function is called by OpenSaveChart class.
     * It shows appropriate dialogs to the user and he chooses which measurements will be appeared on the chart
     * @param context   // the context of the class which called this function
     * @param chart     // the chart which we created in class OpenSaveCharts
     * @param mChart    // is an object of class ManageChart
     */
    void ManageOpenAndDeleteFile(final Context context, final LineChart chart, final ManageChart mChart,boolean isForOpenMeasurementInChart) {
        List <String> selectedItems = new ArrayList<>();

        //we are using 4 arrayLists
        ArrayList <SaveModel> result0 = new ArrayList<>();       //includes all measurements which exist in the saved file
        ArrayList<SaveModel> result1 = new ArrayList<>();
        for (SaveModel pair : data1) {
            selectedItems.add(pair.getLocation());
        }
        Set<String> deleteDuplicates = new HashSet<>();
        deleteDuplicates.addAll(selectedItems);
        selectedItems.clear();
        selectedItems.addAll(deleteDuplicates);
        deleteDuplicates.clear();
        if(selectedItems.size()==0){
            alertDialogForNoDataCanLoad(context);
        }else {
            new MaterialDialog.Builder(context)
                    .title("Επιλέξτε την τοποθεσία που πραγματοποιήθηκε η μέτρηση θερμοκρασίας!!!")
                    .items(selectedItems)
                    .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                        if (text == null) {
                            alertDialogForNoDataCanLoad(context);
                            return false;
                        }

                        selectedItems.clear();
                        for (SaveModel pair : data1) {
                            if (pair.getLocation().contains(text)) {
                                result0.add(pair);
                                selectedItems.add(pair.getYear());
                            }
                        }
                        if (selectedItems.size() == 0) {
                            return false;
                        }
                        Set<String> hs13 = new HashSet<>();
                        deleteDuplicates.addAll(selectedItems);
                        selectedItems.clear();
                        selectedItems.addAll(deleteDuplicates);
                        deleteDuplicates.clear();
                        // TODO Auto-generated method stub
                        Collections.sort(selectedItems, String::compareToIgnoreCase);
                        new MaterialDialog.Builder(context)
                                .title("Επιλέξτε το έτος που πραγματοποιήθηκε η μέτρηση!!!")
                                .items(selectedItems)
                                .itemsCallbackSingleChoice(-1, (dialog14, itemView13, which14, text14) -> {

                                    if (text14 == null) {
                                        return false;
                                    }

                                    selectedItems.clear();
                                    for (SaveModel pair : result0) {
                                        if (pair.getYear().contains(text14)) {
                                            result1.add(pair);
                                            selectedItems.add(pair.getMonth());
                                        }
                                    }

                                    result0.clear();
                                    deleteDuplicates.addAll(selectedItems);
                                    selectedItems.clear();
                                    selectedItems.addAll(deleteDuplicates);
                                    deleteDuplicates.clear();

                                    new MaterialDialog.Builder(context)
                                            .title("Επιλέξτε το μήνα που πραγματοποιήθηκε η μέτρηση")
                                            .items(selectedItems)
                                            .itemsCallbackSingleChoice(-1, (dialog13, itemView12, which13, text13) -> {

                                                if (text13 == null) {
                                                    return false;
                                                }

                                                selectedItems.clear();
                                                for (SaveModel pair : result1) {
                                                    if (pair.getMonth().contains(text13)) {
                                                        result0.add(pair);
                                                        selectedItems.add(pair.getDate());
                                                    }
                                                }
                                                result1.clear();
                                                if (selectedItems.size() == 0) {
                                                    return false;
                                                }


                                                deleteDuplicates.addAll(selectedItems);
                                                selectedItems.clear();
                                                selectedItems.addAll(deleteDuplicates);
                                                deleteDuplicates.clear();
                                                // TODO Auto-generated method stub
                                                Collections.sort(selectedItems, String::compareToIgnoreCase);
                                                new MaterialDialog.Builder(context)
                                                        .title("Επιλέξτε την ημέρα του μήνα " + text13.toString() + " που πραγματοποιήθηκε η μέτρηση")
                                                        .items(selectedItems)
                                                        .itemsCallbackSingleChoice(-1, (dialog12, itemView1, which12, text12) -> {

                                                            if (text12 == null) {
                                                                return false;
                                                            }
                                                            selectedItems.clear();
                                                            for (SaveModel pair : result0) {
                                                                if (pair.getDate().contains(text12)) {
                                                                    result1.add(pair);
                                                                    selectedItems.add(pair.getSeconds());
                                                                }
                                                            }
                                                            if (selectedItems.size() > 0) {

                                                                // TODO Auto-generated method stub
                                                                Collections.sort(selectedItems, String::compareToIgnoreCase);
                                                                String message;
                                                                if(isForOpenMeasurementInChart){
                                                                    message = "Επιλέξτε τις μετρήσεις που θέλεται να εμφανίσεται για την ημέρα ";
                                                                }else{
                                                                    message = "Επιλέξτε τις μετρήσεις που θέλεται να διαγράψετε για την ημέρα ";
                                                                }
                                                                new MaterialDialog.Builder(context)
                                                                        .title(message + text12.toString() + " του μήνα " + text13.toString() + " του έτους " + text14.toString())
                                                                        .items(selectedItems)
                                                                        .itemsCallbackMultiChoice(null, (dialog1, which1, text1) -> {
                                                                            if (text1 == null) {
                                                                                return false;
                                                                            }
                                                                            result0.clear();
                                                                            for (SaveModel pair : result1) {
                                                                                for (CharSequence pair2 : text1) {
                                                                                    if (pair.getSeconds().equals(pair2.toString())) {
                                                                                        result0.add(pair);
                                                                                        if (data1.contains(pair)) {
                                                                                            data1.remove(pair);//svinoume tis kataxwriseis pou prosthetoume ston grafw gia na min mporoume na tis ksana epileksoume.
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                            if (result0.size() == 0) {
                                                                                return false;
                                                                            }
                                                                            //Sorting measurements roll time which received temperature
                                                                            Collections.sort(result0, (obj1, obj2) -> {
                                                                                // TODO Auto-generated method stub
                                                                                return (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) < Integer.valueOf(obj2.getSeconds().replaceAll(":", "")))
                                                                                        ? -1 : (Integer.valueOf(obj1.getSeconds().replaceAll(":", "")) > Integer.valueOf(obj2.getSeconds().replaceAll(":", "")))
                                                                                        ? 1 : 0;
                                                                            });
                                                                            if(isForOpenMeasurementInChart){
                                                                                mChart.drawSaveCharts(chart, result0);
                                                                                return true;
                                                                            }else{
                                                                               FileManagement.deleteFromFile(context,result0);
                                                                                return true;
                                                                            }


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

    /**
     * Is called when there are no data for saving
     * @param context The context from the class which want to appear this Alert dialog.
     */
    private void alertDialogForNoDataCanLoad(Context context){
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("No data found!!!")
                .setMessage("Όλες οι μετρήσης έχουν τοποθετηθεί στο γράφημα. Επιλέξτε 'clear data' " +
                        "από το menu για να τοπεθετίσετε διαφορετικά set μετρήσεων")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                })
                .show();
    }

    //initializing lists' data
    void initializeData(Context context){
        data1.clear();
        data1.addAll(FileManagement.ReadFromFile(context));
    }

    /**
     * is called by MainActivity class
     * @param mMenu         // the main menu
     * @param context       // the context of class MainActivity
     * @param switcher //where we want to go (MapsActivity or OpenSaveCharts)
     */

    static void mainMaterialDialog(final Menu mMenu, final Context context, final int switcher,final Handler handler){

        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setTitle("Save measurements")
                .setMessage("Do you want to save the current measurements?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mMenu.findItem(R.id.action_manage_file).setEnabled(true);
                    FileManagement.SaveToFile(context);
                    Toast.makeText(context, "The file has been saved successfully", Toast.LENGTH_LONG).show();
                    FileManagement.deleteAllDataTemperatures();
                    if(switcher==2){

                        handler.obtainMessage(MESSAGE_KILL_MAIN_ACTIVITY).sendToTarget();

                    }else if(switcher == 4){

                        handler.obtainMessage(MESSAGE_GO_OPEN_SAVE_FILE_ACTIVITY).sendToTarget();

                    }else if(switcher ==3){
                        //do nothing because we are in the same class. we can delete this if but
                        // we are keep it for the better understanding the code
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {
                    if(switcher==2){

                        handler.obtainMessage(MESSAGE_KILL_MAIN_ACTIVITY).sendToTarget();

                    }else if(switcher == 4){

                        handler.obtainMessage(MESSAGE_GO_OPEN_SAVE_FILE_ACTIVITY).sendToTarget();

                    }else if(switcher == 3){
                        //do nothing because we are in the same class. we can delete this if but
                        // we are keep it for the better understanding the code
                    }
                })
                .show();
    }

    /**
     * some informations about us
     *
     */
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
