package com.smart_learn.core.general

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.smart_learn.R
import com.smart_learn.data.entities.DictionaryDetails
import com.smart_learn.data.entities.DictionaryEntrance
import com.smart_learn.presenter.recycler_view.adapters.DictionariesRVAdapter
import com.smart_learn.presenter.recycler_view.adapters.EntrancesRVAdapter
import com.smart_learn.data.repository.DatabaseSchema
import com.smart_learn.core.services.ApplicationService
import kotlinx.android.synthetic.main.dialog_add_word.*
import kotlinx.android.synthetic.main.dialog_new_dictionary.*


fun showSettingsDialog(activity: Activity){
    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(R.layout.dialog_settings)

    dialog.show()
}



private fun dictionaryDetailsCheck(
    activity: Activity,
    applicationService: ApplicationService,
    dictionaryName: String
): Boolean{

    if (dictionaryName.isEmpty()) {
        Toast.makeText(activity, "Enter a name", Toast.LENGTH_LONG).show()
        return false
    }

    // check dictionaryName length
    if (dictionaryName.length > DatabaseSchema.DictionariesTable.DIMENSION_COLUMN_NAME) {
        Toast.makeText( activity, "This name is too big. Choose a shorter name.",
            Toast.LENGTH_LONG).show()
        return false
    }

    //add dictionary only if this does not exist
    if (applicationService.dictionaryService.checkIfDictionaryExist(dictionaryName)) {
        Toast.makeText(
            activity, "Dictionary $dictionaryName already exists. Choose other name",
            Toast.LENGTH_LONG).show()
        return false
    }

    return true

}


fun showAddDictionaryDialog(
    TAG: String,
    activity: Activity,
    dialogLayout: Int?,
    applicationService: ApplicationService,
    updateDictionary: Boolean = false,
    currentDictionary: DictionaryDetails? = null,
    updateRecyclerView: Boolean = false,
    dictionariesRVAdapter: DictionariesRVAdapter? = null,
    position: Int = -1
){

    // check if this occurs
    if(updateDictionary && currentDictionary == null){
        Log.e(UNEXPECTED_ERROR," current dictionary is null in update listener for $TAG")
        return
    }

    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(dialogLayout)


    if(updateDictionary && currentDictionary != null){
        // TODO: hide save button and remove space used by this
        dialog.btnSaveDict.visibility = View.INVISIBLE
        dialog.btnUpdateDict.visibility = View.VISIBLE

        // get current dictionary
        // set current data in dialog
        dialog.etDictionaryName.setText(currentDictionary.title)

        dialog.btnUpdateDict.setOnClickListener {

            // get inserted data
            val dictionaryName = dialog.etDictionaryName.text.toString().trim()

            // make some specific checks
            if(dictionaryName == currentDictionary.title){
                Toast.makeText(activity,"No modification was made",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // make some general check
            if(!dictionaryDetailsCheck(activity,applicationService,dictionaryName)){
                return@setOnClickListener
            }

            // dictionaryName is valid
            currentDictionary.title = dictionaryName

            // update in database
            applicationService.dictionaryService.updateDictionary(currentDictionary)

            // update item in recycler view
            if(updateRecyclerView && dictionariesRVAdapter != null) {
                dictionariesRVAdapter.updateItem(position, currentDictionary)
            }

            dialog.dismiss()
        }
    }
    else{

        // TODO: hide update button and remove space used by this
        dialog.btnSaveDict.visibility = View.VISIBLE
        dialog.btnUpdateDict.visibility = View.INVISIBLE

        dialog.btnSaveDict.setOnClickListener {

            val dictionaryName = dialog.etDictionaryName.text.toString().trim()

            // make some general checks
            if(!dictionaryDetailsCheck(activity,applicationService,dictionaryName)){
                return@setOnClickListener
            }

            // dictionaryName is valid
            val newDictionary: DictionaryDetails = DictionaryDetails(title = dictionaryName)

            // add in database
            applicationService.dictionaryService.addDictionary(dictionaryName)

            // update recycler view with dictionary from database (need update for primary key)
            if(updateRecyclerView){
                val updatedDictionary = applicationService.dictionaryService.getDictionary(dictionaryName)
                if(updatedDictionary != null && dictionariesRVAdapter != null) {
                    dictionariesRVAdapter.insertItem(updatedDictionary)
                }
            }

            dialog.dismiss()
        }
    }

    dialog.btnCancelDict.setOnClickListener{
        dialog.dismiss()
    }

    dialog.show()
}




private fun entryCheck(
    activity: Activity,
    applicationService: ApplicationService,
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

    if(word.length > DatabaseSchema.EntriesTable.DIMENSION_COLUMN_WORD){
        Toast.makeText(activity, "This word is too big.",Toast.LENGTH_LONG).show()
        return false
    }

    if(phonetic.length > DatabaseSchema.EntriesTable.DIMENSION_COLUMN_PHONETIC){
        Toast.makeText(activity, "This phonetic translation is too big.",
            Toast.LENGTH_LONG).show()
        return false
    }

    // TODO: check to see if word exist in all database not only in one dictionary
    // add word only if this does not exists in current dictionary
    if(word.isNotEmpty() &&
        applicationService.dictionaryService.checkIfWordExist(word,SELECTED_DICTIONARY_ID)){

        Toast.makeText(activity, "Word $word already exists in this dictionary.",
            Toast.LENGTH_LONG).show()
        return false
    }

    // TODO: check if translation exists in dictionary

    return true

}


fun showAddEntranceDialog(
    TAG: String,
    activity: Activity,
    dialogLayout: Int?,
    applicationService: ApplicationService,
    updateEntrance: Boolean = false,
    entrance: DictionaryEntrance? = null,
    updateRecyclerView: Boolean = false,
    entranceRVAdapter: EntrancesRVAdapter? = null,
    position: Int = -1
){

    // check if this occurs
    if(updateEntrance && entrance == null){
        Log.e(UNEXPECTED_ERROR," current entrance is null in update listener for $TAG")
        return
    }

    // must be called with activity -- if is called with 'activity.applicationContext'
    // will produce a null exception on application context
    val dialog = MaterialDialog(activity)
        .noAutoDismiss()
        .customView(dialogLayout)

    if(updateEntrance && entrance != null){

        // TODO: hide update button and remove space used by this
        dialog.btnSaveEntrance.visibility = View.INVISIBLE
        dialog.btnUpdateEntrance.visibility = View.VISIBLE

        // set current data on dialog elements
        dialog.etWord.setText(entrance.word)
        dialog.etTranslation.setText(entrance.translation)
        dialog.etPhonetic.setText(entrance.phonetic)

        dialog.btnUpdateEntrance.setOnClickListener{

            val word = dialog.etWord.text.toString().trim()
            val translation = dialog.etTranslation.text.toString().trim()
            val phonetic = dialog.etPhonetic.text.toString().trim()

            // make some specific checks for fields data
            if(word == entrance.word && translation == entrance.translation && phonetic == entrance.phonetic){
                Toast.makeText(activity,"No modification was made",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // make some general checks
            if(!entryCheck(activity,applicationService,word,translation,phonetic)){
                return@setOnClickListener
            }

            // update current entrance
            entrance.word = word
            entrance.translation = translation
            entrance.phonetic = phonetic

            // update entrance in database
            applicationService.dictionaryService.updateEntrance(entrance)

            // update item in recycler view
            if(updateRecyclerView && entranceRVAdapter != null) {
                entranceRVAdapter.updateItem(position, entrance)
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
            if(!entryCheck(activity,applicationService,word,translation,phonetic)){
                return@setOnClickListener
            }

            val newEntrance = DictionaryEntrance(word = word,translation = translation,
                phonetic = phonetic,dictionaryId = SELECTED_DICTIONARY_ID)

            // add new entrance in database
            applicationService.dictionaryService.addEntrance(newEntrance)

            // update recycler view with word from database (need update for primary key)
            if(updateRecyclerView){
                val updatedEntrance = applicationService.dictionaryService.getUpdatedEntrance(newEntrance)
                if(updatedEntrance != null && entranceRVAdapter != null) {
                    entranceRVAdapter.insertItem(updatedEntrance)
                }
            }

            dialog.dismiss()
        }
    }

    dialog.btnCancelEntrance.setOnClickListener{
        dialog.dismiss()
    }

    dialog.show()
}

