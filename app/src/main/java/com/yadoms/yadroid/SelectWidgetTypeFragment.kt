package com.yadoms.yadroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.widgets.WidgetTypes

/**
 * A fragment representing a list of Items.
 */
class SelectWidgetTypeFragment : Fragment() {

    fun newWidgetActivity(): NewWidgetActivity {
        return activity as NewWidgetActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_widget, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                (activity as NewWidgetActivity).setOperationDescription(R.string.select_widget_type)

                val onItemClickListener =
                    object : SelectWidgetTypeRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            Log.d(SelectWidgetTypeFragment::class.simpleName, "Selected widget type = $position")
                            newWidgetActivity().selectedWidgetType = WidgetTypes.WidgetTypes[position]
                            findNavController().navigate(SelectWidgetTypeFragmentDirections.actionWidgetTypeFragmentToDeviceFragment())
                        }
                    }

                adapter = SelectWidgetTypeRecyclerViewAdapter(WidgetTypes.WidgetTypes, onItemClickListener)
            }
        }

        return view
    }
}