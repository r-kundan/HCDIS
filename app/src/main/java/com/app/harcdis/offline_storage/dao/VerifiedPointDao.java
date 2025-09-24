package com.app.harcdis.offline_storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;
import com.app.harcdis.offline_storage.entites.VerifiedPointRecord;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface VerifiedPointDao {

    @Query("SELECT * FROM VerifiedPointRecord")
    List<VerifiedPointRecord> getAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<VerifiedPointRecord> verifiedPointRecords);

    @Insert
    void insert(VerifiedPointRecord verifiedPointRecord);

    @Delete
    void delete(VerifiedPointRecord verifiedPointRecord);

    @Query("DELETE FROM VerifiedPointRecord")
    void deleteAll();


}
