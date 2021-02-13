package com.smart_learn.core.general

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.smart_learn.R
import com.smart_learn.data.entities.LessonDetailsK
import com.smart_learn.data.entities.LessonEntranceK
import com.smart_learn.presenter.recycler_view.adapters.LessonRVAdapterK
import com.smart_learn.presenter.recycler_view.adapters.EntrancesRVAdapterK
import com.smart_learn.data.repository.DatabaseSchemaK
import com.smart_learn.core.services.ApplicationServiceK
import kotlinx.android.synthetic.main.dialog_add_word.*
import kotlinx.android.synthetic.main.dialog_new_lesson.*


fun showSettingsDialog(activity: Activity){
    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(R.layout.dialog_settings)

    dialog.show()
}



private fun lessonDetailsCheck(
    activity: Activity,
    applicationServiceK: ApplicationServiceK,
    lessonName: String
): Boolean{

    if (lessonName.isEmpty()) {
        Toast.makeText(activity, "Enter a name", Toast.LENGTH_LONG).show()
        return false
    }

    // check lessonName length
    if (lessonName.length > DatabaseSchemaK.LessonsTable.DIMENSION_COLUMN_NAME) {
        Toast.makeText( activity, "This name is too big. Choose a shorter name.",
            Toast.LENGTH_LONG).show()
        return false
    }

    //add lesson only if this does not exist
    if (applicationServiceK.lessonServiceK.checkIfLessonExist(lessonName)) {
        Toast.makeText(
            activity, "Lesson $lessonName already exists. Choose other name",
            Toast.LENGTH_LONG).show()
        return false
    }

    return true

}


fun showAddLessonDialog(
    TAG: String,
    activity: Activity,
    dialogLayout: Int?,
    applicationServiceK: ApplicationServiceK,
    updateLesson: Boolean = false,
    currentLessonK: LessonDetailsK? = null,
    updateRecyclerView: Boolean = false,
    lessonRVAdapter: LessonRVAdapterK? = null,
    position: Int = -1
){

    // check if this occurs
    if(updateLesson && currentLessonK == null){
        Log.e(UNEXPECTED_ERROR," current lesson is null in update listener for $TAG")
        return
    }

    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(dialogLayout)


    if(updateLesson && currentLessonK != null){
        // TODO: hide save button and remove space used by this
        dialog.btnSaveLesson.visibility = View.INVISIBLE
        dialog.btnUpdateLess.visibility = View.VISIBLE

        // get current lesson
        // set current data in dialog
        dialog.etLessonName.setText(currentLessonK.title)

        dialog.btnUpdateLess.setOnClickListener {

            // get inserted data
            val lessonName = dialog.etLessonName.text.toString().trim()

            // make some specific checks
            if(lessonName == currentLessonK.title){
                Toast.makeText(activity,"No modification was made",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // make some general check
            if(!lessonDetailsCheck(activity,applicationServiceK,lessonName)){
                return@setOnClickListener
            }

            // lesson name is valid
            currentLessonK.title = lessonName

            // update in database
            applicationServiceK.lessonServiceK.update(currentLessonK)

            // update item in recycler view
            if(updateRecyclerView && lessonRVAdapter != null) {
                lessonRVAdapter.updateItem(position, currentLessonK)
            }

            dialog.dismiss()
        }
    }
    else{

        // TODO: hide update button and remove space used by this
        dialog.btnSaveLesson.visibility = View.VISIBLE
        dialog.btnUpdateLess.visibility = View.INVISIBLE

        dialog.btnSaveLesson.setOnClickListener {

            val lessonName = dialog.etLessonName.text.toString().trim()

            // make some general checks
            if(!lessonDetailsCheck(activity,applicationServiceK,lessonName)){
                return@setOnClickListener
            }

            // lessonName is valid
            val newLessonK: LessonDetailsK = LessonDetailsK(title = lessonName)

            // add in database
            applicationServiceK.lessonServiceK.insert(lessonName)

            // update recycler view with lesson from database (need update for primary key)
            if(updateRecyclerView){
                val updatedLesson = applicationServiceK.lessonServiceK.getSampleLiveLesson(lessonName)
                if(updatedLesson != null && lessonRVAdapter != null) {
                    lessonRVAdapter.insertItem(updatedLesson)
                }
            }

            dialog.dismiss()
        }
    }

    dialog.btnCancelLesson.setOnClickListener{
        dialog.dismiss()
    }

    dialog.show()
}




private fun entryCheck(
    activity: Activity,
    applicationServiceK: ApplicationServiceK,
    word: String,
    translation: String,
    phonetic: String
): Boolean {

    if(word.isEmpty() && translation.isEmpty() && phonetic.isEmpty()){
        Toast.makeText(activity,
            "All fields are empty. You must enter value in at least one field",
            Toast.LENGTH_LONG).show()
        return false
    }

    if(word.length > DatabaseSchemaK.EntriesTable.DIMENSION_COLUMN_WORD){
        Toast.makeText(activity, "This word is too big.",Toast.LENGTH_LONG).show()
        return false
    }

    if(phonetic.length > DatabaseSchemaK.EntriesTable.DIMENSION_COLUMN_PHONETIC){
        Toast.makeText(activity, "This phonetic translation is too big.",
            Toast.LENGTH_LONG).show()
        return false
    }

    // TODO: check to see if word exist in all database not only in one lesson
    // add word only if this does not exists in current lesson
    if(word.isNotEmpty() &&
        applicationServiceK.lessonServiceK.checkIfWordExist(word,SELECTED_LESSON_ID)){

        Toast.makeText(activity, "Word $word already exists in this lesson.",
            Toast.LENGTH_LONG).show()
        return false
    }

    // TODO: check if translation exists in lesson

    return true

}


fun showAddEntranceDialog(
    TAG: String,
    activity: Activity,
    dialogLayout: Int?,
    applicationServiceK: ApplicationServiceK,
    updateEntrance: Boolean = false,
    entranceK: LessonEntranceK? = null,
    updateRecyclerView: Boolean = false,
    entranceRVAdapter: EntrancesRVAdapterK? = null,
    position: Int = -1
){

    // check if this occurs
    if(updateEntrance && entranceK == null){
        Log.e(UNEXPECTED_ERROR," current entranceK is null in update listener for $TAG")
        return
    }

    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(dialogLayout)

    if(updateEntrance && entranceK != null){

        // TODO: hide update button and remove space used by this
        dialog.btnSaveEntrance.visibility = View.INVISIBLE
        dialog.btnUpdateEntrance.visibility = View.VISIBLE

        // set current data on dialog elements
        dialog.etWord.setText(entranceK.word)
        dialog.etTranslation.setText(entranceK.translation)
        dialog.etPhonetic.setText(entranceK.phonetic)

        dialog.btnUpdateEntrance.setOnClickListener{

            val word = dialog.etWord.text.toString().trim()
            val translation = dialog.etTranslation.text.toString().trim()
            val phonetic = dialog.etPhonetic.text.toString().trim()

            // make some specific checks for fields data
            if(word == entranceK.word && translation == entranceK.translation && phonetic == entranceK.phonetic){
                Toast.makeText(activity,"No modification was made",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // make some general checks
            if(!entryCheck(activity,applicationServiceK,word,translation,phonetic)){
                return@setOnClickListener
            }

            // update current entranceK
            entranceK.word = word
            entranceK.translation = translation
            entranceK.phonetic = phonetic

            // update entranceK in database
            applicationServiceK.lessonServiceK.update(entranceK)

            // update item in recycler view
            if(updateRecyclerView && entranceRVAdapter != null) {
                entranceRVAdapter.updateItem(position, entranceK)
            }

            dialog.dismiss()
        }
    }
    else{

        // TODO: hide update button and remove space used by this
        dialog.btnSaveEntrance.visibility = View.VISIBLE
        dialog.btnUpdateEntrance.visibility = View.INVISIBLE

        dialog.btnSaveEntrance.setOnClickListener{

            val word = dialog.etWord.text.toString().trim()
            val translation = dialog.etTranslation.text.toString().trim()
            val phonetic = dialog.etPhonetic.text.toString().trim()

            // make some checks for fields data
            if(!entryCheck(activity,applicationServiceK,word,translation,phonetic)){
                return@setOnClickListener
            }

            val newEntrance = LessonEntranceK(word = word,translation = translation,
                phonetic = phonetic,lessonId = SELECTED_LESSON_ID)

            // add new entranceK in database
            applicationServiceK.lessonServiceK.insert(newEntrance)

            /**
             * This update should be made automatically using LiveData.

            // update recycler view with word from database (need update for primary key)
            if(updateRecyclerView){
                val updatedEntrance = applicationServiceK.lessonServiceK.getUpdatedEntrance(newEntrance)
                if(updatedEntrance != null && entranceRVAdapter != null) {
                    entranceRVAdapter.insertItem(updatedEntrance)
                }
            }

            */

            dialog.dismiss()
        }
    }

    dialog.btnCancelEntrance.setOnClickListener{
        dialog.dismiss()
    }

    dialog.show()
}

