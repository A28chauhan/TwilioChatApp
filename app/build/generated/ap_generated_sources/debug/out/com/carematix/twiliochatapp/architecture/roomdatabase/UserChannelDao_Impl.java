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
import com.carematix.twiliochatapp.architecture.table.UserChannelList;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UserChannelDao_Impl implements UserChannelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserChannelList> __insertionAdapterOfUserChannelList;

  private final EntityDeletionOrUpdateAdapter<UserChannelList> __deletionAdapterOfUserChannelList;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserChannelDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserChannelList = new EntityInsertionAdapter<UserChannelList>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Channel_list` (`id`,`sid`,`friendlyName`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserChannelList value) {
        stmt.bindLong(1, value.id);
        if (value.sid == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.sid);
        }
        if (value.friendlyName == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.friendlyName);
        }
      }
    };
    this.__deletionAdapterOfUserChannelList = new EntityDeletionOrUpdateAdapter<UserChannelList>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Channel_list` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserChannelList value) {
        stmt.bindLong(1, value.id);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Channel_list";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final UserChannelList users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserChannelList.insert(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final UserChannelList user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfUserChannelList.handle(user);
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
  public LiveData<List<UserChannelList>> getAll(final String userIds, final String userName) {
    final String _sql = "SELECT * FROM Channel_list WHERE sid IN (?) or friendlyName IN (?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    _argIndex = 2;
    if (userName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userName);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"Channel_list"}, false, new Callable<List<UserChannelList>>() {
      @Override
      public List<UserChannelList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSid = CursorUtil.getColumnIndexOrThrow(_cursor, "sid");
          final int _cursorIndexOfFriendlyName = CursorUtil.getColumnIndexOrThrow(_cursor, "friendlyName");
          final List<UserChannelList> _result = new ArrayList<UserChannelList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserChannelList _item;
            final String _tmpSid;
            _tmpSid = _cursor.getString(_cursorIndexOfSid);
            final String _tmpFriendlyName;
            _tmpFriendlyName = _cursor.getString(_cursorIndexOfFriendlyName);
            _item = new UserChannelList(_tmpSid,_tmpFriendlyName);
            _item.id = _cursor.getInt(_cursorIndexOfId);
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
  public LiveData<List<UserChannelList>> loadAllByIds(final String userIds) {
    final String _sql = "SELECT * FROM Channel_list WHERE friendlyName IN (?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"Channel_list"}, false, new Callable<List<UserChannelList>>() {
      @Override
      public List<UserChannelList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSid = CursorUtil.getColumnIndexOrThrow(_cursor, "sid");
          final int _cursorIndexOfFriendlyName = CursorUtil.getColumnIndexOrThrow(_cursor, "friendlyName");
          final List<UserChannelList> _result = new ArrayList<UserChannelList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserChannelList _item;
            final String _tmpSid;
            _tmpSid = _cursor.getString(_cursorIndexOfSid);
            final String _tmpFriendlyName;
            _tmpFriendlyName = _cursor.getString(_cursorIndexOfFriendlyName);
            _item = new UserChannelList(_tmpSid,_tmpFriendlyName);
            _item.id = _cursor.getInt(_cursorIndexOfId);
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
  public LiveData<List<UserChannelList>> loadDataLikeByIds(final String userIds) {
    final String _sql = "SELECT * FROM Channel_list WHERE sid LIKE (?)  ORDER BY friendlyName DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"Channel_list"}, false, new Callable<List<UserChannelList>>() {
      @Override
      public List<UserChannelList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSid = CursorUtil.getColumnIndexOrThrow(_cursor, "sid");
          final int _cursorIndexOfFriendlyName = CursorUtil.getColumnIndexOrThrow(_cursor, "friendlyName");
          final List<UserChannelList> _result = new ArrayList<UserChannelList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserChannelList _item;
            final String _tmpSid;
            _tmpSid = _cursor.getString(_cursorIndexOfSid);
            final String _tmpFriendlyName;
            _tmpFriendlyName = _cursor.getString(_cursorIndexOfFriendlyName);
            _item = new UserChannelList(_tmpSid,_tmpFriendlyName);
            _item.id = _cursor.getInt(_cursorIndexOfId);
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
  public List<UserChannelList> loadDataLikeByIdsA(final String userIds) {
    final String _sql = "SELECT * FROM Channel_list WHERE friendlyName LIKE (?) ORDER BY friendlyName DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userIds == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userIds);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfSid = CursorUtil.getColumnIndexOrThrow(_cursor, "sid");
      final int _cursorIndexOfFriendlyName = CursorUtil.getColumnIndexOrThrow(_cursor, "friendlyName");
      final List<UserChannelList> _result = new ArrayList<UserChannelList>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final UserChannelList _item;
        final String _tmpSid;
        _tmpSid = _cursor.getString(_cursorIndexOfSid);
        final String _tmpFriendlyName;
        _tmpFriendlyName = _cursor.getString(_cursorIndexOfFriendlyName);
        _item = new UserChannelList(_tmpSid,_tmpFriendlyName);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
