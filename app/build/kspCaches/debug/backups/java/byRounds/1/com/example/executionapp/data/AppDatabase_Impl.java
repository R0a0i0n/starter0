package com.example.executionapp.data;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile GoalDao _goalDao;

  private volatile StepDao _stepDao;

  private volatile PendingRequestDao _pendingRequestDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `currentAction` TEXT NOT NULL, `resistance` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `preInput` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `steps` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `goalId` INTEGER NOT NULL, `stepNumber` INTEGER NOT NULL, `content` TEXT NOT NULL, `status` TEXT NOT NULL, `completedAt` INTEGER NOT NULL, `durationMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_requests` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `goalId` INTEGER NOT NULL, `stepNumber` INTEGER NOT NULL, `requestType` TEXT NOT NULL, `payload` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b16966d0e18ccfacc31493efaaa610e0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `goals`");
        db.execSQL("DROP TABLE IF EXISTS `steps`");
        db.execSQL("DROP TABLE IF EXISTS `pending_requests`");
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
        final HashMap<String, TableInfo.Column> _columnsGoals = new HashMap<String, TableInfo.Column>(7);
        _columnsGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("currentAction", new TableInfo.Column("currentAction", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("resistance", new TableInfo.Column("resistance", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("preInput", new TableInfo.Column("preInput", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGoals = new TableInfo("goals", _columnsGoals, _foreignKeysGoals, _indicesGoals);
        final TableInfo _existingGoals = TableInfo.read(db, "goals");
        if (!_infoGoals.equals(_existingGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "goals(com.example.executionapp.data.Goal).\n"
                  + " Expected:\n" + _infoGoals + "\n"
                  + " Found:\n" + _existingGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsSteps = new HashMap<String, TableInfo.Column>(7);
        _columnsSteps.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("goalId", new TableInfo.Column("goalId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("stepNumber", new TableInfo.Column("stepNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSteps.put("durationMillis", new TableInfo.Column("durationMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSteps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSteps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSteps = new TableInfo("steps", _columnsSteps, _foreignKeysSteps, _indicesSteps);
        final TableInfo _existingSteps = TableInfo.read(db, "steps");
        if (!_infoSteps.equals(_existingSteps)) {
          return new RoomOpenHelper.ValidationResult(false, "steps(com.example.executionapp.data.Step).\n"
                  + " Expected:\n" + _infoSteps + "\n"
                  + " Found:\n" + _existingSteps);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingRequests = new HashMap<String, TableInfo.Column>(5);
        _columnsPendingRequests.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingRequests.put("goalId", new TableInfo.Column("goalId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingRequests.put("stepNumber", new TableInfo.Column("stepNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingRequests.put("requestType", new TableInfo.Column("requestType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingRequests.put("payload", new TableInfo.Column("payload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingRequests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingRequests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingRequests = new TableInfo("pending_requests", _columnsPendingRequests, _foreignKeysPendingRequests, _indicesPendingRequests);
        final TableInfo _existingPendingRequests = TableInfo.read(db, "pending_requests");
        if (!_infoPendingRequests.equals(_existingPendingRequests)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_requests(com.example.executionapp.data.PendingRequest).\n"
                  + " Expected:\n" + _infoPendingRequests + "\n"
                  + " Found:\n" + _existingPendingRequests);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b16966d0e18ccfacc31493efaaa610e0", "2a4ad52f1dfe273517fb102f6e6c3378");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "goals","steps","pending_requests");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `goals`");
      _db.execSQL("DELETE FROM `steps`");
      _db.execSQL("DELETE FROM `pending_requests`");
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
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(GoalDao.class, GoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StepDao.class, StepDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingRequestDao.class, PendingRequestDao_Impl.getRequiredConverters());
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
  public GoalDao goalDao() {
    if (_goalDao != null) {
      return _goalDao;
    } else {
      synchronized(this) {
        if(_goalDao == null) {
          _goalDao = new GoalDao_Impl(this);
        }
        return _goalDao;
      }
    }
  }

  @Override
  public StepDao stepDao() {
    if (_stepDao != null) {
      return _stepDao;
    } else {
      synchronized(this) {
        if(_stepDao == null) {
          _stepDao = new StepDao_Impl(this);
        }
        return _stepDao;
      }
    }
  }

  @Override
  public PendingRequestDao pendingRequestDao() {
    if (_pendingRequestDao != null) {
      return _pendingRequestDao;
    } else {
      synchronized(this) {
        if(_pendingRequestDao == null) {
          _pendingRequestDao = new PendingRequestDao_Impl(this);
        }
        return _pendingRequestDao;
      }
    }
  }
}
