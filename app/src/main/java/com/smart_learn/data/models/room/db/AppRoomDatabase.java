package com.smart_learn.data.models.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.config.RoomConfig;
import com.smart_learn.data.models.room.dao.LessonDao;
import com.smart_learn.data.models.room.dao.WordDao;
import com.smart_learn.data.models.room.dao.ExpressionDao;
import com.smart_learn.data.models.room.dao.SentenceDao;
import com.smart_learn.data.models.room.entities.Expression;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.entities.Sentence;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.models.room.entities.helpers.Translation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create the room database for local storage
 *
 * https://developer.android.com/codelabs/android-room-with-a-view#7
 * */
@Database(
        entities = {Lesson.class, Word.class, Sentence.class, Expression.class},
        version = 1, exportSchema = false
)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract LessonDao lessonDao();
    public abstract WordDao wordDao();
    public abstract SentenceDao sentenceDao();
    public abstract ExpressionDao expressionDao();

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
                LessonDao dao = instance.lessonDao();

                Lesson a = new Lesson("Lesson 1", System.currentTimeMillis(), System.currentTimeMillis());
                Lesson b = new Lesson("Lesson 2", System.currentTimeMillis(), System.currentTimeMillis());
                long id = dao.insert(b);
                id = dao.insert(a);


                WordDao wordDao = instance.wordDao();
                Word aa = new Word(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("trans 1", "phon 1"),"word 1");
                Word bb = new Word(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("trans 2", "phon 2"),"word 2");

                wordDao.insert(aa);
                wordDao.insert(bb);



                ExpressionDao expDao = instance.expressionDao();
                Expression aaa = new Expression(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("exp 1", "phon exp 1"),"exp 1");

                Expression bbb = new Expression(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("exp 2", "phon exp 2"),"exp 2");


                expDao.insert(aaa);
                expDao.insert(bbb);

                SentenceDao senDao = instance.sentenceDao();
                Sentence aaaa = new Sentence(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("sen 1", "phon sen 1"),"sen 1");

                Sentence bbbb = new Sentence(System.currentTimeMillis(),System.currentTimeMillis(),id,
                        new Translation("sen 2", "phon sen 2"),"sen 2");

                senDao.insert(aaaa);
                senDao.insert(bbbb);

            });
        }
    };
}
