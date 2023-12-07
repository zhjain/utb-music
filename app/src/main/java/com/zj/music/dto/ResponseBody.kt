package com.zj.music.dto

import java.io.Serializable

data class ResponseBody  (
//    var kind: String,
    var items: ArrayList<PlaylistItem>
):Serializable

data class PlaylistItem(
//    val kind: String,
    val id: String,
    val snippet: ItemDetail,
//    val videoDetail: VideoDetail,
):Serializable

data class ItemDetail (
    val title:String,
//    val description:String,
//    val position:Int
):Serializable
data class VideoDetail(val id: String):Serializable
