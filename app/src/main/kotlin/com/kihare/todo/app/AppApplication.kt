package com.kihare.todo.app

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.kihare.todo.app.db.CustomSQLiteHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AppApplication : Application() {

    public val DB : SQLiteDatabase by lazy { CustomSQLiteHelper(this).writableDatabase }

    public fun getDataBase() : SQLiteDatabase {
        return DB
    }

    override fun onCreate() {
        super.onCreate()
        AppApplication.context = applicationContext
    }

    companion object {

        private var context : Context by Delegates.notNull()

        fun getInstance() : Context {
            return context
        }

        fun getDateTime(): Long {
            return Date().time
        }

        private val yyyymmddhhmm = SimpleDateFormat("yyyy/MM/dd HH:mm")
        private val mmdd = SimpleDateFormat("MM/dd")
        private val hhmm = SimpleDateFormat("hh:mm")
        private val yyyymmdd = SimpleDateFormat("yyyy/MM/dd")
        /**
         * Long の数字を日付フォーマットに変換します。
         *
         * @param date
         * @return String
         */
        public fun convertLongToDateString(date: Long?) : String {
            if (date == null) {
                return ""
            }

            if (Date(yyyymmdd.format(Calendar.getInstance().time)).time < date){
                return hhmm.format(Date(date))
            } else {
                return mmdd.format(Date(date));
            }

        }


    }

}