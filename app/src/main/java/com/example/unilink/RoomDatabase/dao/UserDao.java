package com.example.unilink.RoomDatabase.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.unilink.RoomDatabase.objects.UserInfo;

import java.util.List;

@Dao
public interface UserDao {


    @Query("SELECT * FROM user_info")
     List<UserInfo> getAll();

    @Query("Delete FROM user_info")
    void deleteAll();

    @Insert
    void insert(UserInfo user);

}
