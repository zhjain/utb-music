package com.zj.music.dto

data class ResponseBody (
//    var kind: String,
    var items: List<PlaylistItem>
)

data class PlaylistItem(
//    val kind: String,
//    val id: String,
    val snippet: ItemDetail,
//    val videoDetail: VideoDetail,
)

data class ItemDetail (
    val title:String,
//    val description:String,
//    val position:Int
)
data class VideoDetail(val id: String)
