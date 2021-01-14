package com.yadoms.yadroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi
import java.util.ArrayList

class SelectKeywordFragment : Fragment() {

    private var columnCount = 1

    fun newWidgetActivity(): NewWidgetActivity {
        return activity as NewWidgetActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_keyword, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                (activity as NewWidgetActivity).setOperationDescription(R.string.select_keyword)

                val onItemClickListener =
                    object : SelectKeywordRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val selectedKeywordId = newWidgetActivity().preselectedKeywords[position].id
                            Log.d(SelectDeviceFragment::class.simpleName, "Selected keyword = $selectedKeywordId")

                            newWidgetActivity().addNewWidget(selectedKeywordId)

                            newWidgetActivity().finish()
                        }
                    }

                adapter = SelectKeywordRecyclerViewAdapter(newWidgetActivity().preselectedKeywords, onItemClickListener)
            }
        }

        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SelectKeywordFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}