package com.example.`data`.history

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
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
public class HistoryDao_Impl(
  __db: RoomDatabase,
) : HistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryItem: EntityInsertAdapter<HistoryItem>

  private val __deleteAdapterOfHistoryItem: EntityDeleteOrUpdateAdapter<HistoryItem>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryItem = object : EntityInsertAdapter<HistoryItem>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `history` (`id`,`type`,`durationMs`,`timestamp`,`details`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryItem) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.type)
        statement.bindLong(3, entity.durationMs)
        statement.bindLong(4, entity.timestamp)
        statement.bindText(5, entity.details)
      }
    }
    this.__deleteAdapterOfHistoryItem = object : EntityDeleteOrUpdateAdapter<HistoryItem>() {
      protected override fun createQuery(): String = "DELETE FROM `history` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryItem) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertHistoryItem(item: HistoryItem): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfHistoryItem.insert(_connection, item)
  }

  public override suspend fun deleteHistoryItem(item: HistoryItem): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfHistoryItem.handle(_connection, item)
  }

  public override fun getAllHistory(): Flow<List<HistoryItem>> {
    val _sql: String = "SELECT * FROM history ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfDetails: Int = getColumnIndexOrThrow(_stmt, "details")
        val _result: MutableList<HistoryItem> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryItem
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpDetails: String
          _tmpDetails = _stmt.getText(_columnIndexOfDetails)
          _item = HistoryItem(_tmpId,_tmpType,_tmpDurationMs,_tmpTimestamp,_tmpDetails)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAllHistory() {
    val _sql: String = "DELETE FROM history"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
