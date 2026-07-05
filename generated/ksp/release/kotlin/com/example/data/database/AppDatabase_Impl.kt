package com.example.`data`.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.`data`.alarm.AlarmDao
import com.example.`data`.alarm.AlarmDao_Impl
import com.example.`data`.history.HistoryDao
import com.example.`data`.history.HistoryDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _alarmDao: Lazy<AlarmDao> = lazy {
    AlarmDao_Impl(this)
  }

  private val _historyDao: Lazy<HistoryDao> = lazy {
    HistoryDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "2901fd70b7ccedd6615b7b1939996cec", "be47c3def54ea4c80e65d2d134bd36fd") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `alarms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, `customLabel` TEXT NOT NULL, `days` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `soundName` TEXT NOT NULL, `isVibrationEnabled` INTEGER NOT NULL, `snoozeMinutes` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `details` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '2901fd70b7ccedd6615b7b1939996cec')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `alarms`")
        connection.execSQL("DROP TABLE IF EXISTS `history`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsAlarms: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAlarms.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("hour", TableInfo.Column("hour", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("minute", TableInfo.Column("minute", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("customLabel", TableInfo.Column("customLabel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("days", TableInfo.Column("days", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("isEnabled", TableInfo.Column("isEnabled", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("soundName", TableInfo.Column("soundName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("isVibrationEnabled", TableInfo.Column("isVibrationEnabled", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAlarms.put("snoozeMinutes", TableInfo.Column("snoozeMinutes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAlarms: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAlarms: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAlarms: TableInfo = TableInfo("alarms", _columnsAlarms, _foreignKeysAlarms,
            _indicesAlarms)
        val _existingAlarms: TableInfo = read(connection, "alarms")
        if (!_infoAlarms.equals(_existingAlarms)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |alarms(com.example.data.alarm.Alarm).
              | Expected:
              |""".trimMargin() + _infoAlarms + """
              |
              | Found:
              |""".trimMargin() + _existingAlarms)
        }
        val _columnsHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistory.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("durationMs", TableInfo.Column("durationMs", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHistory.put("details", TableInfo.Column("details", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHistory: TableInfo = TableInfo("history", _columnsHistory, _foreignKeysHistory,
            _indicesHistory)
        val _existingHistory: TableInfo = read(connection, "history")
        if (!_infoHistory.equals(_existingHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |history(com.example.data.history.HistoryItem).
              | Expected:
              |""".trimMargin() + _infoHistory + """
              |
              | Found:
              |""".trimMargin() + _existingHistory)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "alarms", "history")
  }

  public override fun clearAllTables() {
    super.performClear(false, "alarms", "history")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(AlarmDao::class, AlarmDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HistoryDao::class, HistoryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun alarmDao(): AlarmDao = _alarmDao.value

  public override fun historyDao(): HistoryDao = _historyDao.value
}
