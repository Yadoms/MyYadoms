package com.yadoms.yadroid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

                val preselectedDevices: MutableList<DeviceApi.Device> = ArrayList()
                val preselectedKeywords: MutableList<DeviceApi.Keyword> = ArrayList()

                val onItemClickListener =
                    object : SelectDeviceRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            newWidgetActivity().selectedDeviceId = preselectedDevices[position].id
                            Log.d(SelectDeviceFragment::class.simpleName, "Selected device = ${newWidgetActivity().selectedDeviceId}")
                            newWidgetActivity().preselectedKeywords.clear()
                            preselectedKeywords.forEach {
                                if (it.deviceId == newWidgetActivity().selectedDeviceId)
                                    newWidgetActivity().preselectedKeywords.add(it)
                            }

                            findNavController().navigate(SelectDeviceFragmentDirections.actionDeviceFragmentToKeywordFragment())
                        }
                    }

                val yApi = YadomsApi(PreferenceManager.getDefaultSharedPreferences(activity))
                DeviceApi(yApi).getDeviceMatchKeywordCriteria(
                    activity,
                    expectedKeywordType = newWidgetActivity().selectedWidgetType!!.keywordFilter.expectedKeywordType,
                    expectedCapacity = newWidgetActivity().selectedWidgetType!!.keywordFilter.expectedCapacity,
                    expectedKeywordAccess = newWidgetActivity().selectedWidgetType!!.keywordFilter.expectedKeywordAccess,
                    onOk = { devices: List<DeviceApi.Device>, keywords: List<DeviceApi.Keyword> ->
                        devices.forEach { preselectedDevices.add(it) }
                        keywords.forEach { preselectedKeywords.add(it) }

                        adapter?.notifyDataSetChanged();
                    },
                    onError = {
                        if (activity != null)
                            Toast.makeText(
                                activity, "Unable to reach the server", Toast.LENGTH_SHORT
                            ).show()
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