package com.yadoms.yadroid

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi
import java.util.*

class SelectDeviceFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_select_device, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                newWidgetActivity().setOperationDescription(R.string.select_device)

                val preselectedDevices: MutableList<DeviceApi.Device> = ArrayList()
                val preselectedKeywords: MutableList<DeviceApi.Keyword> = ArrayList()

                val onItemClickListener =
                    object : SelectDeviceRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            newWidgetActivity().selectedDeviceId = preselectedDevices[position].id
                            newWidgetActivity().selectedDeviceName = preselectedDevices[position].friendlyName
                            Log.d(
                                SelectDeviceFragment::class.simpleName,
                                "Selected device = ${newWidgetActivity().selectedDeviceName} (${newWidgetActivity().selectedDeviceId})"
                            )
                            newWidgetActivity().preselectedKeywords.clear()
                            preselectedKeywords.forEach {
                                if (it.deviceId == newWidgetActivity().selectedDeviceId)
                                    newWidgetActivity().preselectedKeywords.add(it)
                            }

                            findNavController().navigate(SelectDeviceFragmentDirections.actionDeviceFragmentToKeywordFragment())
                        }
                    }

                val kwFilter = newWidgetActivity().selectedWidgetType!!.keywordFilter
                val yApi = YadomsApi(Preferences(activity as Context).serverConnection)
                DeviceApi(yApi).getDeviceMatchKeywordCriteria(
                    activity,
                    expectedKeywordType = kwFilter.expectedKeywordType,
                    expectedCapacity = kwFilter.expectedCapacity,
                    expectedKeywordAccess = kwFilter.expectedKeywordAccess,
                    onOk = { devices: List<DeviceApi.Device>, keywords: List<DeviceApi.Keyword> ->
                        devices.forEach { preselectedDevices.add(it) }
                        keywords.forEach { preselectedKeywords.add(it) }

                        adapter?.notifyDataSetChanged();
                    },
                    onError = {
                        // Fallback for Yadoms < 2.4 which don't support matchkeywordcriteria request
                        if (kwFilter.expectedKeywordType.size != 1) {
                            if (activity != null)
                                Toast.makeText(
                                    activity, "Unable to reach the server", Toast.LENGTH_SHORT
                                ).show()
                        } else
                            DeviceApi(yApi).getDeviceWithCapacityType(
                                activity,
                                kwFilter.expectedKeywordType[0],
                                if (kwFilter.expectedKeywordAccess.size != 1) DeviceApi.KeywordAccess.NoAccess else kwFilter.expectedKeywordAccess[0],
                                onOk = { devices ->
                                    devices.forEach { device -> preselectedDevices.add(device) }
                                    adapter?.notifyDataSetChanged();
                                },
                                onError = {
                                    if (activity != null)
                                        Toast.makeText(
                                            activity, "Unable to reach the server", Toast.LENGTH_SHORT
                                        ).show()
                                })
                    })

                adapter = SelectDeviceRecyclerViewAdapter(preselectedDevices, onItemClickListener)
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
            SelectDeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}