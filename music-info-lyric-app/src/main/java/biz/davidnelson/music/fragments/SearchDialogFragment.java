package biz.davidnelson.music.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import biz.davidnelson.music.R;

public class SearchDialogFragment extends DialogFragment {

    public interface SearchDialogListener {
        void onSearchCriteriaEntered(String searchString);
    }

    SearchDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SearchDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement SearchDialogListener");
        }
    }

    @Override
    @SuppressLint("AndroidLintInflateParams") // dialogs need no parent
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_search_input, null);


        builder
            .setView(layout)
            .setTitle(R.string.search_dialog_title)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final EditText et = (EditText) layout.findViewById(R.id.et_search_input);

                    mListener.onSearchCriteriaEntered(et.getText().toString());


                    //  TODO: finish

                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
