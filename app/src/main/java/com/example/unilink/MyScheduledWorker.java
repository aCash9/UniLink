package com.example.unilink;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.unilink.RoomDatabase.DatabaseHelper;
import com.example.unilink.RoomDatabase.dao.ClubsDao;
import com.example.unilink.RoomDatabase.objects.ClubsInfo;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.ClubEventCard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyScheduledWorker extends Worker {
    private final Context context;
    public MyScheduledWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        performScheduledTask();
        return Result.success();
    }

    private void performScheduledTask() {
        FirebaseController controller = new FirebaseController();
        DatabaseHelper databaseHelper = DatabaseHelper.getDB(context);
        ClubsDao clubsDao = databaseHelper.clubsDao();

        clubsDao.deleteAll();

        controller.getClubsCard(list -> {
            List<ClubsInfo> offlineList = new ArrayList<>();
            int counter = 0;
            for(ClubEventCard club : list) {
                new DownloadImageTask(context, String.valueOf(counter)).execute(club.getBannerURL());
                ClubsInfo clubsInfo = new ClubsInfo(String.valueOf(new File(context.getFilesDir(), String.valueOf(counter))), club.getClubUID());
                offlineList.add(clubsInfo);
                counter++;
            }

            clubsDao.insertAll(offlineList);
        });
    }
}
