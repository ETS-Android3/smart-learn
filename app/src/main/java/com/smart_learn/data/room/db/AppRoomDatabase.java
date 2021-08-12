package com.smart_learn.data.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.data.room.converters.BooleanConverter;
import com.smart_learn.data.room.converters.TranslationConverter;
import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.dao.LessonDao;
import com.smart_learn.data.room.dao.NotificationDao;
import com.smart_learn.data.room.dao.RoomTestDao;
import com.smart_learn.data.room.dao.WordDao;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.Notification;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.Translation;

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

        int translationsUniqueId = 0;

        Lesson lesson = new Lesson("This is an example lesson",false, basicInfo,"Example lesson");
        int idLesson = Math.toIntExact(instance.lessonDao().insert(lesson));

        // add words
        ArrayList<Pair<String, ArrayList<String>>> valuesWords = new ArrayList<>();
        valuesWords.add(new Pair<>("Hi", new ArrayList<>(Arrays.asList("Salut","Buna"))));
        valuesWords.add(new Pair<>("Good luck", new ArrayList<>(Arrays.asList("Noroc","Numai bine"))));
        valuesWords.add(new Pair<>("car", new ArrayList<>(Arrays.asList("masina","autoturism"))));
        valuesWords.add(new Pair<>("orange", new ArrayList<>(Arrays.asList("portocala","portocaliu"))));
        valuesWords.add(new Pair<>("sky", new ArrayList<>(Arrays.asList("cer","orizont"))));
        valuesWords.add(new Pair<>("avion", new ArrayList<>(Collections.singletonList("plane"))));
        valuesWords.add(new Pair<>("verde", new ArrayList<>(Collections.singletonList("green"))));
        valuesWords.add(new Pair<>("curat", new ArrayList<>(Arrays.asList("clean", "fresh"))));
        valuesWords.add(new Pair<>("univers", new ArrayList<>(Arrays.asList("universe", "cosmos"))));
        valuesWords.add(new Pair<>("copac", new ArrayList<>(Collections.singletonList("tree"))));

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
        valuesExp.add(new Pair<>("It's a piece of cake", new ArrayList<>(Arrays.asList("Este usor", "E o nimica toata"))));
        valuesExp.add(new Pair<>("It's raining cats and dogs", new ArrayList<>(Arrays.asList("Ploua cu galeata", "Plouă puternic"))));
        valuesExp.add(new Pair<>("Kill two birds with one stone", new ArrayList<>(Collections.singletonList("omorî doi iepuri dintr-o împuşcătură"))));
        valuesExp.add(new Pair<>("Let the cat out of the bag", new ArrayList<>(Arrays.asList("Da în vileag un secret","se da de gol"))));
        valuesExp.add(new Pair<>("He has bigger fish to fry", new ArrayList<>(Collections.singletonList("Are lucruri mai mari de care să aibă grijă decât ceea ce vorbim acum"))));
        valuesExp.add(new Pair<>("A-și lua nasul la purtare", new ArrayList<>(Collections.singletonList("to be impudent"))));
        valuesExp.add(new Pair<>("A călca pe bec", new ArrayList<>(Collections.singletonList(" to break a rule"))));
        valuesExp.add(new Pair<>("A fi prins cu mâța-n sac", new ArrayList<>(Collections.singletonList("be caught lying"))));
        valuesExp.add(new Pair<>("A ieși basma curată", new ArrayList<>(Collections.singletonList("to get rid of accusations"))));
        valuesExp.add(new Pair<>("A-l scoate din pepeni", new ArrayList<>(Collections.singletonList("to annoy someone"))));

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

    }
}
