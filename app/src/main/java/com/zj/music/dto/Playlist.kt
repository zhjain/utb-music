package com.zj.music.dto

import java.io.Serializable

data class Playlist(val items:ArrayList<Item>) :Serializable


data class Item(val player: Player):Serializable

data class Player(val embedHtml:String):Serializable
