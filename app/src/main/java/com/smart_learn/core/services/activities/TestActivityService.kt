package com.smart_learn.core.services.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import com.smart_learn.presenter.activities.TestActivity
import com.smart_learn.data.entities.DictionaryEntrance
import com.smart_learn.core.general.ActivityServiceUtilities
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.core.services.ApplicationService
import com.smart_learn.core.services.LessonServiceK
import kotlinx.android.synthetic.main.page_test_knowledge.*

class TestActivityService(private val testActivity: TestActivity) :
    ActivityServiceUtilities<TestActivityService, TestActivity> {

    // get the activity
    private var activity: Activity = testActivity.getActivity()

    // GUI element
    private var checkBoxButtonList: List<CheckBox> = ArrayList()

    // other elements
    private var lessonServiceK: LessonServiceK
    private var questionsNumber: Int = 0
    private var currentQuestionNumber = 0
    private var score = 0
    private var checkInsertedResult : Boolean = false
    private var randomNumber : Int = -1 // with -1 I cannot obtain any value
    private var entriesList: List<DictionaryEntrance>
    private var entrance : DictionaryEntrance? = null

    init {

        // add radio buttons in an array
        (checkBoxButtonList as ArrayList<CheckBox>).add(activity.tenCheck)
        (checkBoxButtonList as ArrayList<CheckBox>).add(activity.fifthCheck)
        (checkBoxButtonList as ArrayList<CheckBox>).add(activity.twentyCheck)
        (checkBoxButtonList as ArrayList<CheckBox>).add(activity.twentyFiveCheck)


        // set listeners
        activity.reverseSwitch.setOnClickListener {
            // phonetic translation is used only when reverseSwitch is active
            activity.phoneticSwitch.isClickable = activity.reverseSwitch.isChecked
        }

        for ((index, checkBoxButton) in checkBoxButtonList.withIndex()){
            checkBoxButton.setOnClickListener {
                markCheckBoxButton(index)
            }
        }

        activity.nextButton.setOnClickListener {nextPressed()}
        activity.resetButton.setOnClickListener {resetPressed()}

        // make other initial settings
        // this must be initialised here to prevent problems when you initialize with 'this'
        lessonServiceK = LessonServiceK(this)
        entriesList = lessonServiceK.getFullLiveLessonInfo(SELECTED_DICTIONARY_ID)

    }

    private fun markCheckBoxButton(buttonNumber: Int){

        // uncheck the rest of the buttons
        for ((index, checkBoxButton) in checkBoxButtonList.withIndex()){
            if (index != buttonNumber ) {
                checkBoxButton.isChecked = false
            }
        }

        // set questions number
        if (checkBoxButtonList[buttonNumber].isChecked){
            when(buttonNumber){
                0 -> questionsNumber = 10
                1 -> questionsNumber = 15
                2 -> questionsNumber = 20
                3 -> questionsNumber = 25
            }
        }
        else {
            questionsNumber = 0
        }
    }


    @SuppressLint("SetTextI18n")
    private fun checkResult(){

        checkInsertedResult = false

        // get inserted result
        val translation = activity.translationTextArea.text.toString()
        val phonetic = activity.phoneticTextArea.text.toString()

        // check for reverse and phonetics
        if(activity.reverseSwitch.isChecked && activity.phoneticSwitch.isChecked) {

            if(translation == entrance?.translation && phonetic == entrance?.phonetic){
                score++
                activity.resultLabel.text = "Correct!"
                return
            }

            activity.resultLabel.text =  entrance?.translation + " " + entrance?.phonetic
            return
        }

        // check only for reverse
        if(activity.reverseSwitch.isChecked){

            if(translation == entrance?.translation){
                score++
                activity.resultLabel.text = "Correct!"
                return
            }

            activity.resultLabel.text =  entrance?.translation
            return
        }

        // normal check ( without reverse / reverse and phonetics )
        if(translation == entrance?.word){
            score++
            activity.resultLabel.text = "Correct!"
            return
        }

        activity.resultLabel.text = entrance?.word
    }


    @SuppressLint("SetTextI18n")
    private fun resetPressed(){

        if (questionsNumber <= 0){
            Toast.makeText(activity.applicationContext, "Select questions number!", Toast.LENGTH_LONG).show()
            return
        }

        score = 0
        currentQuestionNumber = 1
        checkInsertedResult = false
        randomNumber = -1 // with -1 I cannot obtain any value
        entrance = null

        activity.scoreLabel.text = ""
        activity.wordLabel.text = ""
        activity.resultLabel.text = ""
        activity.translationTextArea.setText("")
        activity.phoneticTextArea.setText("")
    }

    @SuppressLint("SetTextI18n")
    private fun nextPressed(){

        if (questionsNumber <= 0){
            Toast.makeText(activity.applicationContext, "Select questions number!", Toast.LENGTH_LONG).show()
            return
        }

        if (checkInsertedResult){
            checkResult()
            activity.scoreLabel.text = "[$currentQuestionNumber] $score / $questionsNumber"
            return
        }

        // final check when test is over
        if(currentQuestionNumber == questionsNumber){
            activity.resultLabel.text = "Score: $score / $questionsNumber"
            currentQuestionNumber = 0
            return
        }

        // test is not over ( we still have question left )
        // generate new word
        randomNumber = generateEntranceRandomIndex()
        entrance = getEntrance(randomNumber)

        if(entrance == null)
            return

        if(activity.reverseSwitch.isChecked) {
            activity.wordLabel.text = entrance?.word
        }
        else{
            activity.wordLabel.text = entrance?.translation
        }

        checkInsertedResult = true // on next click we will check the inserted result
        currentQuestionNumber++
        activity.resultLabel.text = "?"
        activity.translationTextArea.setText("")
        activity.phoneticTextArea.setText("")
        activity.scoreLabel.text = "[$currentQuestionNumber] $score / $questionsNumber"

    }

    private fun generateEntranceRandomIndex() : Int {

        if (entriesList.isEmpty()){
            return -1
        }

        // generates random number between 0 and entriesList.size - 1
        return (entriesList.indices).random()
    }


    private fun getEntrance(index: Int) : DictionaryEntrance? {

        if(entriesList.isEmpty()){
            Toast.makeText(activity, "No words available!", Toast.LENGTH_LONG).show()
            return null
        }

        if(index < 0 || index >= entriesList.size) {
            Log.e("[TestActivityService]"," Index $index is incorrect!")
            return null
        }

        return entriesList[index];

    }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): TestActivityService {
        return this
    }

    override fun getParentActivity(): TestActivity {
        return testActivity
    }

    override fun getApplicationService(): ApplicationService {
        TODO("Not yet implemented")
    }

}