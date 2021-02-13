package com.smart_learn.core.services

import com.smart_learn.core.general.ActivityServiceUtilities

/**
 * This class should act as a container class for the activityServices classes through which
 * this classes will have centralized access to all non-activity services
 * from the entire program.
 *
 */
class ApplicationService(private val activityServiceUtilities: ActivityServiceUtilities<*, *>) {

    var dictionaryService: DictionaryService = DictionaryService(activityServiceUtilities)

}