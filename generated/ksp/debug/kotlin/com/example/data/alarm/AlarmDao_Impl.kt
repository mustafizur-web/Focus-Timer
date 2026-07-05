package com.example.`data`.alarm

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AlarmDao_Impl(
  __db: RoomDatabase,
) : AlarmDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAlarm: EntityInsertAdapter<Alarm>

  private val __deleteAdapterOfAlarm: EntityDeleteOrUpdateAdapter<Alarm>

  private val __updateAdapterOfAlarm: EntityDeleteOrUpdateAdapter<Alarm>
  init {
    this.__db = __db
    this.__insertAdapterOfAlarm = object : EntityInsertAdapter<Alarm>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `alarms` (`id`,`hour`,`minute`,`customLabel`,`days`,`isEnabled`,`soundName`,`isVibrationEnabled`,`snoozeMinutes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Alarm) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.hour.toLong())
        statement.bindLong(3, entity.minute.toLong())
        statement.bindText(4, entity.customLabel)
        statement.bindText(5, entity.days)
        val _tmp: Int = if (entity.isEnabled) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.soundName)
        val _tmp_1: Int = if (entity.isVibrationEnabled) 1 else 0
        statement.bindLong(8, _tmp_1.toLong())
        statement.bindLong(9, entity.snoozeMinutes.toLong())
      }
    }
    this.__deleteAdapterOfAlarm = object : EntityDeleteOrUpdateAdapter<Alarm>() {
      protected override fun createQuery(): String = "DELETE FROM `alarms` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Alarm) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfAlarm = object : EntityDeleteOrUpdateAdapter<Alarm>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `alarms` SET `id` = ?,`hour` = ?,`minute` = ?,`customLabel` = ?,`days` = ?,`isEnabled` = ?,`soundName` = ?,`isVibrationEnabled` = ?,`snoozeMinutes` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Alarm) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindLong(2, entity.hour.toLong())
        statement.bindLong(3, entity.minute.toLong())
        statement.bindText(4, entity.customLabel)
        statement.bindText(5, entity.days)
        val _tmp: Int = if (entity.isEnabled) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.soundName)
        val _tmp_1: Int = if (entity.isVibrationEnabled) 1 else 0
        statement.bindLong(8, _tmp_1.toLong())
        statement.bindLong(9, entity.snoozeMinutes.toLong())
        statement.bindLong(10, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertAlarm(alarm: Alarm): Long = performSuspending(__db, false, true)
      { _connection ->
    val _result: Long = __insertAdapterOfAlarm.insertAndReturnId(_connection, alarm)
    _result
  }

  public override suspend fun deleteAlarm(alarm: Alarm): Unit = performSuspending(__db, false, true)
      { _connection ->
    __deleteAdapterOfAlarm.handle(_connection, alarm)
  }

  public override suspend fun updateAlarm(alarm: Alarm): Unit = performSuspending(__db, false, true)
      { _connection ->
    __updateAdapterOfAlarm.handle(_connection, alarm)
  }

  public override fun getAllAlarms(): Flow<List<Alarm>> {
    val _sql: String = "SELECT * FROM alarms ORDER BY hour ASC, minute ASC"
    return createFlow(__db, false, arrayOf("alarms")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfHour: Int = getColumnIndexOrThrow(_stmt, "hour")
        val _columnIndexOfMinute: Int = getColumnIndexOrThrow(_stmt, "minute")
        val _columnIndexOfCustomLabel: Int = getColumnIndexOrThrow(_stmt, "customLabel")
        val _columnIndexOfDays: Int = getColumnIndexOrThrow(_stmt, "days")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _columnIndexOfSoundName: Int = getColumnIndexOrThrow(_stmt, "soundName")
        val _columnIndexOfIsVibrationEnabled: Int = getColumnIndexOrThrow(_stmt,
            "isVibrationEnabled")
        val _columnIndexOfSnoozeMinutes: Int = getColumnIndexOrThrow(_stmt, "snoozeMinutes")
        val _result: MutableList<Alarm> = mutableListOf()
        while (_stmt.step()) {
          val _item: Alarm
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpHour: Int
          _tmpHour = _stmt.getLong(_columnIndexOfHour).toInt()
          val _tmpMinute: Int
          _tmpMinute = _stmt.getLong(_columnIndexOfMinute).toInt()
          val _tmpCustomLabel: String
          _tmpCustomLabel = _stmt.getText(_columnIndexOfCustomLabel)
          val _tmpDays: String
          _tmpDays = _stmt.getText(_columnIndexOfDays)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          val _tmpSoundName: String
          _tmpSoundName = _stmt.getText(_columnIndexOfSoundName)
          val _tmpIsVibrationEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsVibrationEnabled).toInt()
          _tmpIsVibrationEnabled = _tmp_1 != 0
          val _tmpSnoozeMinutes: Int
          _tmpSnoozeMinutes = _stmt.getLong(_columnIndexOfSnoozeMinutes).toInt()
          _item =
              Alarm(_tmpId,_tmpHour,_tmpMinute,_tmpCustomLabel,_tmpDays,_tmpIsEnabled,_tmpSoundName,_tmpIsVibrationEnabled,_tmpSnoozeMinutes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAlarmById(id: Int): Alarm? {
    val _sql: String = "SELECT * FROM alarms WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfHour: Int = getColumnIndexOrThrow(_stmt, "hour")
        val _columnIndexOfMinute: Int = getColumnIndexOrThrow(_stmt, "minute")
        val _columnIndexOfCustomLabel: Int = getColumnIndexOrThrow(_stmt, "customLabel")
        val _columnIndexOfDays: Int = getColumnIndexOrThrow(_stmt, "days")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _columnIndexOfSoundName: Int = getColumnIndexOrThrow(_stmt, "soundName")
        val _columnIndexOfIsVibrationEnabled: Int = getColumnIndexOrThrow(_stmt,
            "isVibrationEnabled")
        val _columnIndexOfSnoozeMinutes: Int = getColumnIndexOrThrow(_stmt, "snoozeMinutes")
        val _result: Alarm?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpHour: Int
          _tmpHour = _stmt.getLong(_columnIndexOfHour).toInt()
          val _tmpMinute: Int
          _tmpMinute = _stmt.getLong(_columnIndexOfMinute).toInt()
          val _tmpCustomLabel: String
          _tmpCustomLabel = _stmt.getText(_columnIndexOfCustomLabel)
          val _tmpDays: String
          _tmpDays = _stmt.getText(_columnIndexOfDays)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          val _tmpSoundName: String
          _tmpSoundName = _stmt.getText(_columnIndexOfSoundName)
          val _tmpIsVibrationEnabled: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsVibrationEnabled).toInt()
          _tmpIsVibrationEnabled = _tmp_1 != 0
          val _tmpSnoozeMinutes: Int
          _tmpSnoozeMinutes = _stmt.getLong(_columnIndexOfSnoozeMinutes).toInt()
          _result =
              Alarm(_tmpId,_tmpHour,_tmpMinute,_tmpCustomLabel,_tmpDays,_tmpIsEnabled,_tmpSoundName,_tmpIsVibrationEnabled,_tmpSnoozeMinutes)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
