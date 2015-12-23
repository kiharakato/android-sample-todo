package com.kihare.todo.app.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CustomSQLiteHelper : SQLiteOpenHelper {

    companion object {
        val DB : String = "kihare.db"
        val TABLE_NAME = "TODO"
        val DB_VERSION : Int = 1
    }

    constructor(context: Context) : super(context, DB, null, DB_VERSION)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE $TABLE_NAME (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        action TEXT,
        status INTEGER NOT NULL,
        updated_at INTEGER NOT NULL,
        created_at INTEGER NOT NULL
        );
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("""
        DROP TABLE $TABLE_NAME;
        """)
        onCreate(db)
    }

}
