package com.popmovies.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.popmovies.R;

public class FilterDialog extends DialogFragment {
    public static final int POPULAR_INDEX = 0;
    public static final int TOP_RATED_INDEX = 1;

    private OptionSelectListener mOptionSelectListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OptionSelectListener) {
            mOptionSelectListener = (OptionSelectListener) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OptionSelectListener) {
            mOptionSelectListener = (OptionSelectListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fileter_dialog_title)
                .setItems(R.array.filter_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOptionSelectListener != null)
                            mOptionSelectListener.onOptionSelected(which);
                    }
                }).create();
    }

    public interface OptionSelectListener {
        void onOptionSelected(int index);
    }

}
