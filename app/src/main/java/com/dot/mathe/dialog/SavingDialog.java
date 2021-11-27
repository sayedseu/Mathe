package com.dot.mathe.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dot.mathe.R;

import java.io.File;

public class SavingDialog extends AppCompatDialogFragment {
    private final String path;
    private SavingDialogListener listener;
    private RadioGroup radioGroup;

    public SavingDialog(String path) {
        this.path = path;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.saving_dialog, null);
        final EditText fileNameET = view.findViewById(R.id.fileNameET);
        radioGroup = view.findViewById(R.id.radioGroup);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String type = ".xlsx";
                        boolean isXlsx = true;
                        int id = radioGroup.getCheckedRadioButtonId();
                        switch (id) {
                            case -1:
                            case R.id.xlsx:
                                isXlsx = true;
                                type = ".xlsx";
                                break;
                            case R.id.pdf:
                                isXlsx = false;
                                type = ".pdf";
                                break;
                        }
                        File file = new File(path, fileNameET.getText().toString() + type);
                        listener.onSave(file, isXlsx);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SavingDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement SavingDialogListener");
        }
    }

    public interface SavingDialogListener {
        void onSave(File file, boolean isXlsx);
    }
}
