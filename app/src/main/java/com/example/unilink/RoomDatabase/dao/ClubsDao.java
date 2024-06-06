package com.example.unilink.RoomDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.unilink.RoomDatabase.objects.ClubsInfo;

import java.util.List;

@Dao
public interface ClubsDao {
    @Query("SELECT * FROM clubs")
    public List<ClubsInfo> getAll();

    @Query("Delete FROM clubs")
    void deleteAll();

    @Insert
    void insertAll(List<ClubsInfo> list);
}
