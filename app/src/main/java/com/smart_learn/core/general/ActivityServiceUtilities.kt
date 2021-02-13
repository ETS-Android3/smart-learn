package com.smart_learn.core.general

import android.app.Activity
import com.smart_learn.core.services.ApplicationService

/** every activity service should implement this interface */
interface ActivityServiceUtilities <T, X> {
    fun getActivity() : Activity
    fun getActivityService() : T
    fun getParentActivity() : X
    fun getApplicationService() : ApplicationService
}
