package com.safety.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.safety.app.data.db.dao.ContactDao;
import com.safety.app.data.db.dao.ContactDao_Impl;
import com.safety.app.data.db.dao.JourneyDao;
import com.safety.app.data.db.dao.JourneyDao_Impl;
import com.safety.app.data.db.dao.LocationLogDao;
import com.safety.app.data.db.dao.LocationLogDao_Impl;
import com.safety.app.data.db.dao.UserDao;
import com.safety.app.data.db.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UserDao _userDao;

  private volatile ContactDao _contactDao;

  private volatile JourneyDao _journeyDao;

  private volatile LocationLogDao _locationLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`userId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `fullName` TEXT NOT NULL, `age` INTEGER NOT NULL, `gender` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `address` TEXT NOT NULL, `bloodGroup` TEXT, `medicalNotes` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `emergency_contacts` (`contactId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `name` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `priority` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `journeys` (`journeyId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `destinationName` TEXT NOT NULL, `destinationLat` REAL NOT NULL, `destinationLng` REAL NOT NULL, `startTime` INTEGER NOT NULL, `expectedArrivalTime` INTEGER NOT NULL, `currentEta` INTEGER NOT NULL, `trafficDuration` INTEGER NOT NULL, `normalDuration` INTEGER NOT NULL, `status` TEXT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `location_logs` (`logId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `journeyId` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `synced` INTEGER NOT NULL, FOREIGN KEY(`journeyId`) REFERENCES `journeys`(`journeyId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1a194ed18ea466369b017cc4cf2dbf42')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `emergency_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `journeys`");
        db.execSQL("DROP TABLE IF EXISTS `location_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(9);
        _columnsUsers.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("fullName", new TableInfo.Column("fullName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("bloodGroup", new TableInfo.Column("bloodGroup", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("medicalNotes", new TableInfo.Column("medicalNotes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.safety.app.data.db.entities.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsEmergencyContacts = new HashMap<String, TableInfo.Column>(6);
        _columnsEmergencyContacts.put("contactId", new TableInfo.Column("contactId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmergencyContacts = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEmergencyContacts.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final HashSet<TableInfo.Index> _indicesEmergencyContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmergencyContacts = new TableInfo("emergency_contacts", _columnsEmergencyContacts, _foreignKeysEmergencyContacts, _indicesEmergencyContacts);
        final TableInfo _existingEmergencyContacts = TableInfo.read(db, "emergency_contacts");
        if (!_infoEmergencyContacts.equals(_existingEmergencyContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "emergency_contacts(com.safety.app.data.db.entities.EmergencyContact).\n"
                  + " Expected:\n" + _infoEmergencyContacts + "\n"
                  + " Found:\n" + _existingEmergencyContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsJourneys = new HashMap<String, TableInfo.Column>(11);
        _columnsJourneys.put("journeyId", new TableInfo.Column("journeyId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("destinationName", new TableInfo.Column("destinationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("destinationLat", new TableInfo.Column("destinationLat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("destinationLng", new TableInfo.Column("destinationLng", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("expectedArrivalTime", new TableInfo.Column("expectedArrivalTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("currentEta", new TableInfo.Column("currentEta", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("trafficDuration", new TableInfo.Column("trafficDuration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("normalDuration", new TableInfo.Column("normalDuration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJourneys.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJourneys = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysJourneys.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("userId")));
        final HashSet<TableInfo.Index> _indicesJourneys = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJourneys = new TableInfo("journeys", _columnsJourneys, _foreignKeysJourneys, _indicesJourneys);
        final TableInfo _existingJourneys = TableInfo.read(db, "journeys");
        if (!_infoJourneys.equals(_existingJourneys)) {
          return new RoomOpenHelper.ValidationResult(false, "journeys(com.safety.app.data.db.entities.Journey).\n"
                  + " Expected:\n" + _infoJourneys + "\n"
                  + " Found:\n" + _existingJourneys);
        }
        final HashMap<String, TableInfo.Column> _columnsLocationLogs = new HashMap<String, TableInfo.Column>(6);
        _columnsLocationLogs.put("logId", new TableInfo.Column("logId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationLogs.put("journeyId", new TableInfo.Column("journeyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationLogs.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationLogs.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationLogs.put("synced", new TableInfo.Column("synced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLocationLogs.add(new TableInfo.ForeignKey("journeys", "CASCADE", "NO ACTION", Arrays.asList("journeyId"), Arrays.asList("journeyId")));
        final HashSet<TableInfo.Index> _indicesLocationLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocationLogs = new TableInfo("location_logs", _columnsLocationLogs, _foreignKeysLocationLogs, _indicesLocationLogs);
        final TableInfo _existingLocationLogs = TableInfo.read(db, "location_logs");
        if (!_infoLocationLogs.equals(_existingLocationLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "location_logs(com.safety.app.data.db.entities.LocationLog).\n"
                  + " Expected:\n" + _infoLocationLogs + "\n"
                  + " Found:\n" + _existingLocationLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "1a194ed18ea466369b017cc4cf2dbf42", "7a3aadae53f91c105a0798950b2b6ca7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "users","emergency_contacts","journeys","location_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `emergency_contacts`");
      _db.execSQL("DELETE FROM `journeys`");
      _db.execSQL("DELETE FROM `location_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(JourneyDao.class, JourneyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LocationLogDao.class, LocationLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }

  @Override
  public JourneyDao journeyDao() {
    if (_journeyDao != null) {
      return _journeyDao;
    } else {
      synchronized(this) {
        if(_journeyDao == null) {
          _journeyDao = new JourneyDao_Impl(this);
        }
        return _journeyDao;
      }
    }
  }

  @Override
  public LocationLogDao locationLogDao() {
    if (_locationLogDao != null) {
      return _locationLogDao;
    } else {
      synchronized(this) {
        if(_locationLogDao == null) {
          _locationLogDao = new LocationLogDao_Impl(this);
        }
        return _locationLogDao;
      }
    }
  }
}
