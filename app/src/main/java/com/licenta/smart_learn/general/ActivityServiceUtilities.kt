package com.licenta.smart_learn.general

import android.app.Activity
import com.licenta.smart_learn.services.ApplicationService

/** every activity service should implement this interface */
interface ActivityServiceUtilities <T, X> {
    fun getActivity() : Activity
    fun getActivityService() : T
    fun getParentActivity() : X
    fun getApplicationService() : ApplicationService
}
