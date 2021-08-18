package com.carematix.twiliochatapp.architecture.roomdatabase;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserChatDao _userChatDao;

  private volatile UserListDao _userListDao;

  private volatile ChannelListDao _channelListDao;

  private volatile UserChannelDao _userChannelDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `User_Chat` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `chat_description` TEXT, `title` TEXT, `user_id` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `User_List` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `droUserId` INTEGER NOT NULL, `droUserRoleId` INTEGER NOT NULL, `firstName` TEXT, `lastName` TEXT, `droProgramUserId` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `User_Channel_List` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `organizerProgramUserId` TEXT, `attendeeProgramUserId` TEXT, `channelSid` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Channel_list` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sid` TEXT, `friendlyName` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f5103d7c385e62cf4467b871277afc5d')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `User_Chat`");
        _db.execSQL("DROP TABLE IF EXISTS `User_List`");
        _db.execSQL("DROP TABLE IF EXISTS `User_Channel_List`");
        _db.execSQL("DROP TABLE IF EXISTS `Channel_list`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsUserChat = new HashMap<String, TableInfo.Column>(4);
        _columnsUserChat.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChat.put("chat_description", new TableInfo.Column("chat_description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChat.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChat.put("user_id", new TableInfo.Column("user_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserChat = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserChat = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserChat = new TableInfo("User_Chat", _columnsUserChat, _foreignKeysUserChat, _indicesUserChat);
        final TableInfo _existingUserChat = TableInfo.read(_db, "User_Chat");
        if (! _infoUserChat.equals(_existingUserChat)) {
          return new RoomOpenHelper.ValidationResult(false, "User_Chat(com.carematix.twiliochatapp.architecture.table.UserChat).\n"
                  + " Expected:\n" + _infoUserChat + "\n"
                  + " Found:\n" + _existingUserChat);
        }
        final HashMap<String, TableInfo.Column> _columnsUserList = new HashMap<String, TableInfo.Column>(6);
        _columnsUserList.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserList.put("droUserId", new TableInfo.Column("droUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserList.put("droUserRoleId", new TableInfo.Column("droUserRoleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserList.put("firstName", new TableInfo.Column("firstName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserList.put("lastName", new TableInfo.Column("lastName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserList.put("droProgramUserId", new TableInfo.Column("droProgramUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserList = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserList = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserList = new TableInfo("User_List", _columnsUserList, _foreignKeysUserList, _indicesUserList);
        final TableInfo _existingUserList = TableInfo.read(_db, "User_List");
        if (! _infoUserList.equals(_existingUserList)) {
          return new RoomOpenHelper.ValidationResult(false, "User_List(com.carematix.twiliochatapp.architecture.table.UserAllList).\n"
                  + " Expected:\n" + _infoUserList + "\n"
                  + " Found:\n" + _existingUserList);
        }
        final HashMap<String, TableInfo.Column> _columnsUserChannelList = new HashMap<String, TableInfo.Column>(4);
        _columnsUserChannelList.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChannelList.put("organizerProgramUserId", new TableInfo.Column("organizerProgramUserId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChannelList.put("attendeeProgramUserId", new TableInfo.Column("attendeeProgramUserId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserChannelList.put("channelSid", new TableInfo.Column("channelSid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserChannelList = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserChannelList = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserChannelList = new TableInfo("User_Channel_List", _columnsUserChannelList, _foreignKeysUserChannelList, _indicesUserChannelList);
        final TableInfo _existingUserChannelList = TableInfo.read(_db, "User_Channel_List");
        if (! _infoUserChannelList.equals(_existingUserChannelList)) {
          return new RoomOpenHelper.ValidationResult(false, "User_Channel_List(com.carematix.twiliochatapp.architecture.table.ChannelList).\n"
                  + " Expected:\n" + _infoUserChannelList + "\n"
                  + " Found:\n" + _existingUserChannelList);
        }
        final HashMap<String, TableInfo.Column> _columnsChannelList = new HashMap<String, TableInfo.Column>(3);
        _columnsChannelList.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannelList.put("sid", new TableInfo.Column("sid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannelList.put("friendlyName", new TableInfo.Column("friendlyName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChannelList = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChannelList = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChannelList = new TableInfo("Channel_list", _columnsChannelList, _foreignKeysChannelList, _indicesChannelList);
        final TableInfo _existingChannelList = TableInfo.read(_db, "Channel_list");
        if (! _infoChannelList.equals(_existingChannelList)) {
          return new RoomOpenHelper.ValidationResult(false, "Channel_list(com.carematix.twiliochatapp.architecture.table.UserChannelList).\n"
                  + " Expected:\n" + _infoChannelList + "\n"
                  + " Found:\n" + _existingChannelList);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f5103d7c385e62cf4467b871277afc5d", "2029bab4c955de3946d1fae04fe30b5f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "User_Chat","User_List","User_Channel_List","Channel_list");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `User_Chat`");
      _db.execSQL("DELETE FROM `User_List`");
      _db.execSQL("DELETE FROM `User_Channel_List`");
      _db.execSQL("DELETE FROM `Channel_list`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public UserChatDao userChatDao() {
    if (_userChatDao != null) {
      return _userChatDao;
    } else {
      synchronized(this) {
        if(_userChatDao == null) {
          _userChatDao = new UserChatDao_Impl(this);
        }
        return _userChatDao;
      }
    }
  }

  @Override
  public UserListDao userListDao() {
    if (_userListDao != null) {
      return _userListDao;
    } else {
      synchronized(this) {
        if(_userListDao == null) {
          _userListDao = new UserListDao_Impl(this);
        }
        return _userListDao;
      }
    }
  }

  @Override
  public ChannelListDao channelListDao() {
    if (_channelListDao != null) {
      return _channelListDao;
    } else {
      synchronized(this) {
        if(_channelListDao == null) {
          _channelListDao = new ChannelListDao_Impl(this);
        }
        return _channelListDao;
      }
    }
  }

  @Override
  public UserChannelDao userChannelDao() {
    if (_userChannelDao != null) {
      return _userChannelDao;
    } else {
      synchronized(this) {
        if(_userChannelDao == null) {
          _userChannelDao = new UserChannelDao_Impl(this);
        }
        return _userChannelDao;
      }
    }
  }
}
