package com.carematix.twiliochatapp.architecture.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.carematix.twiliochatapp.architecture.converter.LinksTypeConverter;
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;
import com.carematix.twiliochatapp.architecture.table.UserChat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserChat.class,UserAllList.class, ChannelList.class, UserChannelList.class}, version = 1,exportSchema = false)
//@TypeConverters({LinksTypeConverter.class})
public abstract
class AppDatabase extends RoomDatabase {

    public abstract UserChatDao userChatDao();
    public abstract UserListDao userListDao();
    public abstract ChannelListDao channelListDao();
    public abstract UserChannelDao userChannelDao();

    private static AppDatabase INSTANCE;
    private static String DATABASE_NAME="chat_database";

    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = getDatabase(context);
            }
        }
        return INSTANCE;
    }

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
