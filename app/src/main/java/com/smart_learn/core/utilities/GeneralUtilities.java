package com.smart_learn.core.utilities;

import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;
import com.smart_learn.R;

public interface GeneralUtilities {

    static void showShortToastMessage(final android.content.Context context, final String message){
        // https://www.youtube.com/watch?v=fq8TDVqpmZ0
        // https://github.com/Muddz/StyleableToast
        StyleableToast.makeText(context, message, Toast.LENGTH_LONG, R.style.AppTheme_CustomToast).show();
    }

}

