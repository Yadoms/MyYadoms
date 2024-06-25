package com.yadoms.myyadoms

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class WidgetSwipeAndDragHandler(context: Context, val adapter: WidgetsRecyclerViewAdapter) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private var icon: Drawable? = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)
    private var deleteItemBackground: ColorDrawable = ColorDrawable(context.getColor(R.color.deleteItem))

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        adapter.moveWidget(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.deleteWidget(viewHolder.bindingAdapterPosition)
        showUndoSnackbar(viewHolder.itemView)
    }

    private fun showUndoSnackbar(itemView: View) {
        val snackbar: Snackbar = Snackbar.make(
            itemView, itemView.context.getString(R.string.widget_deleted),
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(itemView.context.getString(R.string.undo)) { adapter.undoDelete() }
        snackbar.show()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        )
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 0

        if (icon == null)
            return

        val iconMargin: Int = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop: Int = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom: Int = iconTop + icon!!.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon!!.intrinsicWidth
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteItemBackground.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteItemBackground.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> { // view is unSwiped
                icon!!.setBounds(0, 0, 0, 0)
                deleteItemBackground.setBounds(0, 0, 0, 0)
            }
        }

        deleteItemBackground.draw(c)
        icon!!.draw(c)
    }
}