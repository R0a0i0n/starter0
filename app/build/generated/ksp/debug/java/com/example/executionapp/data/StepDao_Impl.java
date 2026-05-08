package com.example.executionapp.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
import java.lang.IllegalArgumentException;
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
public final class StepDao_Impl implements StepDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Step> __insertionAdapterOfStep;

  private final EntityDeletionOrUpdateAdapter<Step> __updateAdapterOfStep;

  public StepDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStep = new EntityInsertionAdapter<Step>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `steps` (`id`,`goalId`,`stepNumber`,`content`,`status`,`completedAt`,`durationMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Step entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGoalId());
        statement.bindLong(3, entity.getStepNumber());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, __StepStatus_enumToString(entity.getStatus()));
        statement.bindLong(6, entity.getCompletedAt());
        statement.bindLong(7, entity.getDurationMillis());
      }
    };
    this.__updateAdapterOfStep = new EntityDeletionOrUpdateAdapter<Step>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `steps` SET `id` = ?,`goalId` = ?,`stepNumber` = ?,`content` = ?,`status` = ?,`completedAt` = ?,`durationMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Step entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGoalId());
        statement.bindLong(3, entity.getStepNumber());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, __StepStatus_enumToString(entity.getStatus()));
        statement.bindLong(6, entity.getCompletedAt());
        statement.bindLong(7, entity.getDurationMillis());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insertStep(final Step step, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfStep.insertAndReturnId(step);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStep(final Step step, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfStep.handle(step);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Step>> getStepsForGoal(final long goalId) {
    final String _sql = "SELECT * FROM steps WHERE goalId = ? ORDER BY stepNumber ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, goalId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"steps"}, new Callable<List<Step>>() {
      @Override
      @NonNull
      public List<Step> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGoalId = CursorUtil.getColumnIndexOrThrow(_cursor, "goalId");
          final int _cursorIndexOfStepNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "stepNumber");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMillis");
          final List<Step> _result = new ArrayList<Step>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Step _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGoalId;
            _tmpGoalId = _cursor.getLong(_cursorIndexOfGoalId);
            final int _tmpStepNumber;
            _tmpStepNumber = _cursor.getInt(_cursorIndexOfStepNumber);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final StepStatus _tmpStatus;
            _tmpStatus = __StepStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final long _tmpDurationMillis;
            _tmpDurationMillis = _cursor.getLong(_cursorIndexOfDurationMillis);
            _item = new Step(_tmpId,_tmpGoalId,_tmpStepNumber,_tmpContent,_tmpStatus,_tmpCompletedAt,_tmpDurationMillis);
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
  public Flow<List<Step>> getCompletedStepsForGoal(final long goalId) {
    final String _sql = "SELECT * FROM steps WHERE goalId = ? AND status = 'COMPLETED' ORDER BY completedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, goalId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"steps"}, new Callable<List<Step>>() {
      @Override
      @NonNull
      public List<Step> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGoalId = CursorUtil.getColumnIndexOrThrow(_cursor, "goalId");
          final int _cursorIndexOfStepNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "stepNumber");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMillis");
          final List<Step> _result = new ArrayList<Step>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Step _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGoalId;
            _tmpGoalId = _cursor.getLong(_cursorIndexOfGoalId);
            final int _tmpStepNumber;
            _tmpStepNumber = _cursor.getInt(_cursorIndexOfStepNumber);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final StepStatus _tmpStatus;
            _tmpStatus = __StepStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            final long _tmpDurationMillis;
            _tmpDurationMillis = _cursor.getLong(_cursorIndexOfDurationMillis);
            _item = new Step(_tmpId,_tmpGoalId,_tmpStepNumber,_tmpContent,_tmpStatus,_tmpCompletedAt,_tmpDurationMillis);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __StepStatus_enumToString(@NonNull final StepStatus _value) {
    switch (_value) {
      case CURRENT: return "CURRENT";
      case COMPLETED: return "COMPLETED";
      case SKIPPED: return "SKIPPED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private StepStatus __StepStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "CURRENT": return StepStatus.CURRENT;
      case "COMPLETED": return StepStatus.COMPLETED;
      case "SKIPPED": return StepStatus.SKIPPED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
