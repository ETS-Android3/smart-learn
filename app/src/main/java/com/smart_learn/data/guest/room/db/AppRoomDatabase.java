package com.smart_learn.data.guest.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.data.guest.room.converters.BooleanConverter;
import com.smart_learn.data.guest.room.converters.TranslationConverter;
import com.smart_learn.data.guest.room.dao.ExpressionDao;
import com.smart_learn.data.guest.room.dao.LessonDao;
import com.smart_learn.data.guest.room.dao.NotificationDao;
import com.smart_learn.data.guest.room.dao.RoomTestDao;
import com.smart_learn.data.guest.room.dao.WordDao;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.data.guest.room.entitites.Notification;
import com.smart_learn.data.guest.room.entitites.RoomTest;
import com.smart_learn.data.guest.room.entitites.Word;
import com.smart_learn.data.guest.room.entitites.helpers.BasicInfo;
import com.smart_learn.data.common.entities.Translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create the room database for local storage
 *
 * https://developer.android.com/codelabs/android-room-with-a-view#7
 * */
@Database(
        entities = {Lesson.class, Word.class, Expression.class, Notification.class, RoomTest.class},
        version = 1, exportSchema = false
)
@TypeConverters({TranslationConverter.class, BooleanConverter.class})
public abstract class AppRoomDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "app_room_db";
    public static final String LESSONS_TABLE = "lessons";
    public static final String WORDS_TABLE = "words";
    public static final String EXPRESSIONS_TABLE = "expressions";
    public static final String NOTIFICATIONS_TABLE = "notifications";
    public static final String TESTS_TABLE = "tests";

    public abstract LessonDao lessonDao();
    public abstract WordDao wordDao();
    public abstract ExpressionDao expressionDao();
    public abstract NotificationDao notificationDao();
    public abstract RoomTestDao roomTestDao();

    private static volatile AppRoomDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized AppRoomDatabase getDatabaseInstance(final Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppRoomDatabase.class, DATABASE_NAME)
                    .addCallback(roomDatabaseCallback)
                    .build();
        }

        return instance;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(AppRoomDatabase::addInitialData);
        }
    };

    private static void addInitialData(){
        BasicInfo basicInfo = new BasicInfo(System.currentTimeMillis());


        // add first lesson
        int translationsUniqueId = 0;

        Lesson lesson = new Lesson("Diverse cuvinte și expresii în limba engleză",false, basicInfo,"Lecție engleză");
        int idLesson = Math.toIntExact(instance.lessonDao().insert(lesson));

        // add words
        ArrayList<Pair<String, ArrayList<String>>> valuesWords = new ArrayList<>();
        valuesWords.add(new Pair<>("Good luck", new ArrayList<>(Arrays.asList("Noroc!","Numai bine!"))));
        valuesWords.add(new Pair<>("Hi", new ArrayList<>(Arrays.asList("Salut!","Bună!","Hei!"))));
        valuesWords.add(new Pair<>("car", new ArrayList<>(Arrays.asList("mașină","autoturism","automobil"))));
        valuesWords.add(new Pair<>("clean", new ArrayList<>(Arrays.asList("curat", "îngrijit"))));
        valuesWords.add(new Pair<>("green", new ArrayList<>(Collections.singletonList("verde"))));
        valuesWords.add(new Pair<>("orange", new ArrayList<>(Arrays.asList("portocală","portocaliu"))));
        valuesWords.add(new Pair<>("plane", new ArrayList<>(Collections.singletonList("avion"))));
        valuesWords.add(new Pair<>("sky", new ArrayList<>(Arrays.asList("cer","orizont"))));
        valuesWords.add(new Pair<>("tree", new ArrayList<>(Arrays.asList("copac","arbore","pom"))));
        valuesWords.add(new Pair<>("universe", new ArrayList<>(Arrays.asList("univers", "cosmos"))));

        ArrayList<Word> wordsList = new ArrayList<>();
        for(Pair<String, ArrayList<String>> pair : valuesWords){
            ArrayList<Translation> translations = new ArrayList<>();
            for(String item : pair.second){
                translationsUniqueId++;
                translations.add(new Translation(translationsUniqueId, item,"", ""));
            }

            Word word = new Word("",false, basicInfo, idLesson,false,"",
                    translations,pair.first, "");
            wordsList.add(word);
        }

        instance.wordDao().insertAll(wordsList);


        // add expressions
        ArrayList<Pair<String, ArrayList<String>>> valuesExp = new ArrayList<>();
        valuesExp.add(new Pair<>("A storm in a teacup", new ArrayList<>(Collections.singletonList("Gălăgie făcută în jurul unui subiect neimportant"))));
        valuesExp.add(new Pair<>("As cool as a cucumber", new ArrayList<>(Collections.singletonList("A fi calm, cumpătat și netulburat de stres"))));
        valuesExp.add(new Pair<>("He has bigger fish to fry", new ArrayList<>(Collections.singletonList("Are lucruri mai mari de care să aibă grijă decât ceea ce vorbim acum"))));
        valuesExp.add(new Pair<>("Hold your horses!", new ArrayList<>(Collections.singletonList("Așteaptă puțin!"))));
        valuesExp.add(new Pair<>("It's a piece of cake", new ArrayList<>(Arrays.asList("Este ușor", "E o nimica toată"))));
        valuesExp.add(new Pair<>("It's raining cats and dogs", new ArrayList<>(Arrays.asList("Plouă cu găleata", "Plouă puternic"))));
        valuesExp.add(new Pair<>("Kill two birds with one stone", new ArrayList<>(Collections.singletonList("A împușca doi iepuri dintr-un foc"))));
        valuesExp.add(new Pair<>("Let the cat out of the bag", new ArrayList<>(Arrays.asList("A se da de gol","A da în vileag un secret"))));
        valuesExp.add(new Pair<>("To be barking up the wrong tree", new ArrayList<>(Collections.singletonList("A urmări o pistă greșită"))));
        valuesExp.add(new Pair<>("To turn a blind eye", new ArrayList<>(Collections.singletonList("A ignora în mod deliberat un fapt"))));

        ArrayList<Expression> expressionsList = new ArrayList<>();
        for(Pair<String, ArrayList<String>> pair : valuesExp){
            ArrayList<Translation> translations = new ArrayList<>();
            for(String item : pair.second){
                translationsUniqueId++;
                translations.add(new Translation(translationsUniqueId, item,"", ""));
            }

            Expression expression = new Expression("",false, basicInfo, idLesson,false,"",
                    translations,pair.first);
            expressionsList.add(expression);
        }

        instance.expressionDao().insertAll(expressionsList);





        // add second lesson

        translationsUniqueId = 0;
        lesson = new Lesson("Această lecție conține:\n- regionalisme\n- diverse expresii",false, basicInfo,"Regionalisme și expresii");
        idLesson = Math.toIntExact(instance.lessonDao().insert(lesson));

        // add words
        valuesWords = new ArrayList<>();
        valuesWords.add(new Pair<>("bai", new ArrayList<>(Arrays.asList("supărare","necaz","bucluc","încurcătură"))));
        valuesWords.add(new Pair<>("barabulă", new ArrayList<>(Collections.singletonList("cartof"))));
        valuesWords.add(new Pair<>("gâvan", new ArrayList<>(Collections.singletonList("polonic"))));
        valuesWords.add(new Pair<>("obijdui", new ArrayList<>(Collections.singletonList("a neîndreptăți"))));
        valuesWords.add(new Pair<>("păpușoi", new ArrayList<>(Collections.singletonList("porumb"))));

        wordsList = new ArrayList<>();
        for(Pair<String, ArrayList<String>> pair : valuesWords){
            ArrayList<Translation> translations = new ArrayList<>();
            for(String item : pair.second){
                translationsUniqueId++;
                translations.add(new Translation(translationsUniqueId, item,"", ""));
            }

            Word word = new Word("",false, basicInfo, idLesson,false,"",
                    translations,pair.first, "");
            wordsList.add(word);
        }

        instance.wordDao().insertAll(wordsList);


        // add expressions
        valuesExp = new ArrayList<>();
        valuesExp.add(new Pair<>("a băga de seamă", new ArrayList<>(Arrays.asList("a observa cu mare atenție", "a fi foarte atent în ceea ce face"))));
        valuesExp.add(new Pair<>("a face pe niznaiul", new ArrayList<>(Arrays.asList("a se preface că nu ştie nimic", "a se preface că nu îi place ceva"))));
        valuesExp.add(new Pair<>("a fi prins cu mâța-n sac", new ArrayList<>(Arrays.asList("a fi prins cu minciuna","a fi prins cu înșelând"))));
        valuesExp.add(new Pair<>("a-și lua nasul la purtare", new ArrayList<>(Collections.singletonList("a se obrăznici"))));

        expressionsList = new ArrayList<>();
        for(Pair<String, ArrayList<String>> pair : valuesExp){
            ArrayList<Translation> translations = new ArrayList<>();
            for(String item : pair.second){
                translationsUniqueId++;
                translations.add(new Translation(translationsUniqueId, item,"", ""));
            }

            Expression expression = new Expression("",false, basicInfo, idLesson,false,"",
                    translations,pair.first);
            expressionsList.add(expression);
        }

        instance.expressionDao().insertAll(expressionsList);

    }
}
