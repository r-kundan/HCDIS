package com.app.harcdis.offline_storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.harcdis.offline_storage.dao.NewPointRecordDao;
import com.app.harcdis.offline_storage.dao.VerifiedPointDao;
import com.app.harcdis.offline_storage.entites.NewPointRecord;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {NewPointRecord.class, VerifiedPointRecord.class}, version = 9, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 1;
    private static volatile AppDataBase INSTANCE;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, "HCDISAPPDATABASELIVE")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract NewPointRecordDao newPointRecordDao();
    public abstract VerifiedPointDao verifiedPointDao();

}
