package com.kihare.todo.app.recyclerView

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kihare.todo.app.R
import com.kihare.todo.app.AppApplication
import com.kihare.todo.app.db.model.Todo
import java.util.*

@Suppress("DEPRECATED_SYMBOL_WITH_MESSAGE")
class RecyclerAdapter(val mContext: Context) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>() {
    private val mInflater: LayoutInflater
    private var actions: ArrayList<Todo> = ArrayList()
    public var dones: ArrayList<Todo> = ArrayList()
    private var mOnViewClickListener: onViewClickListener = object : onViewClickListener {
        override fun itemClickListener(v: View) {
        }
    }

    init {
        mInflater = LayoutInflater.from(mContext)
    }

    override fun getItemViewType(position: Int): Int {
        val actionCount = actions.size
        fun donePosition(): Int = actionCount + 1
        when (position) {
            POSITION_TO_BAR -> return VIEW_TYPE_TO_BAR
            donePosition() -> return VIEW_TYPE_DONE_BAR
            in 1..(donePosition() - 1) -> return VIEW_TYPE_TO
            else -> return VIEW_TYPE_DONE
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerViewHolder {
        when (type) {
            VIEW_TYPE_TO_BAR->
                return RecyclerViewHolder(mContext, mInflater.inflate(R.layout.recycler_view_ber, viewGroup, false), VIEW_TYPE_TO_BAR)
            VIEW_TYPE_DONE_BAR->
                return RecyclerViewHolder(mContext, mInflater.inflate(R.layout.recycler_view_ber, viewGroup, false), VIEW_TYPE_DONE_BAR)
            VIEW_TYPE_TO ->
                return RecyclerViewHolder(mContext, mInflater.inflate(R.layout.home_recycler_view_content, viewGroup, false), VIEW_TYPE_TO)
            else ->
                return RecyclerViewHolder(mContext, mInflater.inflate(R.layout.home_recycler_view_content, viewGroup, false), VIEW_TYPE_DONE)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerViewHolder, i: Int) {
        val type = viewHolder.itemViewType
        if (type == VIEW_TYPE_TO_BAR) {
            // when Action bar
            // it will return Especially since there is no treatment.
            return
        } else if (type == VIEW_TYPE_DONE_BAR) {
            // when Done bar
            // it will return Especially since there is no treatment.
            return
        } else {
            // when Todo data

            // clean translation
            viewHolder.content!!.translationX = 0f
            viewHolder.content!!.translationY = 0f

            if (type == VIEW_TYPE_TO) {
                val action = actions[getActionPosition(i)]
                viewHolder.box.tag = action.id
                viewHolder.createdAt!!.text = AppApplication.convertLongToDateString(action.created_at)
                viewHolder.action!!.text = action.action
            } else {
                val action = dones[getDonePosition(i)]
                viewHolder.box.tag = action.id
                viewHolder.createdAt!!.text = AppApplication.convertLongToDateString(action.created_at)
                viewHolder.action!!.text = action.action
            }
        }
    }

    override fun getItemCount(): Int {
        val actionCount = actions.size
        val doneCount = dones.size
        return actionCount + doneCount + 2
    }

    public fun clearAllItem(){
        actions.clear()
        dones.clear()
    }

    private fun getActionPosition(position : Int): Int {
        return position - 1
    }

    private fun getDonePosition(position: Int): Int {
        val actionCount = actions.size
        val donePosition = position - actionCount - 2
        return if(0 <= donePosition) donePosition else 0
    }

    public fun setActionsWithCursor(cursor: Cursor?) {
        if (cursor == null) {
            return
        }
        actions = arrayListOf<Todo>()
        for (i in 0..cursor.count) {
            if(cursor.moveToPosition(i)) {
                var todo = Todo()
                todo.id = cursor.getInt(cursor.getColumnIndex("id"))
                todo.action = cursor.getString(cursor.getColumnIndex("action"))
                todo.status = cursor.getInt(cursor.getColumnIndex("status"))
                todo.updated_at = cursor.getLong(cursor.getColumnIndex("updated_at"))
                todo.created_at = cursor.getLong(cursor.getColumnIndex("created_at"))
                actions.add(todo)
            }
        }
    }

    public fun setDonesWithCursor(cursor: Cursor?) {
        if (cursor == null) {
            return
        }
        dones = arrayListOf<Todo>()
        for (i in 0..cursor.count) {
            if(cursor.moveToPosition(i)) {
                var todo = Todo()
                todo.id = cursor.getInt(cursor.getColumnIndex("id"))
                todo.action = cursor.getString(cursor.getColumnIndex("action"))
                todo.status = cursor.getInt(cursor.getColumnIndex("status"))
                todo.updated_at = cursor.getLong(cursor.getColumnIndex("updated_at"))
                todo.created_at = cursor.getLong(cursor.getColumnIndex("created_at"))
                dones.add(todo)
            }
        }
    }

    public fun setOnViewClickListener(setOnViewClickListener: onViewClickListener) {
        this.mOnViewClickListener = setOnViewClickListener
    }

    public fun notifyInsertAction(item: Todo){
        actions.add(0, item)
        notifyItemInserted(1)
    }

    public fun notifyInsertDone(item: Todo){
        notifyItemInserted(actions.size + 2)
        dones.add(0, item)
    }

    public fun changeStatusActionToDone(position: Int){
        val actionPosition = getActionPosition(position)
        val item = actions[actionPosition]
        actions.removeAt(actionPosition)
        notifyItemRemoved(position)
        notifyInsertDone(item)
    }

    public fun changeStatusDoneToAction(position: Int){
        val donePosition = getDonePosition(position)
        val item = dones[donePosition]
        dones.removeAt(donePosition)
        notifyItemRemoved(position)
        notifyInsertAction(item)
    }

    public fun notifyRemoveDoneItem(position: Int){
        dones.removeAt(getDonePosition(position))
        notifyItemRemoved(position)
    }

    interface onViewClickListener {
        fun itemClickListener(v: View)
    }

    public class RecyclerViewHolder(val context : Context, val box: View, val type: Int) : RecyclerView.ViewHolder(box) {
        var createdAt: TextView? = null
        var action: TextView? = null
        var content: View? = null

        init {

            if (type == RecyclerAdapter.static.VIEW_TYPE_TO_BAR) {
                (box.findViewById(R.id.bar_title) as TextView).text = "Action"

            } else if (type == RecyclerAdapter.static.VIEW_TYPE_DONE_BAR) {
                (box.findViewById(R.id.bar_title) as TextView).text = "DONE"

            } else if (type == RecyclerAdapter.static.VIEW_TYPE_TO) {
                createdAt = box.findViewById(R.id.created_at) as TextView
                action = box.findViewById(R.id.action) as TextView
                content = box.findViewById(R.id.module)
                (box.findViewById(R.id.swipe_status) as TextView).text = "Done"
                (box.findViewById(R.id.swipe_status) as TextView).setTextColor(context.resources.getColor(R.color.primary_text))
                (box.findViewById(R.id.swipe_status) as TextView).setBackgroundColor(context.resources.getColor(R.color.primary_light))

            } else if (type == RecyclerAdapter.static.VIEW_TYPE_DONE) {
                createdAt = box.findViewById(R.id.created_at) as TextView
                action = box.findViewById(R.id.action) as TextView
                content = box.findViewById(R.id.module)
                (content as LinearLayout).background = context.getDrawable(R.color.gray)
                (box.findViewById(R.id.swipe_status) as TextView).text = "Delete"
                (box.findViewById(R.id.swipe_status) as TextView).setTextColor(context.resources.getColor(R.color.gray))
                (box.findViewById(R.id.swipe_status) as TextView).setBackgroundColor(context.resources.getColor(R.color.black))

            }
        }
    }

    companion object static {
        public val VIEW_TYPE_TO = 0
        public val VIEW_TYPE_DONE = 1
        public val VIEW_TYPE_TO_BAR = 2
        public val VIEW_TYPE_DONE_BAR = 3
        public val POSITION_TO_BAR = 0
    }

}
