package com.kihare.todo.app.activity

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Activity
import android.database.Cursor
import android.graphics.Canvas
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import butterknife.bindView
import com.kihare.todo.app.R
import com.kihare.todo.app.db.TodoRepository
import com.kihare.todo.app.db.model.Todo
import com.kihare.todo.app.recyclerView.DividerItemDecoration
import com.kihare.todo.app.recyclerView.RecyclerAdapter

class MainActivity : Activity() {

    private val mAdapter: RecyclerAdapter by lazy{ RecyclerAdapter(this) }

    private val mRootView: FrameLayout by bindView(R.id.container)
    private val mRecyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val RecyclerViewRefresh: SwipeRefreshLayout by bindView(R.id.recycler_view_refresh)
    private val mCreateView: RelativeLayout by bindView(R.id.create_container)
    private val mCreateEditText: EditText by bindView(R.id.text_area)
    private val mThis: Activity by lazy{ this }
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val createBtn: Button by bindView(R.id.create_btn)
    private val createDismissBtn: ImageButton by bindView(R.id.dismiss_btn)
    private val coordinatorView: CoordinatorLayout by bindView(R.id.coordinator_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar init
        val toolbar = findViewById(R.id.tool_bar) as Toolbar
        toolbar.title = "Todo"
        toolbar.setTitleTextColor(resources.getColor(R.color.white))

        // RecyclerView init
        mAdapter.setActionsWithCursor(allActionStatus())
        mAdapter.setDonesWithCursor(allDoneStatus())
        val helper = ItemTouchHelper(ItemOnTouchCallback())
        helper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.addItemDecoration(helper)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.addItemDecoration(DividerItemDecoration(this))
        RecyclerViewRefresh.setOnRefreshListener {
            mAdapter.clearAllItem()
            mAdapter.setActionsWithCursor(allActionStatus())
            mAdapter.setDonesWithCursor(allDoneStatus())
            mAdapter.notifyDataSetChanged()
            RecyclerViewRefresh.isRefreshing = false
        }

        // crate view
        createBtn.setOnClickListener {
            if (mCreateEditText.text.equals("")) {
                Snackbar.make(coordinatorView, R.string.create_todo_failed, Snackbar.LENGTH_LONG).show()
            } else {
                val todo = TodoRepository.create(this, mCreateEditText.text.toString())
                mAdapter.notifyInsertAction(todo)
                switchCreateTodoView()
                Snackbar.make(coordinatorView, R.string.create_todo_success, Snackbar.LENGTH_LONG).show()
            }
        }

        // FloatingActionButton
        fab.setOnClickListener {
            switchCreateTodoView()
        }

        // Set DismissBtn behavior
        createDismissBtn.setOnClickListener{
            switchCreateTodoView()
        }

    }

    override fun onDestroy() {
        mRecyclerView.adapter = null
    }

    private fun allDoneStatus(): Cursor? {
        return TodoRepository.SelectDoneStatusAll(applicationContext)
    }

    private fun allActionStatus(): Cursor? {
        return TodoRepository.SelectActionStatusAll(applicationContext)
    }

    private fun switchCreateTodoView() {
        //Display change on add action area
        if (mCreateView.alpha == 0f) {
            val anim = AnimatorInflater.loadAnimator(this, R.anim.fade_in_500)
            anim.setTarget(mCreateView)
            anim.start()

            val animFab = AnimatorInflater.loadAnimator(this, R.anim.scale_out_500)
            animFab.setTarget(fab)
            animFab.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    fab.isEnabled = false
                    createBtn.isEnabled = true
                    createDismissBtn.isEnabled = true
                    mCreateEditText.isEnabled = true
                }
            })
            animFab.start()

            //fab.setImageResource(R.drawable.ic_clear_white_24dp);
            //mCreateView.visibility = View.VISIBLE
        } else {
            val anim = AnimatorInflater.loadAnimator(this, R.anim.fade_out_500)
            anim.setTarget(mCreateView)
            anim.start()

            val animFab = AnimatorInflater.loadAnimator(this, R.anim.scale_in_500)
            animFab.setTarget(fab)
            animFab.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    fab.isEnabled = true
                    createBtn.isEnabled = false
                    createDismissBtn.isEnabled = false
                    mCreateEditText.isEnabled = false
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            animFab.start()

            fab.isClickable = true

            //fab.setImageResource(R.drawable.ic_clear_white_24dp)
            //mCreateView.visibility = View.GONE
        }
    }

    private inner class TodoItemTouchHelper(callback : ItemTouchHelper.Callback) : ItemTouchHelper(callback) {

        override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
            super.onDraw(c, parent, state)
        }

    }

    private inner class ItemOnTouchCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            if(viewHolder.itemViewType == RecyclerAdapter.VIEW_TYPE_TO_BAR ||
                    viewHolder.itemViewType == RecyclerAdapter.VIEW_TYPE_DONE_BAR){
                        return ItemTouchHelper.Callback.makeMovementFlags(0, 0)
            }

            return ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.END)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // item swipe behavior on ItemTouchHelper.END
            if (direction == ItemTouchHelper.END) {
                val position = viewHolder.adapterPosition
                val id = viewHolder.itemView.tag as Int

                // update adapter
                when(viewHolder.itemViewType) {
                    RecyclerAdapter.VIEW_TYPE_TO ->
                        mAdapter.changeStatusActionToDone(position)
                    RecyclerAdapter.VIEW_TYPE_DONE ->
                        mAdapter.notifyRemoveDoneItem(position)
                    else-> return
                }

                // update database
                var status: Int
                when(viewHolder.itemViewType){
                    RecyclerAdapter.VIEW_TYPE_TO ->
                        status = Todo.DONE_STATUS
                    RecyclerAdapter.VIEW_TYPE_DONE ->
                        status = Todo.DELETE_STATUS
                    else -> return
                }
                TodoRepository.UpdateStatus(mThis, id, status)
            }

        }

        override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (viewHolder != null) {
                val type = viewHolder.itemViewType
                if (type == RecyclerAdapter.static.VIEW_TYPE_TO || type == RecyclerAdapter.static.VIEW_TYPE_DONE) {
                    if (viewHolder is RecyclerAdapter.RecyclerViewHolder) {
                        viewHolder.content!!.translationY = dY
                        viewHolder.content!!.translationX = dX
                        return
                    }
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

}
