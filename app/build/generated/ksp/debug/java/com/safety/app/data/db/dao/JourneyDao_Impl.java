package com.safety.app.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safety.app.data.db.entities.Journey;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class JourneyDao_Impl implements JourneyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Journey> __insertionAdapterOfJourney;

  private final EntityDeletionOrUpdateAdapter<Journey> __updateAdapterOfJourney;

  public JourneyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJourney = new EntityInsertionAdapter<Journey>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `journeys` (`journeyId`,`userId`,`destinationName`,`destinationLat`,`destinationLng`,`startTime`,`expectedArrivalTime`,`currentEta`,`trafficDuration`,`normalDuration`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Journey entity) {
        statement.bindLong(1, entity.getJourneyId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDestinationName());
        statement.bindDouble(4, entity.getDestinationLat());
        statement.bindDouble(5, entity.getDestinationLng());
        statement.bindLong(6, entity.getStartTime());
        statement.bindLong(7, entity.getExpectedArrivalTime());
        statement.bindLong(8, entity.getCurrentEta());
        statement.bindLong(9, entity.getTrafficDuration());
        statement.bindLong(10, entity.getNormalDuration());
        statement.bindString(11, entity.getStatus());
      }
    };
    this.__updateAdapterOfJourney = new EntityDeletionOrUpdateAdapter<Journey>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `journeys` SET `journeyId` = ?,`userId` = ?,`destinationName` = ?,`destinationLat` = ?,`destinationLng` = ?,`startTime` = ?,`expectedArrivalTime` = ?,`currentEta` = ?,`trafficDuration` = ?,`normalDuration` = ?,`status` = ? WHERE `journeyId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Journey entity) {
        statement.bindLong(1, entity.getJourneyId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getDestinationName());
        statement.bindDouble(4, entity.getDestinationLat());
        statement.bindDouble(5, entity.getDestinationLng());
        statement.bindLong(6, entity.getStartTime());
        statement.bindLong(7, entity.getExpectedArrivalTime());
        statement.bindLong(8, entity.getCurrentEta());
        statement.bindLong(9, entity.getTrafficDuration());
        statement.bindLong(10, entity.getNormalDuration());
        statement.bindString(11, entity.getStatus());
        statement.bindLong(12, entity.getJourneyId());
      }
    };
  }

  @Override
  public Object insertJourney(final Journey journey, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfJourney.insertAndReturnId(journey);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateJourney(final Journey journey, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfJourney.handle(journey);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<Journey> getJourney(final long id) {
    final String _sql = "SELECT * FROM journeys WHERE journeyId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"journeys"}, false, new Callable<Journey>() {
      @Override
      @Nullable
      public Journey call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDestinationName = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationName");
          final int _cursorIndexOfDestinationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationLat");
          final int _cursorIndexOfDestinationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationLng");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfExpectedArrivalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedArrivalTime");
          final int _cursorIndexOfCurrentEta = CursorUtil.getColumnIndexOrThrow(_cursor, "currentEta");
          final int _cursorIndexOfTrafficDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "trafficDuration");
          final int _cursorIndexOfNormalDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "normalDuration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final Journey _result;
          if (_cursor.moveToFirst()) {
            final long _tmpJourneyId;
            _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpDestinationName;
            _tmpDestinationName = _cursor.getString(_cursorIndexOfDestinationName);
            final double _tmpDestinationLat;
            _tmpDestinationLat = _cursor.getDouble(_cursorIndexOfDestinationLat);
            final double _tmpDestinationLng;
            _tmpDestinationLng = _cursor.getDouble(_cursorIndexOfDestinationLng);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpExpectedArrivalTime;
            _tmpExpectedArrivalTime = _cursor.getLong(_cursorIndexOfExpectedArrivalTime);
            final long _tmpCurrentEta;
            _tmpCurrentEta = _cursor.getLong(_cursorIndexOfCurrentEta);
            final long _tmpTrafficDuration;
            _tmpTrafficDuration = _cursor.getLong(_cursorIndexOfTrafficDuration);
            final long _tmpNormalDuration;
            _tmpNormalDuration = _cursor.getLong(_cursorIndexOfNormalDuration);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new Journey(_tmpJourneyId,_tmpUserId,_tmpDestinationName,_tmpDestinationLat,_tmpDestinationLng,_tmpStartTime,_tmpExpectedArrivalTime,_tmpCurrentEta,_tmpTrafficDuration,_tmpNormalDuration,_tmpStatus);
          } else {
            _result = null;
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
  public Object getActiveJourney(final Continuation<? super Journey> $completion) {
    final String _sql = "SELECT * FROM journeys WHERE status = 'active' ORDER BY startTime DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Journey>() {
      @Override
      @Nullable
      public Journey call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfJourneyId = CursorUtil.getColumnIndexOrThrow(_cursor, "journeyId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDestinationName = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationName");
          final int _cursorIndexOfDestinationLat = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationLat");
          final int _cursorIndexOfDestinationLng = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationLng");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfExpectedArrivalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "expectedArrivalTime");
          final int _cursorIndexOfCurrentEta = CursorUtil.getColumnIndexOrThrow(_cursor, "currentEta");
          final int _cursorIndexOfTrafficDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "trafficDuration");
          final int _cursorIndexOfNormalDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "normalDuration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final Journey _result;
          if (_cursor.moveToFirst()) {
            final long _tmpJourneyId;
            _tmpJourneyId = _cursor.getLong(_cursorIndexOfJourneyId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpDestinationName;
            _tmpDestinationName = _cursor.getString(_cursorIndexOfDestinationName);
            final double _tmpDestinationLat;
            _tmpDestinationLat = _cursor.getDouble(_cursorIndexOfDestinationLat);
            final double _tmpDestinationLng;
            _tmpDestinationLng = _cursor.getDouble(_cursorIndexOfDestinationLng);
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpExpectedArrivalTime;
            _tmpExpectedArrivalTime = _cursor.getLong(_cursorIndexOfExpectedArrivalTime);
            final long _tmpCurrentEta;
            _tmpCurrentEta = _cursor.getLong(_cursorIndexOfCurrentEta);
            final long _tmpTrafficDuration;
            _tmpTrafficDuration = _cursor.getLong(_cursorIndexOfTrafficDuration);
            final long _tmpNormalDuration;
            _tmpNormalDuration = _cursor.getLong(_cursorIndexOfNormalDuration);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new Journey(_tmpJourneyId,_tmpUserId,_tmpDestinationName,_tmpDestinationLat,_tmpDestinationLng,_tmpStartTime,_tmpExpectedArrivalTime,_tmpCurrentEta,_tmpTrafficDuration,_tmpNormalDuration,_tmpStatus);
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
