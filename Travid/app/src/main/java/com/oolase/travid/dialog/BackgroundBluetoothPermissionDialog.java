package com.oolase.travid.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.oolase.travid.R;
import com.oolase.travid.activity.NewsActivity;
import com.oolase.travid.utility.SignOut;
import com.oolase.travid.utility.Util;

/* Dialog that guides user to enable background locaation */

public class BackgroundBluetoothPermissionDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setMessage(R.string.backgroundPermissionExplanation)
                .setPositiveButton(R.string.dialog_allow_background_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to settings
                        dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, Util.RETURN_FROM_SETTINGS_PERMISSION_REQ);
                    }
                })
                .setNegativeButton(R.string.dialog_allow_background_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                        ((NewsActivity) getActivity()).stopBluetoothSearch();
//                        SignOut.signOut((NewsActivity) getActivity());
                    }
                });
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
