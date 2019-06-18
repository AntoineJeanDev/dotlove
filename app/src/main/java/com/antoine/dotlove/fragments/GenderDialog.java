package com.antoine.dotlove.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.dotlove.R;
import com.antoine.dotlove.activities.MainActivity;

import java.util.ArrayList;


public class GenderDialog extends DialogFragment {

    public interface NoticeDialogListener {
        void onGenderChose(String gender);
    }

    NoticeDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quel genre ?")
                .setItems(R.array.genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gender;

                        if (which == 0) {
                            gender = "male";
                        } else {
                            gender = "female";
                        }

                        listener.onGenderChose(gender);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + "must implements NoticeDialogListener");
        }
    }
}
