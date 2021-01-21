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

class SelectKeywordFragment : Fragment() {

    fun newWidgetActivity(): NewWidgetActivity {
        return activity as NewWidgetActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_keyword, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                (activity as NewWidgetActivity).setOperationDescription(R.string.select_keyword)

                val onItemClickListener =
                    object : SelectKeywordRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            newWidgetActivity().selectedKeywordId = newWidgetActivity().preselectedKeywords[position].id
                            newWidgetActivity().selectedKeywordName =newWidgetActivity().preselectedKeywords[position].friendlyName

                            Log.d(SelectDeviceFragment::class.simpleName, "Selected keyword = ${newWidgetActivity().selectedKeywordName} (${newWidgetActivity().selectedKeywordId})")

                            findNavController().navigate(SelectKeywordFragmentDirections.actionKeywordFragmentToNameFragment())
                        }
                    }

                adapter = SelectKeywordRecyclerViewAdapter(newWidgetActivity().preselectedKeywords, onItemClickListener)
            }
        }

        return view
    }
}