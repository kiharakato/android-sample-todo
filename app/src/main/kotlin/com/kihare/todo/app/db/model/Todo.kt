package com.kihare.todo.app.db.model

import com.kihare.todo.app.AppApplication

class Todo{

    public var id : Int? = null
    public var action : String? = null
    public var status : Int? = null
    public var updated_at : Long? = null
    public var created_at : Long? = null

    companion object {
        var ACTION_STATUS = 0
        var DONE_STATUS = 1
        var DELETE_STATUS = 2

        public fun create(action : String) : Todo{
            val todo = Todo()

            todo.status = Todo.ACTION_STATUS
            todo.action = action
            todo.created_at = AppApplication.getDateTime()
            todo.updated_at = AppApplication.getDateTime()

            return todo
        }
    }
}
