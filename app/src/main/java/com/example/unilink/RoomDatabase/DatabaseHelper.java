package com.example.unilink.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.unilink.RoomDatabase.dao.ClubsDao;
import com.example.unilink.RoomDatabase.dao.UserDao;
import com.example.unilink.RoomDatabase.objects.ClubsInfo;
import com.example.unilink.RoomDatabase.objects.UserInfo;

@Database(entities = {UserInfo.class, ClubsInfo.class}, exportSchema = false, version = 1)
public abstract class DatabaseHelper extends RoomDatabase {
    private static final String DB_NAME = "uniLink.db";
    private static  DatabaseHelper instance;
    public static synchronized DatabaseHelper getDB(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context, DatabaseHelper.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract UserDao userDao();
    public abstract ClubsDao clubsDao();
}
