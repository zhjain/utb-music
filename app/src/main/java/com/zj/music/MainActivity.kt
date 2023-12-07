package com.zj.music

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zj.music.databinding.ActivityMainBinding
import com.zj.music.dto.Playlist
import com.zj.music.dto.ResponseBody
import com.zj.music.ui.theme.MusicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import rxhttp.toFlow
import rxhttp.wrapper.param.RxHttp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val token = "AIzaSyBVCOaK_vhgfKhOI_Uu8YR1IijEYp6a6Wg"
//        binding.playlistId.set = "PLRTW6h03whLlKyanwalcNMkf-FSXWfldn"
        binding.queryButton.setOnClickListener {
            Log.e("myTest",binding.playlistId.text.toString())
            Toast.makeText(this,binding.playlistId.text.toString(),Toast.LENGTH_SHORT).show()

            runBlocking {
                launch(this.coroutineContext) {
                    RxHttp.get("https://youtube.googleapis.com/youtube/v3/playlists")
                        .addQuery("part", "snippet")
                        .addQuery("part", "contentDetails")
                        .addQuery("part","player")
                        .addQuery("id", binding.playlistId.text.toString())
                        .addQuery("maxResults",5000)
                        .addQuery("key", token)
                        .toFlow<Playlist>().catch {
                            val throwable = it
                            Log.e("myTest", throwable.message.toString())
                        }.collect {
                            Log.i("myTest", it.items.size.toString())
                            val intent: Intent = Intent(this@MainActivity, PlaylistActivity::class.java)
                            intent.putExtra("playlist",it)
                            startActivity(intent)
//                            finish()
                        }
 }
            }


        }
    }
}
