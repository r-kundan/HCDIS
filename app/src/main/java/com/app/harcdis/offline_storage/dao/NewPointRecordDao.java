package com.app.harcdis.offline_storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.harcdis.offline_storage.entites.NewPointRecord;
import com.app.harcdis.offline_storage.entites.NewPointRecord;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NewPointRecordDao {

    @Query("SELECT * FROM NewPointRecord")
    List<NewPointRecord> getAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<NewPointRecord> pointRecords);

    @Insert
    void insert(NewPointRecord newPointRecord);

    @Delete
    void delete(NewPointRecord newPointRecord);

    @Query("DELETE FROM NewPointRecord")
    void deleteAll();


}
