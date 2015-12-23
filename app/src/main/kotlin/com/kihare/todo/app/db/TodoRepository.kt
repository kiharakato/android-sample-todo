package com.kihare.todo.app.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.kihare.todo.app.db.model.Todo
import com.kihare.todo.app.AppApplication

class TodoRepository {

    companion object{

        private fun getContext(context : Context): AppApplication? {
            val applicationContext = context.applicationContext
            return if (applicationContext is AppApplication) applicationContext else null
        }

        /**
         * Create new Todo data.
         */
        public fun create(context: Context, action : String) : Todo {
            val todo = Todo.create(action)

            val values = ContentValues()
            values.put("action", todo.action)
            values.put("status", todo.status)
            values.put("created_at", todo.created_at)
            values.put("updated_at", todo.updated_at)

            todo.id = getContext(context)!!.DB.insert(CustomSQLiteHelper.TABLE_NAME, null, values).toInt()

            return todo
        }

        /***
         * Get status is action of the All field.
         */
        public fun SelectActionStatusAll(context: Context): Cursor? {
            if(getContext(context) == null){
                return null
            }

            return getContext(context)!!.DB.query(
                    CustomSQLiteHelper.TABLE_NAME,
                    Array(1, { "*" }),
                    "status = ${Todo.ACTION_STATUS}",
                    null, null, null, "updated_at desc")
        }

        /***
         * Get status is done of the All field.
         */
        public fun SelectDoneStatusAll(context: Context): Cursor? {
            if(getContext(context) == null){
                return null
            }
            return getContext(context)!!.DB.query(
                    CustomSQLiteHelper.TABLE_NAME,
                    Array(1, { "*" }),
                    "status = ${Todo.DONE_STATUS}",
                    null, null, null, "updated_at desc")
        }

        /**
         *
         */
        public fun UpdateStatus(context : Context, id : Int, status : Int) : Int {
            if (getContext(context) == null) {
                return -1
            }
            val contentValues = ContentValues()
            contentValues.put("status", status)
            contentValues.put("updated_at", AppApplication.getDateTime())
            return getContext(context)!!.DB.update(CustomSQLiteHelper.TABLE_NAME, contentValues, "id = $id", null)
        }
    }
}