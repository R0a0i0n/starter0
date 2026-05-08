package com.example.executionapp.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GoalDao_Impl implements GoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Goal> __insertionAdapterOfGoal;

  private final EntityDeletionOrUpdateAdapter<Goal> __updateAdapterOfGoal;

  public GoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGoal = new EntityInsertionAdapter<Goal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `goals` (`id`,`name`,`currentAction`,`resistance`,`createdAt`,`isCompleted`,`preInput`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Goal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCurrentAction());
        statement.bindString(4, entity.getResistance());
        statement.bindLong(5, entity.getCreatedAt());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getPreInput());
      }
    };
    this.__updateAdapterOfGoal = new EntityDeletionOrUpdateAdapter<Goal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `goals` SET `id` = ?,`name` = ?,`currentAction` = ?,`resistance` = ?,`createdAt` = ?,`isCompleted` = ?,`preInput` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Goal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCurrentAction());
        statement.bindString(4, entity.getResistance());
        statement.bindLong(5, entity.getCreatedAt());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getPreInput());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertGoal(final Goal goal, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGoal.insertAndReturnId(goal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateGoal(final Goal goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGoal.handle(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Goal>> getAllGoals() {
    final String _sql = "SELECT * FROM goals ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"goals"}, new Callable<List<Goal>>() {
      @Override
      @NonNull
      public List<Goal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCurrentAction = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAction");
          final int _cursorIndexOfResistance = CursorUtil.getColumnIndexOrThrow(_cursor, "resistance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfPreInput = CursorUtil.getColumnIndexOrThrow(_cursor, "preInput");
          final List<Goal> _result = new ArrayList<Goal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Goal _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCurrentAction;
            _tmpCurrentAction = _cursor.getString(_cursorIndexOfCurrentAction);
            final String _tmpResistance;
            _tmpResistance = _cursor.getString(_cursorIndexOfResistance);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPreInput;
            _tmpPreInput = _cursor.getString(_cursorIndexOfPreInput);
            _item = new Goal(_tmpId,_tmpName,_tmpCurrentAction,_tmpResistance,_tmpCreatedAt,_tmpIsCompleted,_tmpPreInput);
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
  public Object getLatestIncompleteGoal(final Continuation<? super Goal> $completion) {
    final String _sql = "SELECT * FROM goals WHERE isCompleted = 0 ORDER BY createdAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Goal>() {
      @Override
      @Nullable
      public Goal call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCurrentAction = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAction");
          final int _cursorIndexOfResistance = CursorUtil.getColumnIndexOrThrow(_cursor, "resistance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfPreInput = CursorUtil.getColumnIndexOrThrow(_cursor, "preInput");
          final Goal _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCurrentAction;
            _tmpCurrentAction = _cursor.getString(_cursorIndexOfCurrentAction);
            final String _tmpResistance;
            _tmpResistance = _cursor.getString(_cursorIndexOfResistance);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPreInput;
            _tmpPreInput = _cursor.getString(_cursorIndexOfPreInput);
            _result = new Goal(_tmpId,_tmpName,_tmpCurrentAction,_tmpResistance,_tmpCreatedAt,_tmpIsCompleted,_tmpPreInput);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getGoalById(final long goalId, final Continuation<? super Goal> $completion) {
    final String _sql = "SELECT * FROM goals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, goalId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Goal>() {
      @Override
      @Nullable
      public Goal call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCurrentAction = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAction");
          final int _cursorIndexOfResistance = CursorUtil.getColumnIndexOrThrow(_cursor, "resistance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfPreInput = CursorUtil.getColumnIndexOrThrow(_cursor, "preInput");
          final Goal _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCurrentAction;
            _tmpCurrentAction = _cursor.getString(_cursorIndexOfCurrentAction);
            final String _tmpResistance;
            _tmpResistance = _cursor.getString(_cursorIndexOfResistance);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpPreInput;
            _tmpPreInput = _cursor.getString(_cursorIndexOfPreInput);
            _result = new Goal(_tmpId,_tmpName,_tmpCurrentAction,_tmpResistance,_tmpCreatedAt,_tmpIsCompleted,_tmpPreInput);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
