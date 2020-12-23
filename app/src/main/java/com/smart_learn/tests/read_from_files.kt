package com.smart_learn.tests

/** from nextPressed in TestActivity
*
if(!isReadFromExternalStoragePermission(activity, activity.activityTestLayout,false)){
    return
}

*/

/*

init{

    //File(getExternalFilesDir("")!!.absolutePath).walk().forEach {  println(it.absolutePath) }
    //File(Environment.getExternalStorageDirectory().absolutePath).walk().forEach { println(it.absolutePath)}
    //myExternalFile = File(getExternalFilesDir(filepath), fileName.text.toString())
    //myExternalFile = File(Environment.getExternalStorageDirectory().absolutePath + "/", fileName.text.toString())

    // TODO: request permission I think it can be put in next button press
    if(!isReadFromExternalStoragePermission(activity,activity.activityTestLayout,true)){
        // the application cannot read because the permission for reading was denied

        // TODO: add flow for NOT read permission

        // TODO:
        //  FIX THIS BUG -- > when we enter in test page if permission does not exist and we accept words are not loaded
        //  we must enter again in test page to load words ---->

    }
    else{

        // TODO: this can be a load data function in repository
        try {
            // /storage/emulated/0/ --> root
            val root = Environment.getExternalStorageDirectory().absolutePath + "/"
            val filename = "words_external_2.csv"
            val pathname = root + filename
            val fileLines: List<String> = File(pathname)
                .bufferedReader()
                .use { it.readLines() }

            /*
              val fileLines: List<String> = applicationContext
                  .assets
                  .open("words.csv")
                  .bufferedReader()
                  .use { it.readLines() }

            */

            fileLines.forEach {
                println(it)
                val line: List<String> = it.split(",")
                this.entranceList.add(DictionaryEntrance(line[0], line[1], line[2]))
            }

            // TODO: learn more about Toast.make
            Toast.makeText(activity, SUCCESS_DATA_LOADED_MESSAGE, Toast.LENGTH_LONG)
                .show()
        }
        catch (e: Exception){
            //Log.e("init in Dictionary", pathname + "File Not Found [$e]")
            Toast.makeText(activity, "File Not Found",
                Toast.LENGTH_LONG).show()
        }
    }

}
*/