package hn.techcom.com.hnapp.Database;

import androidx.room.RoomDatabase;

import hn.techcom.com.hnapp.Interfaces.UserProfileDao;

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDao userProfileDao();
}
