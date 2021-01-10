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

/**
 * A fragment representing a list of Items.
 */
class SelectDeviceFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_select_device, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val onItemClickListener =
                    object : SelectDeviceRecyclerViewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            Log.d(SelectDeviceFragment::class.simpleName, "Position = $position")
                            findNavController().navigate(SelectDeviceFragmentDirections.actionDeviceFragmentToKeywordFragment())
                        }
                    }

                val list: MutableList<DeviceApi.Device> = ArrayList()

                val yApi = YadomsApi(PreferenceManager.getDefaultSharedPreferences(activity))
                DeviceApi(yApi).getDeviceMatchKeywordCriteria(
                    activity,
                    expectedCapacity = arrayOf("switch"),
                    onOk = {
                        it.forEach { device -> list.add(device) }

                        adapter?.notifyDataSetChanged();
                    },
                    onError = {
                        if (activity != null)
                            Toast.makeText(
                                activity, "Unable to reach the server", Toast.LENGTH_SHORT
                            ).show()
                    })

                adapter = SelectDeviceRecyclerViewAdapter(list, onItemClickListener)
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