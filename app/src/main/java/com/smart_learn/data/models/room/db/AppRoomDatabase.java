package com.smart_learn.data.models.room.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.smart_learn.core.config.RoomConfig;
import com.smart_learn.data.models.room.dao.LessonDao;
import com.smart_learn.data.models.room.dao.WordDao;
import com.smart_learn.data.models.room.dao.ExpressionDao;
import com.smart_learn.data.models.room.entities.Expression;
import com.smart_learn.data.models.room.entities.Lesson;
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
        entities = {Lesson.class, Word.class, Expression.class},
        version = 1, exportSchema = false
)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract LessonDao lessonDao();
    public abstract WordDao wordDao();
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

                Lesson a = new Lesson("Lesson 1", System.currentTimeMillis(), System.currentTimeMillis(), false);
                Lesson b = new Lesson("Lesson 2", System.currentTimeMillis(), System.currentTimeMillis(), false);
                Lesson c = new Lesson("Lesson 3", System.currentTimeMillis(), System.currentTimeMillis(), false);
                Lesson d = new Lesson("Lesson 4", System.currentTimeMillis(), System.currentTimeMillis(), false);
                int id =  Math.toIntExact(dao.insert(b));
                id =  Math.toIntExact(dao.insert(c));
                id =  Math.toIntExact(dao.insert(d));
                id =  Math.toIntExact(dao.insert(a));

                WordDao wordDao = instance.wordDao();
                Word aa = new Word(System.currentTimeMillis(),System.currentTimeMillis(),id,false,
                        new Translation("trans 1", "phon 1"),"word 1");
                Word bb = new Word(System.currentTimeMillis(),System.currentTimeMillis(),id,false,
                        new Translation("trans 2", "phon 2"),"word 2");

                wordDao.insert(aa);
                wordDao.insert(bb);



                ExpressionDao expDao = instance.expressionDao();
                Expression aaa = new Expression(System.currentTimeMillis(),System.currentTimeMillis(),id,false,
                        new Translation("exp 1", "phon exp 1"),"exp 1");

                Expression bbb = new Expression(System.currentTimeMillis(),System.currentTimeMillis(),id,false,
                        new Translation("exp 2", "phon exp 2"),"exp 2");


                expDao.insert(aaa);
                expDao.insert(bbb);

            });
        }
    };
}
