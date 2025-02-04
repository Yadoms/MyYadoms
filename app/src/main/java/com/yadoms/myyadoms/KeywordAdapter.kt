package com.yadoms.myyadoms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.yadomsApi.DeviceApi

class KeywordAdapter(
    private var allKeywords: List<DeviceApi.Keyword>,
    private var allDevices: List<DeviceApi.Device>,
    private val listener: OnKeywordSelectedListener
) :
    RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder>() {

    private var filteredKeywords: List<DeviceApi.Keyword> = allKeywords
    private lateinit var context: Context;
    private var selectedKeywordId: Int? = null

    interface OnKeywordSelectedListener {
        fun onKeywordSelected(keywordId: Int?)
    }

    class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton: RadioButton = itemView.findViewById(R.id.keyword_radio_button)
        val nameTextView: TextView = itemView.findViewById(R.id.keyword_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.keyword_item, parent, false)
        return KeywordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        val keyword = filteredKeywords[position]

        holder.nameTextView.text = buildKeywordLabel(keyword)
        holder.radioButton.isChecked = keyword.id == selectedKeywordId

        holder.itemView.setOnClickListener {
            selectedKeywordId = keyword.id
            listener.onKeywordSelected(keyword.id)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = filteredKeywords.size

    fun filter(query: String) {
        filteredKeywords = allKeywords.filter { keyword ->
            buildKeywordLabel(keyword).contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }

    private fun buildKeywordLabel(kw: DeviceApi.Keyword): String {
        val keyword = filteredKeywords.find { it.id == kw.id }
        val device = allDevices.find { it.id == keyword?.deviceId }

        return buildString {
            append(device?.friendlyName ?: context.getString(R.string.unknown_device))
            append(" - ")
            append(keyword?.friendlyName)
        }
    }

    fun setKeywords(
        newKeywords: List<DeviceApi.Keyword>,
        newDevices: List<DeviceApi.Device>,
        currentlySelectedKeywordId: Int? = null
    ) {
        allDevices = newDevices
        allKeywords = newKeywords
        filteredKeywords = allKeywords
        selectedKeywordId = currentlySelectedKeywordId
        notifyDataSetChanged()
    }
}