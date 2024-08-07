package com.yadoms.myyadoms

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
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi
import java.util.*

class SelectDeviceFragment : Fragment() {
    private val _logTag = javaClass.canonicalName
    val newWidgetActivity: NewWidgetActivity
        get() = activity as NewWidgetActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_device, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                newWidgetActivity.setOperationDescription(R.string.select_device)

                val preselectedDevices: MutableList<DeviceApi.Device> = ArrayList()
                val preselectedKeywords: MutableList<DeviceApi.Keyword> = ArrayList()

                val onItemClickListener =
                    object : SelectDeviceRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            newWidgetActivity.selectedDeviceId = preselectedDevices[position].id
                            newWidgetActivity.selectedDeviceName = preselectedDevices[position].friendlyName
                            Log.d(
                                _logTag,
                                "Selected device = ${newWidgetActivity.selectedDeviceName} (${newWidgetActivity.selectedDeviceId})"
                            )
                            newWidgetActivity.preselectedKeywords.clear()
                            preselectedKeywords.forEach {
                                if (it.deviceId == newWidgetActivity.selectedDeviceId)
                                    newWidgetActivity.preselectedKeywords.add(it)
                            }

                            findNavController().navigate(SelectDeviceFragmentDirections.actionDeviceFragmentToKeywordFragment())
                        }
                    }

                newWidgetActivity.startWait()

                val kwFilter = newWidgetActivity.selectedWidgetType!!.keywordFilter
                val yApi = YadomsApi(context)
                DeviceApi(yApi).getDeviceMatchKeywordCriteria(
                    expectedKeywordType = kwFilter.expectedKeywordType,
                    expectedCapacity = kwFilter.expectedCapacity,
                    expectedKeywordAccess = kwFilter.expectedKeywordAccess,
                    onOk = { devices: List<DeviceApi.Device>, keywords: List<DeviceApi.Keyword> ->
                        devices.forEach { preselectedDevices.add(it) }
                        keywords.forEach { preselectedKeywords.add(it) }

                        adapter?.notifyDataSetChanged()
                        newWidgetActivity.stopWait()
                    }
                ) {
                    // Fallback for Yadoms < 2.4 which don't support matchkeywordcriteria request
                    if (kwFilter.expectedKeywordType.size != 1) {
                        if (activity != null)
                            Snackbar.make(
                                view,
                                context.getString(R.string.unable_to_reach_the_server),
                                Snackbar.LENGTH_LONG
                            ).show()
                        newWidgetActivity.stopWait()
                    } else
                        DeviceApi(yApi).getDeviceWithCapacityType(
                            kwFilter.expectedKeywordType[0],
                            if (kwFilter.expectedKeywordAccess.size != 1) DeviceApi.KeywordAccess.NoAccess else kwFilter.expectedKeywordAccess[0],
                            onOk = { devices ->
                                devices.forEach { device -> preselectedDevices.add(device) }
                                adapter?.notifyDataSetChanged()
                                newWidgetActivity.stopWait()
                            }
                        ) {
                            if (activity != null)
                                Snackbar.make(
                                    view,
                                    context.getString(R.string.unable_to_reach_the_server),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            newWidgetActivity.stopWait()
                        }
                }

                adapter = SelectDeviceRecyclerViewAdapter(preselectedDevices, onItemClickListener)
            }
        }

        return view
    }
}