package com.yadoms.yadroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

                val onItemClickListener =
                    object : SelectKeywordRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            (activity as NewWidgetActivity).selectedKeywordId = (activity as NewWidgetActivity).preselectedKeywords[position].id
                            Log.d(SelectDeviceFragment::class.simpleName, "Selected keyword = ${(activity as NewWidgetActivity).selectedKeywordId}")

//TODO                            findNavController().navigate( SelectKeywordFragmentDirections.actionSelectDeviceFragmentToEditLabelFragment())
                        }
                    }

                adapter = SelectKeywordRecyclerViewAdapter((activity as NewWidgetActivity).preselectedKeywords, onItemClickListener)
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