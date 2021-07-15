package com.smart_learn.data.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.data.room.converters.TranslationConverter;
import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.dao.LessonDao;
import com.smart_learn.data.room.dao.NotificationDao;
import com.smart_learn.data.room.dao.WordDao;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.Notification;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create the room database for local storage
 *
 * https://developer.android.com/codelabs/android-room-with-a-view#7
 * */
@Database(
        entities = {Lesson.class, Word.class, Expression.class, Notification.class},
        version = 1, exportSchema = false
)
@TypeConverters({TranslationConverter.class})
public abstract class AppRoomDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "app_room_db";
    public static final String LESSONS_TABLE = "lessons";
    public static final String WORDS_TABLE = "words";
    public static final String EXPRESSIONS_TABLE = "expressions";
    public static final String NOTIFICATIONS_TABLE = "notifications";

    public abstract LessonDao lessonDao();
    public abstract WordDao wordDao();
    public abstract ExpressionDao expressionDao();
    public abstract NotificationDao notificationDao();

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

            databaseWriteExecutor.execute(() -> {

                BasicInfo basicInfo = new BasicInfo(System.currentTimeMillis());

                Lesson lesson = new Lesson("notes lectia 1",false, basicInfo,"lectia 1");
                int idLesson = Math.toIntExact(instance.lessonDao().insert(lesson));

                ArrayList<Translation> translations = new ArrayList<>();
                translations.add(new Translation("trans 1","phon 1", ""));
                translations.add(new Translation("trans 2","phon 2", ""));
                Word word = new Word("word 1",false,
                        basicInfo,idLesson,false,"", translations,"word 1", "phonetic 1");
                instance.wordDao().insert(word);

                word = new Word("notes word 2",false,
                        basicInfo,idLesson,false,"", translations,"word 2", "phonetic 2");
                instance.wordDao().insert(word);

                word = new Word("notes word 3",false,
                        basicInfo,idLesson,false,"", translations,"word 3", "phonetic 3");
                instance.wordDao().insert(word);

                word = new Word("notes word 4",false,
                        basicInfo,idLesson,false,"", translations,"word 4", "phonetic 4");
                instance.wordDao().insert(word);

                lesson = new Lesson("notes lectia 2",false, basicInfo,"lectia 2");
                instance.lessonDao().insert(lesson);
                lesson = new Lesson("notes lectia 3",false, basicInfo,"lectia 3");
                instance.lessonDao().insert(lesson);
                lesson = new Lesson("notes lectia 4",false, basicInfo,"lectia 4");
                instance.lessonDao().insert(lesson);
            });
        }
    };
}
