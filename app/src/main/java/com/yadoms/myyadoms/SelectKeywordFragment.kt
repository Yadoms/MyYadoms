package com.yadoms.myyadoms

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class SelectKeywordFragment : Fragment() {

    val newWidgetActivity: NewWidgetActivity
        get() = activity as NewWidgetActivity

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
                            newWidgetActivity.selectedKeywordId = newWidgetActivity.preselectedKeywords[position].id
                            newWidgetActivity.selectedKeywordName = newWidgetActivity.preselectedKeywords[position].friendlyName

                            Log.d(
                                SelectDeviceFragment::class.simpleName,
                                "Selected keyword = ${newWidgetActivity.selectedKeywordName} (${newWidgetActivity.selectedKeywordId})"
                            )

                            findNavController().navigate(SelectKeywordFragmentDirections.actionKeywordFragmentToNameFragment())
                        }
                    }

                if (newWidgetActivity.preselectedKeywords.isEmpty()) {
                    // Only for Yadoms < 2.4 compatibility

                    newWidgetActivity.startWait()

                    val kwFilter = newWidgetActivity.selectedWidgetType!!.keywordFilter
                    val yApi = YadomsApi(Preferences(activity as Context).serverConnection)
                    newWidgetActivity.selectedDeviceId?.let { selectedDeviceId ->
                        DeviceApi(yApi).getDeviceKeywords(
                            activity,
                            selectedDeviceId,
                            onOk = { keywords ->
                                keywords.forEach { keyword ->
                                    if (kwFilter.expectedKeywordAccess.isNotEmpty() && keyword.accessMode !in kwFilter.expectedKeywordAccess) {
                                        return@forEach
                                    }
                                    if (kwFilter.expectedKeywordType.isNotEmpty() && keyword.type !in kwFilter.expectedKeywordType) {
                                        return@forEach
                                    }
                                    newWidgetActivity.preselectedKeywords.add(keyword)
                                }
                                adapter?.notifyDataSetChanged()
                                newWidgetActivity.stopWait()
                            },
                            onError = {
                                if (activity != null)
                                    Snackbar.make(
                                        view, context.getString(R.string.unable_to_reach_the_server),
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                newWidgetActivity.stopWait()
                            })
                    }
                }


                adapter = SelectKeywordRecyclerViewAdapter(newWidgetActivity.preselectedKeywords, onItemClickListener)
            }
        }

        return view
    }
}