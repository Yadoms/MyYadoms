package com.yadoms.myyadoms

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.preferences.SelectKeywordPreference
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.DeviceApi.StandardCapacities
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class SelectKeywordPreferenceDialogFragment : PreferenceDialogFragmentCompat(), KeywordAdapter.OnKeywordSelectedListener {

    private lateinit var adapter: KeywordAdapter
    private var allDevices: List<DeviceApi.Device> = listOf()
    private var allKeywords: List<DeviceApi.Keyword> = listOf()
    private var selectedKeywordId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DeviceApi(YadomsApi(requireContext())).getDeviceMatchKeywordCriteria(
            expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool),
            expectedCapacity = arrayOf(StandardCapacities.switch),
            expectedKeywordAccess = arrayOf(DeviceApi.KeywordAccess.GetSet),
            onOk = { devices: List<DeviceApi.Device>, keywords: List<DeviceApi.Keyword> ->
                allDevices = devices
                allKeywords = keywords

                if (allKeywords.isNotEmpty()) {
                    val persistedValue = (preference as SelectKeywordPreference).getPersistedKeywordId(allKeywords[0].id)
                    selectedKeywordId = (allKeywords.find { it.id == persistedValue } ?: allKeywords[0]).id
                }
                adapter.setKeywords(allKeywords, allDevices, selectedKeywordId)
            },
            onError = {
            }
        )
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.keyword_recycler_view)
        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = KeywordAdapter(allKeywords, allDevices, this)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterKeywords(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterKeywords(query: String) {
        adapter.filter(query)
    }

    override fun onKeywordSelected(keywordId: Int?) {
        selectedKeywordId = keywordId
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val preference = preference as? SelectKeywordPreference
            preference?.let { pref ->
                selectedKeywordId?.let {
                    if (pref.callChangeListener(it)) {
                        pref.persistKeywordId(it)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(key: String): SelectKeywordPreferenceDialogFragment {
            val fragment = SelectKeywordPreferenceDialogFragment()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}