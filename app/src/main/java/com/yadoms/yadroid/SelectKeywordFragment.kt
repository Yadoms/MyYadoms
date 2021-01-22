package com.yadoms.yadroid

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.key
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi

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
                            newWidgetActivity().selectedKeywordName = newWidgetActivity().preselectedKeywords[position].friendlyName

                            Log.d(
                                SelectDeviceFragment::class.simpleName,
                                "Selected keyword = ${newWidgetActivity().selectedKeywordName} (${newWidgetActivity().selectedKeywordId})"
                            )

                            findNavController().navigate(SelectKeywordFragmentDirections.actionKeywordFragmentToNameFragment())
                        }
                    }

                if (newWidgetActivity().preselectedKeywords.isEmpty()) {
                    // Only for Yadoms < 2.4 compatibility
                    val kwFilter = newWidgetActivity().selectedWidgetType!!.keywordFilter
                    val yApi = YadomsApi(Preferences(activity as Context).serverConnection)
                    newWidgetActivity().selectedDeviceId?.let { selectedDeviceId ->
                        DeviceApi(yApi).getDeviceKeywords(
                            activity,
                            selectedDeviceId,
                            onOk = { keywords ->
                                keywords.forEach { keyword ->
                                    if (kwFilter.expectedKeywordAccess.isNotEmpty() && keyword.AccessMode !in kwFilter.expectedKeywordAccess) {
                                        return@forEach
                                    }
                                    if (kwFilter.expectedKeywordType.isNotEmpty() && keyword.Type !in kwFilter.expectedKeywordType) {
                                        return@forEach
                                    }
                                    newWidgetActivity().preselectedKeywords.add(keyword)
                                }
                                adapter?.notifyDataSetChanged();
                            },
                            onError = {
                                if (activity != null)
                                    Toast.makeText(
                                        activity, "Unable to reach the server", Toast.LENGTH_SHORT
                                    ).show()
                            })
                    }
                }


                adapter = SelectKeywordRecyclerViewAdapter(newWidgetActivity().preselectedKeywords, onItemClickListener)
            }
        }

        return view
    }
}