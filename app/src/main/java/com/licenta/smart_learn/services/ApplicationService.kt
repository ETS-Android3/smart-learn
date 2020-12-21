package com.licenta.smart_learn.services

import com.licenta.smart_learn.general.ActivityServiceUtilities

/**
 * This class should act as a container class for the activityServices classes through which
 * this classes will have centralized access to all non-activity services
 * from the entire program.
 *
 */
class ApplicationService(private val activityServiceUtilities: ActivityServiceUtilities<*, *>) {

    var dictionaryService: DictionaryService = DictionaryService(activityServiceUtilities)

}