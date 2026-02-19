package com.safety.app.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safety.app.data.db.entities.LocationLog;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class LocationLogDao_Impl implements LocationLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LocationLog> __insertionAdapterOfLocationLog;

  public LocationLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLocationLog = new EntityInsertionAdapter<LocationLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `location_logs` (`logId`,`journeyId`,`latitude`,`longitude`,`timestamp`,`synced`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationLog entity) {
        statement.bindLong(1, entity.getLogId());
        statement.bindLong(2, entity.getJourneyId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindLong(5, entity.getTimestamp());
        final int _tmp = entity.getSynced() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
  }

  @Override
  public Object insertLog(final LocationLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLocationLog.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUnsyncedLogs(final Continuation<? super List<LocationLog>> $completion) {
    final String _sql = "SELECT * FROM location_logs WHERE synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationLog>>() {
      @Override
      @NonNull
      public List<LocationLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLogId = CursorUtil.getColumnIndexOrThrow(_cursor, "logId");
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
          final List<LocationLog> _result = new ArrayList<LocationLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationLog _item;
            final long _tmpLogId;
            _tmpLogId = _cursor.getLong(_cursorIndexOfLogId);
            final long _tmpJourneyId;
            _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSynced);
            _tmpSynced = _tmp != 0;
            _item = new LocationLog(_tmpLogId,_tmpJourneyId,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpSynced);
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

  @Override
  public Object markAsSynced(final List<Long> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE location_logs SET synced = 1 WHERE logId IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (long _item : ids) {
          _stmt.bindLong(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
