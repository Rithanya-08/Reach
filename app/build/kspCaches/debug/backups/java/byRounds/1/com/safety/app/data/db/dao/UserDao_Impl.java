package com.safety.app.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.safety.app.data.db.entities.User;
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
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<User> __insertionAdapterOfUser;

  private final SharedSQLiteStatement __preparedStmtOfClearUser;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUser = new EntityInsertionAdapter<User>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`userId`,`fullName`,`age`,`gender`,`phoneNumber`,`address`,`bloodGroup`,`medicalNotes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final User entity) {
        statement.bindLong(1, entity.getUserId());
        statement.bindString(2, entity.getFullName());
        statement.bindLong(3, entity.getAge());
        statement.bindString(4, entity.getGender());
        statement.bindString(5, entity.getPhoneNumber());
        statement.bindString(6, entity.getAddress());
        if (entity.getBloodGroup() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getBloodGroup());
        }
        if (entity.getMedicalNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMedicalNotes());
        }
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfClearUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users";
        return _query;
      }
    };
  }

  @Override
  public Object insertUser(final User user, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUser.insertAndReturnId(user);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearUser(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearUser.acquire();
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
          __preparedStmtOfClearUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<User> getUser() {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"users"}, false, new Callable<User>() {
      @Override
      @Nullable
      public User call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfBloodGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "bloodGroup");
          final int _cursorIndexOfMedicalNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalNotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final User _result;
          if (_cursor.moveToFirst()) {
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpBloodGroup;
            if (_cursor.isNull(_cursorIndexOfBloodGroup)) {
              _tmpBloodGroup = null;
            } else {
              _tmpBloodGroup = _cursor.getString(_cursorIndexOfBloodGroup);
            }
            final String _tmpMedicalNotes;
            if (_cursor.isNull(_cursorIndexOfMedicalNotes)) {
              _tmpMedicalNotes = null;
            } else {
              _tmpMedicalNotes = _cursor.getString(_cursorIndexOfMedicalNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new User(_tmpUserId,_tmpFullName,_tmpAge,_tmpGender,_tmpPhoneNumber,_tmpAddress,_tmpBloodGroup,_tmpMedicalNotes,_tmpCreatedAt);
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
  public Object getUserSync(final Continuation<? super User> $completion) {
    final String _sql = "SELECT * FROM users LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<User>() {
      @Override
      @Nullable
      public User call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfBloodGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "bloodGroup");
          final int _cursorIndexOfMedicalNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalNotes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final User _result;
          if (_cursor.moveToFirst()) {
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            final String _tmpGender;
            _tmpGender = _cursor.getString(_cursorIndexOfGender);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpBloodGroup;
            if (_cursor.isNull(_cursorIndexOfBloodGroup)) {
              _tmpBloodGroup = null;
            } else {
              _tmpBloodGroup = _cursor.getString(_cursorIndexOfBloodGroup);
            }
            final String _tmpMedicalNotes;
            if (_cursor.isNull(_cursorIndexOfMedicalNotes)) {
              _tmpMedicalNotes = null;
            } else {
              _tmpMedicalNotes = _cursor.getString(_cursorIndexOfMedicalNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new User(_tmpUserId,_tmpFullName,_tmpAge,_tmpGender,_tmpPhoneNumber,_tmpAddress,_tmpBloodGroup,_tmpMedicalNotes,_tmpCreatedAt);
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
