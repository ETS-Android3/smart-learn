package com.smart_learn.presenter.helpers.fragments.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smart_learn.databinding.FragmentWebViewBinding;

import org.jetbrains.annotations.NotNull;

public class WebViewFragment extends Fragment {

    protected FragmentWebViewBinding binding;
    public static final String DEFAULT_URL = "https://www.google.com";
    public static final String URL_KEY = "url";

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentWebViewBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Use this line in order to avoid fragment to open a browser.
        // https://stackoverflow.com/questions/7746409/android-webview-launches-browser-when-calling-loadurl/12802173#12802173
        binding.webViewFragmentWebView.setWebViewClient(new WebViewClient());

        // try to get url and load page
        if(getArguments() != null) {
            // https://developer.android.com/guide/navigation/navigation-pass-data#java
            binding.webViewFragmentWebView.loadUrl(getArguments().getString(URL_KEY));
        }
        else{
            binding.webViewFragmentWebView.loadUrl(DEFAULT_URL);
        }
    }
}