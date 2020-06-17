package com.oolase.travid.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.oolase.travid.R;
import com.oolase.travid.activity.NewsActivity;
import com.oolase.travid.utility.SignOut;
import com.oolase.travid.utility.UserRegisteration;

/* Dialog that contains a text field where the user can input his/her own MAC address */

public class UserBluetoothAddressDialog extends DialogFragment {
    EditText userMACAdress;
    UserRegisteration caller;
    OnAddressEntered callback;

    public UserBluetoothAddressDialog(UserRegisteration caller) {
        this.callback = (OnAddressEntered) caller;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setMessage(R.string.bluetooth_address_explanation)
                .setView(inflater.inflate(R.layout.bluetooth_address_form, null))
                .setPositiveButton(R.string.submit_address, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        userMACAdress = (EditText) ((AlertDialog) dialog).findViewById(R.id.mac_address);
                        // send address back to caller object
                        callback.getAddress(userMACAdress.getText().toString());

                    }
                })
                .setNegativeButton(R.string.cancel_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                        SignOut.signOut(getActivity());
                    }
                });
        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    // to communicate back to the calling class
    public interface OnAddressEntered{
        public void getAddress(String userMACAdress);

    }
}