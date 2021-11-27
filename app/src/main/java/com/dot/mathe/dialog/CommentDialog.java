package com.dot.mathe.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dot.mathe.R;
import com.google.android.material.textfield.TextInputLayout;

public class CommentDialog extends AppCompatDialogFragment {
    private final String previousText;
    private final String title;
    private CommentDialogListener listener;

    public CommentDialog(String previousText, String title) {
        this.previousText = previousText;
        this.title = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.comment_dialog, null);
        final TextInputLayout commentBox = view.findViewById(R.id.commentBox);
        final TextView titleTV = view.findViewById(R.id.commentTitle);
        titleTV.setText(title);
        commentBox.getEditText().setText(previousText);
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = commentBox.getEditText().getText().toString().trim();
                        listener.onSaveComment(text);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CommentDialogListener");
        }
    }

    public interface CommentDialogListener {
        void onSaveComment(String text);
    }
}
