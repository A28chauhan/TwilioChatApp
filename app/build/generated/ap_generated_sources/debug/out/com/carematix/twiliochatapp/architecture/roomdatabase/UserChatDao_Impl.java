package com.carematix.twiliochatapp.architecture.roomdatabase;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.carematix.twiliochatapp.architecture.table.UserChat;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UserChatDao_Impl implements UserChatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserChat> __insertionAdapterOfUserChat;

  private final EntityDeletionOrUpdateAdapter<UserChat> __deletionAdapterOfUserChat;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll_1;

  public UserChatDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserChat = new EntityInsertionAdapter<UserChat>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `User_Chat` (`id`,`chat_description`,`title`,`user_id`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserChat value) {
        stmt.bindLong(1, value.id);
        if (value.chat_description == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.chat_description);
        }
        if (value.title == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.title);
        }
        if (value.uid == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.uid);
        }
      }
    };
    this.__deletionAdapterOfUserChat = new EntityDeletionOrUpdateAdapter<UserChat>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `User_Chat` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserChat value) {
        stmt.bindLong(1, value.id);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM User_Chat";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll_1 = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM User_Chat WHERE user_id in (?)";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final UserChat users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserChat.insert(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final UserChat user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfUserChat.handle(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public void deleteAll(final String channelId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll_1.acquire();
    int _argIndex = 1;
    if (channelId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, channelId);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll_1.release(_stmt);
    }
  }

  @Override
  public LiveData<List<UserChat>> getAll(final String userIds) {
    final String _sql = "SELECT * FROM User_Chat WHERE user_id IN (?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_Chat"}, false, new Callable<List<UserChat>>() {
      @Override
      public List<UserChat> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChatDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "chat_description");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
          final List<UserChat> _result = new ArrayList<UserChat>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserChat _item;
            _item = new UserChat();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.chat_description = _cursor.getString(_cursorIndexOfChatDescription);
            _item.title = _cursor.getString(_cursorIndexOfTitle);
            _item.uid = _cursor.getString(_cursorIndexOfUid);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<UserChat>> loadAllByIds(final String userIds) {
    final String _sql = "SELECT * FROM User_Chat WHERE user_id IN (?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_Chat"}, false, new Callable<List<UserChat>>() {
      @Override
      public List<UserChat> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChatDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "chat_description");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
          final List<UserChat> _result = new ArrayList<UserChat>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserChat _item;
            _item = new UserChat();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            _item.chat_description = _cursor.getString(_cursorIndexOfChatDescription);
            _item.title = _cursor.getString(_cursorIndexOfTitle);
            _item.uid = _cursor.getString(_cursorIndexOfUid);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }
}
