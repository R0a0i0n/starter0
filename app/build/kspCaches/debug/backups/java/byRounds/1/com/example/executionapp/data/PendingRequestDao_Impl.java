package com.example.executionapp.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PendingRequestDao_Impl implements PendingRequestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingRequest> __insertionAdapterOfPendingRequest;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRequest;

  public PendingRequestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPendingRequest = new EntityInsertionAdapter<PendingRequest>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_requests` (`id`,`goalId`,`stepNumber`,`requestType`,`payload`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PendingRequest entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGoalId());
        statement.bindLong(3, entity.getStepNumber());
        statement.bindString(4, entity.getRequestType());
        statement.bindString(5, entity.getPayload());
      }
    };
    this.__preparedStmtOfDeleteRequest = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_requests WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRequest(final PendingRequest request,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPendingRequest.insert(request);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRequest(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRequest.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRequest.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllRequests(final Continuation<? super List<PendingRequest>> $completion) {
    final String _sql = "SELECT * FROM pending_requests ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PendingRequest>>() {
      @Override
      @NonNull
      public List<PendingRequest> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGoalId = CursorUtil.getColumnIndexOrThrow(_cursor, "goalId");
          final int _cursorIndexOfStepNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "stepNumber");
          final int _cursorIndexOfRequestType = CursorUtil.getColumnIndexOrThrow(_cursor, "requestType");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final List<PendingRequest> _result = new ArrayList<PendingRequest>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PendingRequest _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGoalId;
            _tmpGoalId = _cursor.getLong(_cursorIndexOfGoalId);
            final int _tmpStepNumber;
            _tmpStepNumber = _cursor.getInt(_cursorIndexOfStepNumber);
            final String _tmpRequestType;
            _tmpRequestType = _cursor.getString(_cursorIndexOfRequestType);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            _item = new PendingRequest(_tmpId,_tmpGoalId,_tmpStepNumber,_tmpRequestType,_tmpPayload);
            _result.add(_item);
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
