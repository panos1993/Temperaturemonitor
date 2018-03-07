package com.example.gioti.temperaturemonitor;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gioti on 6/3/2018.
 */

public class ShowMaterialDialog {
    private static CharSequence[] selectedMeasurements;
    private static CharSequence addressLocation, year1, month1, date1;
    private ArrayList<SaveModel> measurements = new ArrayList<>();
    private ArrayList<ArrayList<SaveModel>> data = new ArrayList<>();
    void ManageOpenFile(final Context context, final LineChart chart, final ManageChart mChart) {
        ArrayList<String> address = new ArrayList<>();
        measurements.clear();
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
                        addressLocation = text;
                        final ArrayList<String> year = new ArrayList<>();
                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                            if (pair.getLocation().equals(addressLocation.toString())) {
                                year.add(pair.getYear());
                            }
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
                                                        month1 = text;
                                                        ArrayList<String> date = new ArrayList<>();
                                                        for (SaveModel pair : FileManagement.ReadFromFile(context)) {
                                                            if ((pair.getLocation().equals(addressLocation.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString()))) {
                                                                date.add(pair.getDate());
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
                                                                                seconds.add(pair.getSeconds());
                                                                            }
                                                                        }
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
                                                                                        if(selectedMeasurements.length>0){
                                                                                            mChart.refreshGraph(chart,measurements);
                                                                                        }
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
}
