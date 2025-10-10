package com.example.globaltranslation.`data`.local

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
public class ConversationDao_Impl(
  __db: RoomDatabase,
) : ConversationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfConversationEntity: EntityInsertAdapter<ConversationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfConversationEntity = object : EntityInsertAdapter<ConversationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `conversations` (`id`,`originalText`,`translatedText`,`sourceLang`,`targetLang`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConversationEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.originalText)
        statement.bindText(3, entity.translatedText)
        statement.bindText(4, entity.sourceLang)
        statement.bindText(5, entity.targetLang)
        statement.bindLong(6, entity.timestamp)
      }
    }
  }

  public override suspend fun insertConversation(conversation: ConversationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfConversationEntity.insert(_connection, conversation)
  }

  public override fun getAllConversations(): Flow<List<ConversationEntity>> {
    val _sql: String = "SELECT * FROM conversations ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("conversations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOriginalText: Int = getColumnIndexOrThrow(_stmt, "originalText")
        val _cursorIndexOfTranslatedText: Int = getColumnIndexOrThrow(_stmt, "translatedText")
        val _cursorIndexOfSourceLang: Int = getColumnIndexOrThrow(_stmt, "sourceLang")
        val _cursorIndexOfTargetLang: Int = getColumnIndexOrThrow(_stmt, "targetLang")
        val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ConversationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConversationEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpOriginalText: String
          _tmpOriginalText = _stmt.getText(_cursorIndexOfOriginalText)
          val _tmpTranslatedText: String
          _tmpTranslatedText = _stmt.getText(_cursorIndexOfTranslatedText)
          val _tmpSourceLang: String
          _tmpSourceLang = _stmt.getText(_cursorIndexOfSourceLang)
          val _tmpTargetLang: String
          _tmpTargetLang = _stmt.getText(_cursorIndexOfTargetLang)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp)
          _item =
              ConversationEntity(_tmpId,_tmpOriginalText,_tmpTranslatedText,_tmpSourceLang,_tmpTargetLang,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getConversationsForLanguagePair(sourceLang: String, targetLang: String):
      Flow<List<ConversationEntity>> {
    val _sql: String =
        "SELECT * FROM conversations WHERE sourceLang = ? AND targetLang = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("conversations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, sourceLang)
        _argIndex = 2
        _stmt.bindText(_argIndex, targetLang)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOriginalText: Int = getColumnIndexOrThrow(_stmt, "originalText")
        val _cursorIndexOfTranslatedText: Int = getColumnIndexOrThrow(_stmt, "translatedText")
        val _cursorIndexOfSourceLang: Int = getColumnIndexOrThrow(_stmt, "sourceLang")
        val _cursorIndexOfTargetLang: Int = getColumnIndexOrThrow(_stmt, "targetLang")
        val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ConversationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConversationEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_cursorIndexOfId)
          val _tmpOriginalText: String
          _tmpOriginalText = _stmt.getText(_cursorIndexOfOriginalText)
          val _tmpTranslatedText: String
          _tmpTranslatedText = _stmt.getText(_cursorIndexOfTranslatedText)
          val _tmpSourceLang: String
          _tmpSourceLang = _stmt.getText(_cursorIndexOfSourceLang)
          val _tmpTargetLang: String
          _tmpTargetLang = _stmt.getText(_cursorIndexOfTargetLang)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp)
          _item =
              ConversationEntity(_tmpId,_tmpOriginalText,_tmpTranslatedText,_tmpSourceLang,_tmpTargetLang,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteConversation(timestamp: Long) {
    val _sql: String = "DELETE FROM conversations WHERE timestamp = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM conversations"
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
