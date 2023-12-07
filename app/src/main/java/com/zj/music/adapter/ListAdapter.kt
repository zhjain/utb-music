package com.zj.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zj.music.R
import com.zj.music.dto.PlaylistItem

data class ListAdapter(val mDataList: ArrayList<PlaylistItem>) : BaseAdapter() {
    override fun getCount(): Int {
        return mDataList.size
    }

    override fun getItem(position: Int): Any {
        return mDataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.activity_playlist, parent, false)
        val item = mDataList[position]
//        view.findViewById<View>(R.id.iv_playlist_cover).setImageResource(item.cover)
//        view.findViewById<View>(R.id.tv_playlist_name).text = item.name
        return view
    }
}
