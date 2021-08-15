package com.smart_learn.presenter.activities.settings.fragments.basic_settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.databinding.FragmentBasicSettingsBinding;
import com.smart_learn.presenter.activities.settings.SettingsActivity;
import com.smart_learn.presenter.helpers.PresenterCallbacks;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;


public class BasicSettingsFragment extends BasicFragment<BasicSettingsViewModel> {

    protected FragmentBasicSettingsBinding binding;

    @NonNull
    @Override
    protected @NotNull Class<BasicSettingsViewModel> getModelClassForViewModel() {
        return BasicSettingsViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentBasicSettingsBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){
        // https://developer.android.com/guide/topics/ui/controls/spinner
        // https://mkyong.com/android/android-spinner-drop-down-list-example/
        // https://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
        // https://www.youtube.com/watch?v=741l_fPKL3Y&ab_channel=Stevdza-San
        // https://www.youtube.com/watch?v=zILw5eV9QBQ&ab_channel=AtifPervaiz
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLanguagesFragmentBasicSettings.setAdapter(adapter);

        // set initial selection
        binding.spinnerLanguagesFragmentBasicSettings.setSelection(SettingsService.getInstance().getLanguageOption());

        binding.spinnerLanguagesFragmentBasicSettings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    setLanguage(SettingsService.Languages.Options.DEFAULT);
                    return;
                }
                if(position == 1){
                    setLanguage(SettingsService.Languages.Options.ROMANIAN);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no action needed here
            }
        });
    }

    private void setLanguage(int newLanguageOption){
        int currentLanguageOption = SettingsService.getInstance().getLanguageOption();
        if(currentLanguageOption == newLanguageOption){
            return;
        }

        PresenterUtilities.Activities.showStandardAlertDialog(requireContext(), getString(R.string.change_language),
                getString(R.string.change_language_alert), getString(android.R.string.ok), new PresenterCallbacks.StandardAlertDialogCallback() {
                        @Override
                        public void onPositiveButtonPress() {
                            // set new language
                            SettingsService.getInstance().saveLanguageOption(newLanguageOption);
                            SettingsService.getInstance().loadLanguageConfiguration(requireActivity().getBaseContext());
                            // go back in order to apply settings
                            ((SettingsActivity)requireActivity()).goToStartActivity();
                        }

                    @Override
                    public void onNegativeButtonPress() {
                        // reset selection
                        binding.spinnerLanguagesFragmentBasicSettings.setSelection(currentLanguageOption);
                    }
                });
    }

}