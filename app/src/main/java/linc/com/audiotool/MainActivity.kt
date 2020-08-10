package linc.com.audiotool

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*AudioTool.init(this)

        AudioTool.getAmplitudes {

        }*/

        waveform.setSampleFrom("/storage/9016-4EF8/MUSIC/Kygo - Only Us.mp3")

 }



}
