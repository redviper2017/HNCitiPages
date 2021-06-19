package hn.techcom.com.hnapp.Interfaces;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hn.techcom.com.hnapp.Entities.UserProfile;

public interface UserProfileDao {
    @Query("SELECT * FROM userprofile")
    List<UserProfile> get();

    @Update
    void update(UserProfile userProfile);

    @Insert
    void insert(UserProfile userProfile);

    @Delete
    void delete(UserProfile userProfile);
}
