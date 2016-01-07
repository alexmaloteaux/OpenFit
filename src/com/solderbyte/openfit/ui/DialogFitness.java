package com.solderbyte.openfit.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.solderbyte.openfit.PedometerData;
import com.solderbyte.openfit.PedometerTotal;
import com.solderbyte.openfit.ProfileData;
import com.solderbyte.openfit.R;
import com.solderbyte.openfit.util.OpenFitData;
import com.solderbyte.openfit.util.OpenFitIntent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;

public class DialogFitness extends DialogFragment {
    private static final String LOG_TAG = "OpenFit:DialogFitness";

    private AlertDialog dialog = null;
    private Date trialDate = null;
    private ListAdapter adapter;
    private Context context;

    public DialogFitness(Context cntxt, ArrayList<PedometerData> pedometerDailyList, ArrayList<PedometerData> pedometerList, PedometerTotal pedometerTotal, ProfileData profileData) {
        context = cntxt;
        buildAdapter(pedometerDailyList, pedometerList, pedometerTotal, profileData);
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        trialDate = cal.getTime();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_fitness);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                Log.d(LOG_TAG, "Clicked: " + index);
            }
        });
        builder.setPositiveButton(R.string.dialog_close_fitness,  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {}
        });
        dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Sync onstart");
        if(dialog != null) {
            Button syncButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            if(syncButton != null) {
                Date now = new Date();
                Log.d(LOG_TAG, "trail: " + trialDate + " current: " + now);
                if(trialDate.getTime() < now.getTime()) {
                    syncButton.setEnabled(false);
                }
            }
        }
    }

    public void buildAdapter(ArrayList<PedometerData> pedometerDailyList, ArrayList<PedometerData> pedometerList, PedometerTotal pedometerTotal, ProfileData profileData) {
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<String> subitems = new ArrayList<String>();
        ArrayList<Drawable> iDraw = new ArrayList<Drawable>();
        Calendar cal = Calendar.getInstance();

        if(profileData != null) {
            String item = OpenFitData.getGender(profileData.getGender()) + ", " + profileData.getAge() + " years";
            String subitem = profileData.getHeight() + "cm, " + String.format(Locale.getDefault(), "%.2f", profileData.getWeight()) + "kg";
            Drawable icon = context.getResources().getDrawable(R.drawable.open_stand);
            icon.setBounds(0, 0, 144, 144);
            items.add(item);
            subitems.add(subitem);
            iDraw.add(icon);
        }

        if(pedometerTotal != null) {
            String steps = Integer.toString(pedometerTotal.getSteps());
            String distance = String.format(Locale.getDefault(), "%.2f", pedometerTotal.getDistance());
            String calories = String.format(Locale.getDefault(), "%.2f", pedometerTotal.getCalories());
            String item = "Total steps: " + steps;
            items.add(item);
            String subitem = distance + "m, " + calories + "kcal";
            subitems.add(subitem);

            Drawable icon = context.getResources().getDrawable(R.drawable.open_walk);
            icon.setBounds(0, 0, 144, 144);
            iDraw.add(icon);
        }

        for(int i = pedometerDailyList.size() - 1; i >= 0; i--) {
            Date date = new Date(pedometerDailyList.get(i).getTimeStamp());
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) - 1));
            String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            String year = Integer.toString(cal.get(Calendar.YEAR));

            String item = month + " " + day + ", " + year;
            items.add(item);

            String steps = Integer.toString(pedometerDailyList.get(i).getSteps());
            String distance = String.format(Locale.getDefault(), "%.2f", pedometerDailyList.get(i).getDistance());
            String calories = String.format(Locale.getDefault(), "%.2f", pedometerDailyList.get(i).getCalories());

            String subitem = "Steps: " + steps + ", " + distance + "m, " + calories + "kcal";
            subitems.add(subitem);

            Drawable icon = context.getResources().getDrawable(R.drawable.open_walk);
            icon.setBounds(0, 0, 144, 144);
            iDraw.add(icon);
        }

        if(items.size() <= 0) {
            String item = "No fitness data found";
            String subitem = "Enable pedometer on Gear Fit";
            items.add(item);
            subitems.add(subitem);

            Drawable icon = context.getResources().getDrawable(R.drawable.open_info);
            icon.setBounds(0, 0, 144, 144);
            iDraw.add(icon);
        }

        adapter = new ArrayAdapterFitness(context, items, subitems, iDraw);
    }
}
