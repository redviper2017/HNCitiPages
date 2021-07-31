package hn.techcom.com.hncitipages.Database;

import androidx.room.RoomDatabase;

import hn.techcom.com.hncitipages.Interfaces.UserProfileDao;

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserProfileDao userProfileDao();
}
