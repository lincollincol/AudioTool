package linc.com.audiotool

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import linc.com.library.AudioTool
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*waveform.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var percent = motionEvent.x/waveform.width
                    waveform.updatePlayerPercent(percent)
                    player!!.seekTo((percent*player!!.duration).toInt())

                }
            }
            return@setOnTouchListener true
        }*/

        AudioTool.init(this)

        AudioTool.getAmplitudes {

        }

    }


    private var pauseProgress = 0
    private var player: MediaPlayer? = MediaPlayer()

    private fun onPlay(start: Boolean, filePath: String) = if (start) {
        startPlaying(filePath)
    } else {
        pause()
    }

    private fun startPlaying(filePath: String) {
        player = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
        }
        player!!.start()
        player!!.seekTo(pauseProgress)
        initProgress()
    }

    private fun initProgress() {
        Observable.interval(1, TimeUnit.MILLISECONDS)
            .takeWhile {
                player!!.currentPosition.toFloat()/player!!.duration.toFloat() < 1f
            }
            .doOnNext {
                waveform.updatePlayerPercent(
                    player!!.currentPosition.toFloat()/player!!.duration.toFloat()
                )
                println((player!!.currentPosition/player!!.duration))
            }
            .doOnComplete {
                pauseProgress = 0
                player!!.seekTo(0)
                waveform.updatePlayerPercent(0f)
                println("COMPLETE")
            }
            .subscribe()
    }

    private fun pause() {
        player?.pause()
        pauseProgress = player?.currentPosition ?: 0
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    // 70 - 10c
    //  x - 200c

    // 8723
    //------ = 1400
    //  x

    // 8723     1400
    //------ = ------
    //  7        1


    fun compress(arr: IntArray): ByteArray {
        val resAverage = mutableListOf<Byte>()
        var sum: Byte = 0
        arr.map { (if(it < 0) -1*it else it).toByte() }
            .toByteArray()
            .forEachIndexed { index, value ->
                if(index % 56 == 0) {
                    resAverage.add((sum/7).toByte())
                    sum = 0
                } else sum = (sum + value).toByte()
            }
        return resAverage.toByteArray()
    }
}
