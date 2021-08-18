package com.carematix.twiliochatapp.architecture.roomdatabase;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UserListDao_Impl implements UserListDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserAllList> __insertionAdapterOfUserAllList;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserListDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserAllList = new EntityInsertionAdapter<UserAllList>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `User_List` (`id`,`droUserId`,`droUserRoleId`,`firstName`,`lastName`,`droProgramUserId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, UserAllList value) {
        stmt.bindLong(1, value.id);
        stmt.bindLong(2, value.getDroUserId());
        stmt.bindLong(3, value.getDroUserRoleId());
        if (value.getFirstName() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFirstName());
        }
        if (value.getLastName() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getLastName());
        }
        stmt.bindLong(6, value.getDroProgramUserId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM User_List";
        return _query;
      }
    };
  }

  @Override
  public void insert(final UserAllList users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserAllList.insert(users);
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
  public LiveData<List<UserAllList>> getAllList() {
    final String _sql = "SELECT * FROM User_List";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_List"}, false, new Callable<List<UserAllList>>() {
      @Override
      public List<UserAllList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDroUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "droUserId");
          final int _cursorIndexOfDroUserRoleId = CursorUtil.getColumnIndexOrThrow(_cursor, "droUserRoleId");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDroProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "droProgramUserId");
          final List<UserAllList> _result = new ArrayList<UserAllList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserAllList _item;
            final int _tmpDroUserId;
            _tmpDroUserId = _cursor.getInt(_cursorIndexOfDroUserId);
            final int _tmpDroUserRoleId;
            _tmpDroUserRoleId = _cursor.getInt(_cursorIndexOfDroUserRoleId);
            final String _tmpFirstName;
            _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            final String _tmpLastName;
            _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            final int _tmpDroProgramUserId;
            _tmpDroProgramUserId = _cursor.getInt(_cursorIndexOfDroProgramUserId);
            _item = new UserAllList(_tmpDroUserId,_tmpDroUserRoleId,_tmpFirstName,_tmpLastName,_tmpDroProgramUserId);
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
  public LiveData<List<UserAllList>> getUserByID(final String programUserId) {
    final String _sql = "SELECT * FROM User_List WHERE droProgramUserId IN (?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (programUserId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, programUserId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_List"}, false, new Callable<List<UserAllList>>() {
      @Override
      public List<UserAllList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDroUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "droUserId");
          final int _cursorIndexOfDroUserRoleId = CursorUtil.getColumnIndexOrThrow(_cursor, "droUserRoleId");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDroProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "droProgramUserId");
          final List<UserAllList> _result = new ArrayList<UserAllList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final UserAllList _item;
            final int _tmpDroUserId;
            _tmpDroUserId = _cursor.getInt(_cursorIndexOfDroUserId);
            final int _tmpDroUserRoleId;
            _tmpDroUserRoleId = _cursor.getInt(_cursorIndexOfDroUserRoleId);
            final String _tmpFirstName;
            _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            final String _tmpLastName;
            _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            final int _tmpDroProgramUserId;
            _tmpDroProgramUserId = _cursor.getInt(_cursorIndexOfDroProgramUserId);
            _item = new UserAllList(_tmpDroUserId,_tmpDroUserRoleId,_tmpFirstName,_tmpLastName,_tmpDroProgramUserId);
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
}
