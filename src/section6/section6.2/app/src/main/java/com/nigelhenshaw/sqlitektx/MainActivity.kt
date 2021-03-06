package com.nigelhenshaw.sqlitektx

import android.app.Activity
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Toast
import androidx.core.widget.toast

/**
 * This exercise will be used to compare the standard Android
 * SQLite database transactions with the KTX SQLite module
 * transaction call
 */
class MainActivity : Activity() {

    private val dbHelper by lazy {
        DbHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Call the newly created insertTransaction() here

        val titles = displayTitlesTransaction()
        toast(titles.toString(), Toast.LENGTH_LONG)

        deleteTableTransaction()

    }

    /**
     * Create a function called insertTransaction()
     * It will be used to insert a value to the database using the
     * standard Android SQLite transaction.
     * Then the implementation will be changed to the KTX SQLite module
     * transaction.
     */

    private fun deleteTableTransaction() {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(KtxContract.KtxEntry.TABLE_NAME, null, null)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun displayTitlesTransaction(): List<String> {
        val db = dbHelper.readableDatabase
        val titles: List<String>
        db.beginTransaction()
        try {
            titles = getTitleList()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return titles
    }

    private fun getTitleList(): List<String> {
        val db = dbHelper.readableDatabase
        val titleList = mutableListOf<String>()
        val cursor = getTitleCursor(db)
        with(cursor) {
            while (cursor.moveToNext()) {
                val title = getString(getColumnIndexOrThrow(KtxContract.KtxEntry.COLUMN_NAME_TITLE))
                titleList.add(title)
            }
        }
        return titleList

    }

    private fun getTitleCursor(db: SQLiteDatabase): Cursor {
        val projection = arrayOf(BaseColumns._ID, KtxContract.KtxEntry.COLUMN_NAME_TITLE)
        return db.query(
                KtxContract.KtxEntry.TABLE_NAME,
                projection,
                null,
                null, null, null, null)

    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
