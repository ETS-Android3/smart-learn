package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.core.services.ApplicationServiceK

/** every activity service should implement this interface */
interface ActivityViewModelUtilitiesK <T, X> {
    fun getActivity() : Activity
    fun getActivityService() : T
    fun getParentActivity() : X
    fun getApplicationService() : ApplicationServiceK
}
