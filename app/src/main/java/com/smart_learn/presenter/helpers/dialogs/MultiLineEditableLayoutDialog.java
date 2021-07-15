package com.smart_learn.presenter.helpers.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.databinding.LayoutDialogMultilineEditableLayoutBinding;

import org.jetbrains.annotations.NotNull;


public class MultiLineEditableLayoutDialog extends DialogFragment {

    private final MultiLineEditableLayoutDialog.Callback callback;
    private final String title;
    private final String previousValue;
    private final MutableLiveData<String> liveValue;
    private final int maximLength;

    public MultiLineEditableLayoutDialog(String title, String value, int maximLength,
                                          @NonNull @NotNull MultiLineEditableLayoutDialog.Callback callback) {
        this.callback = callback;
        this.title = title == null ? "" : title;
        this.previousValue = value == null ? "" : value;
        this.liveValue = new MutableLiveData<>(this.previousValue);
        this.maximLength = maximLength;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogMultilineEditableLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_multiline_editable_layout,null, false);
        binding.setLifecycleOwner(this);

        // set dialog values
        binding.setLiveValue(liveValue);
        binding.setCounterMaxLength(maximLength);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setTitle(title)
                .setView(binding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // add action buttons

                // Set to null. We override the onclick.
                .setPositiveButton(R.string.update, null)

                // No need for a listener because I do no action when BUTTON_NEGATIVE is pressed.
                // Dialog will be dismissed automatically.
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();

        // Override onclick for positive button to turn off auto dismiss when BUTTON_POSITIVE is clicked.
        // Check this to see we is need an override.
        // https://www.xspdf.com/help/50319382.html
        // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked/10661281#10661281
        // https://stackoverflow.com/questions/6142308/android-dialog-keep-dialog-open-when-button-is-pressed/6142413#6142413
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onUpdate(previousValue, liveValue.getValue(), binding.textInputLayoutDialogMultilineEditableLayout, new Listener() {
                            @Override
                            public void onSuccessCheck() {
                                MultiLineEditableLayoutDialog.this.dismiss();
                            }
                        });
                    }
                });
            }
        });

        return dialog;
    }

    public interface Callback {
        void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout, @NonNull @NotNull Listener listener);
    }

    public interface Listener {
        void onSuccessCheck();
    }

}