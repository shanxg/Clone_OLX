package com.lucasrivaldo.cloneolx.helper;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.lucasrivaldo.cloneolx.R;

import dmax.dialog.SpotsDialog;

public class AlertDialogUtil {

    public static void openDetailsAlert(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Instructions :");
        builder.setMessage("When you are interested in an announcement, " +
                "perform a long click on it to open the announcement details.\n" +
                "And double-click to");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {});

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void permissionValidationAlert(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Permissions denied:");
        builder.setMessage("To keep using the App, you need to accept the Requested permissions.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm",
                (dialog, which) -> SystemPermissions.validatePermissions(activity, 1));
        builder.setNegativeButton("Deny", (dialogInterface, i) -> activity.finish());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static android.app.AlertDialog progressDialogAlert(Activity activity){

        android.app.AlertDialog progressDialog =
                new SpotsDialog.Builder()
                        .setContext(activity)
                        .setCancelable(false)
                        .setMessage("Uploading announcement")
                        .build();

        return progressDialog;
    }

    public static void filterTypeDialog(Activity activity, boolean isForRegions,
                                        ReturnFilterData returnOnClick){

        View viewSpinner = activity.getLayoutInflater().inflate(R.layout.layout_spinner, null);
        Spinner spinner = viewSpinner.findViewById(R.id.spinnerFilter);
        String type;

        if (isForRegions) {
            String[] regions = activity.getResources().getStringArray(R.array.regions);
            type = regions[0];

            ArrayAdapter<String> adapterRegions =
                    new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, regions);
            adapterRegions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterRegions);
        }else {
            String[] categories = activity.getResources().getStringArray(R.array.categories);
            type = categories[0];

            ArrayAdapter<String> adapterCategories =
                    new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, categories);
            adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterCategories);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select a "+type+" to filter:");
        builder.setView(viewSpinner);
        builder.setPositiveButton("Filter", (dialogInterface, i) ->
                returnOnClick.getFilterData(spinner.getSelectedItem().toString()));

        builder.setNegativeButton("Cancel", (dialogInterface, i) ->{});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface ReturnFilterData {
        void getFilterData(String type);
    }
}