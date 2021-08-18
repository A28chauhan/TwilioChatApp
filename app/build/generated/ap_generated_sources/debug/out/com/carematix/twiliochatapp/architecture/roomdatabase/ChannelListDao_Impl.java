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
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ChannelListDao_Impl implements ChannelListDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChannelList> __insertionAdapterOfChannelList;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public ChannelListDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChannelList = new EntityInsertionAdapter<ChannelList>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR IGNORE INTO `User_Channel_List` (`id`,`organizerProgramUserId`,`attendeeProgramUserId`,`channelSid`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ChannelList value) {
        stmt.bindLong(1, value.id);
        if (value.getOrganizerProgramUserId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getOrganizerProgramUserId());
        }
        if (value.getAttendeeProgramUserId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getAttendeeProgramUserId());
        }
        if (value.getChannelSid() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getChannelSid());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM User_Channel_List";
        return _query;
      }
    };
  }

  @Override
  public void insert(final ChannelList channelList) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfChannelList.insert(channelList);
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
  public LiveData<List<ChannelList>> getAllChannel() {
    final String _sql = "SELECT * FROM User_Channel_List";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_Channel_List"}, false, new Callable<List<ChannelList>>() {
      @Override
      public List<ChannelList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOrganizerProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizerProgramUserId");
          final int _cursorIndexOfAttendeeProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "attendeeProgramUserId");
          final int _cursorIndexOfChannelSid = CursorUtil.getColumnIndexOrThrow(_cursor, "channelSid");
          final List<ChannelList> _result = new ArrayList<ChannelList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ChannelList _item;
            final String _tmpOrganizerProgramUserId;
            _tmpOrganizerProgramUserId = _cursor.getString(_cursorIndexOfOrganizerProgramUserId);
            final String _tmpAttendeeProgramUserId;
            _tmpAttendeeProgramUserId = _cursor.getString(_cursorIndexOfAttendeeProgramUserId);
            final String _tmpChannelSid;
            _tmpChannelSid = _cursor.getString(_cursorIndexOfChannelSid);
            _item = new ChannelList(_tmpOrganizerProgramUserId,_tmpAttendeeProgramUserId,_tmpChannelSid);
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
  public LiveData<List<ChannelList>> getChannelDetails(final String programUserID,
      final String attendeeProgramUserId) {
    final String _sql = "SELECT * FROM User_Channel_List WHERE organizerProgramUserId IN (?) AND attendeeProgramUserId IN (?) ORDER BY id ASC ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (programUserID == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, programUserID);
    }
    _argIndex = 2;
    if (attendeeProgramUserId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, attendeeProgramUserId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"User_Channel_List"}, false, new Callable<List<ChannelList>>() {
      @Override
      public List<ChannelList> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOrganizerProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizerProgramUserId");
          final int _cursorIndexOfAttendeeProgramUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "attendeeProgramUserId");
          final int _cursorIndexOfChannelSid = CursorUtil.getColumnIndexOrThrow(_cursor, "channelSid");
          final List<ChannelList> _result = new ArrayList<ChannelList>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ChannelList _item;
            final String _tmpOrganizerProgramUserId;
            _tmpOrganizerProgramUserId = _cursor.getString(_cursorIndexOfOrganizerProgramUserId);
            final String _tmpAttendeeProgramUserId;
            _tmpAttendeeProgramUserId = _cursor.getString(_cursorIndexOfAttendeeProgramUserId);
            final String _tmpChannelSid;
            _tmpChannelSid = _cursor.getString(_cursorIndexOfChannelSid);
            _item = new ChannelList(_tmpOrganizerProgramUserId,_tmpAttendeeProgramUserId,_tmpChannelSid);
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
