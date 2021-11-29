package com.yadoms.myyadoms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yadoms.myyadoms.databinding.FragmentSetNameBinding

class SetNameFragment : Fragment() {

    private lateinit var binding: FragmentSetNameBinding

    private val newWidgetActivity: NewWidgetActivity
        get() = activity as NewWidgetActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetNameBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.itemName.hint = getString(R.string.default_widget_name, newWidgetActivity.selectedKeywordName, newWidgetActivity.selectedDeviceName)

        binding.finish.setOnClickListener {
            val name = binding.itemName.text.toString().ifEmpty { binding.itemName.hint } as String
            Log.d(SelectDeviceFragment::class.simpleName, "Set widget name = $name)")
            newWidgetActivity.finish(name)
        }

        return view
    }
}