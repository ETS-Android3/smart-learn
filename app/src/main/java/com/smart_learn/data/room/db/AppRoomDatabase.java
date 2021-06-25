package com.smart_learn.data.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.room.converters.TranslationConverter;
import com.smart_learn.data.room.dao.ExpressionDao;
import com.smart_learn.data.room.dao.FriendDao;
import com.smart_learn.data.room.dao.LessonDao;
import com.smart_learn.data.room.dao.NotificationDao;
import com.smart_learn.data.room.dao.WordDao;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.Friend;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.Notification;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BackupStatus;
import com.smart_learn.data.room.entities.helpers.DocumentMetadata;
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
        entities = {Lesson.class, Word.class, Expression.class, Friend.class, Notification.class},
        version = 1, exportSchema = false
)
@TypeConverters({TranslationConverter.class})
public abstract class AppRoomDatabase extends RoomDatabase {

    public static final String FRIENDS_TABLE = "friends";
    public static final String NOTIFICATIONS_TABLE = "notifications";

    public abstract LessonDao lessonDao();
    public abstract WordDao wordDao();
    public abstract ExpressionDao expressionDao();
    public abstract FriendDao friendDao();
    public abstract NotificationDao notificationDao();

    private static final String ADMIN_VALUE = "ADMIN_VALUE";

    private static volatile AppRoomDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized AppRoomDatabase getDatabaseInstance(final Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppRoomDatabase.class, RoomConfig.DATABASE_NAME)
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

                DocumentMetadata documentMetadata = new DocumentMetadata("", System.currentTimeMillis(),
                        System.currentTimeMillis(), BackupStatus.getStandardAddedBackupStatus());

                Lesson lesson = new Lesson(null,"notes lectia 1",false,false, documentMetadata,"lectia 1");
                int idLesson = Math.toIntExact(instance.lessonDao().insert(lesson));

                ArrayList<Translation> translations = new ArrayList<>();
                translations.add(new Translation("trans 1","phon 1", ""));
                translations.add(new Translation("trans 2","phon 2", ""));
                Word word = new Word(null,"word 1",false,false,
                        documentMetadata,idLesson,false,"", translations,"word 1");
                instance.wordDao().insert(word);

                word = new Word(null,"notes word 2",false,false,
                        documentMetadata,idLesson,false,"", translations,"word 2");
                instance.wordDao().insert(word);

                word = new Word(null,"notes word 3",false,false,
                        documentMetadata,idLesson,false,"", translations,"word 3");
                instance.wordDao().insert(word);

                word = new Word(null,"notes word 4",false,false,
                        documentMetadata,idLesson,false,"", translations,"word 4");
                instance.wordDao().insert(word);

                lesson = new Lesson(null,"notes lectia 2",false,false, documentMetadata,"lectia 2");
                instance.lessonDao().insert(lesson);
                lesson = new Lesson(null,"notes lectia 3",false,false, documentMetadata,"lectia 3");
                instance.lessonDao().insert(lesson);
                lesson = new Lesson(null,"notes lectia 4",false,false, documentMetadata,"lectia 4");
                instance.lessonDao().insert(lesson);
            });
        }
    };
}
