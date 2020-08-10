package linc.com.library

import android.content.Context
import androidx.annotation.Nullable
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg
import java.io.File
import java.io.FileNotFoundException

object AudioTool {

    private lateinit var ffmpeg: FFmpeg

    // Tool settings
    private lateinit var audio: File
    private lateinit var outputDirectory: String

    fun init(context: Context) {
        ffmpeg = FFmpeg.getInstance(context)
    }

    /**
     * Set audio file source
     * @param path path to the source file
     */
    fun withAudio(audio: File): AudioTool {
        if(!audio.exists()) throw FileNotFoundException()
        this.audio = audio

        return this
    }

    /**
     * Save output file
     * @param outputDirectory path to the output directory
     */
    fun withOutputDirectory(outputDirectory: String): AudioTool {

        return this
    }

    /**
     * Release data
     */
    fun release(saveAudio: Boolean) {

    }

    /**
     * @param start format in second -> 0
     * @param end   format in second -> 0
     */
    fun cutAudio(start: Int, end: Int, onComplete: ((audio: File) -> Unit)? = null): AudioTool {

        return this
    }

    /**
     * @param start format -> "00:00:00"
     * @param end   format -> "00:20:30"
     * "mm:ss:ms"
     */
    fun cutAudio(start: String, end: String, onComplete: ((audio: File) -> Unit)? = null): AudioTool {

        return this
    }

    /**
     * @param format     format output image format -> Image.PNG
     * @param width      format with in px -> 1920
     * @param height     format height in px -> 1080
     * @param color      format hex color value -> #4a4a4a
     * @param onComplete lambda with result image
     */
    fun generateWaveform(format: String = "todo",
                         width: Int,
                         height: Int,
                         color: String,
                         onComplete: (waveform: File) -> Unit
    ): AudioTool {

        return this
    }

    /**
     * @param onComplete lambda with result byte array
     */
    fun getAmplitudes(onComplete: (amplitudes: ByteArray) -> Unit): AudioTool {

        val file = File("/storage/emulated/0/viber/kygo.wav")
//        val wav =  WaveFile()
//
//        wav.extractAmplitudeFromFile(file).forEach {
//            print(" it")
//        }

        println()


        /*println(ffmpeg.isSupported)

        execFFmpegBinary(arrayOf(
            "-i", "/storage/9016-4EF8/MUSIC/Kygo - Only Us.mp3", "/storage/emulated/0/viber/kygo.wav"
        ))*/

        return this
    }

    /**
     * @param volume     volume percent -> 0.75 as 75% of volume
     * @param onComplete lambda with result audio
     */
    fun changeAudioVolume(volume: Float, start: Int = 0, end: Int = 0, onComplete: ((audio: File) -> Unit)? = null): AudioTool {
        /*
            If we want our volume to be half of the input volume:

            ffmpeg -i input.wav -filter:a "volume=0.5" output.wav
            150% of current volume:

            ffmpeg -i input.wav -filter:a "volume=1.5" output.wav


            // normalize
            ffmpeg -i input.wav -filter:a loudnorm output.wav
         */

        // todo start and end ffmpeg -y -i kygo.mp3 -af "volume=enable='between(t,5,10)':volume=0" muted.mp3
        return this
    }

    fun changeAudioSpeed(xSpeed: Float, onComplete: ((audio: File) -> Unit)? = null): AudioTool {
//        ffmpeg -i pre.mp3 -filter:a "atempo=2.0" -vn tempoout.mp3
        return this
    }

    fun changeAudioPitch(): AudioTool {
//        ffmpeg -y -i kygo.mp3 -filter_complex "asetrate=48000*2^(-10/12),atempo=1/2^(-10/12)" p_10_ky.mp3
        return this
    }

    fun changeAudioBass(bass: Int/*[-20;20]*/, width: Float/*[0;1]*/, frequency: Int): AudioTool {
        // ffmpeg -y -i kygo.mp3 -af bass=g=10:w=0.5:f=150 bass.mp3
        return this
    }

    fun removeAudioNoise(): AudioTool {
        // todo filterAudio(400, 4000)
        return this
    }

    fun removeVocal() {
//        ffmpeg -i song.mp3 -af pan="stereo|c0=c0|c1=-1*c1" -ac 1 karaoke.mp3
    }

    fun filterAudio(highpass: Int, lowpass: Int): AudioTool {
//        ffmpeg -y -i kygo.mp3 -af "highpass=f=400, lowpass=f=4000" noise.mp3
//        ffmpeg -y -i kygo.mp3 -af "bandreject=f=900:width_type=h:w=600, bandreject=f=900:width_type=h:w=600" instr.mp3
        return this
    }

    fun reverseAudio(): AudioTool {
//        ffmpeg -i levels.mp3 -map 0 -c:v copy -af "areverse" reversed_levels.mp3
        return this
    }

    fun applyEchoEffect(/*type*/): AudioTool {

        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.88:60:0.4" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.88:6:0.4" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.9:1000:0.3" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.9:1000|1800:0.3|0.25" echo.mp3

        return this
    }

    fun applyVibratoEffect(frequency: Int/*[0.1 - 20000.0]*/, depth: Float/*[0;1]*/): AudioTool {

        // ffmpeg -y -i kygo.mp3 -filter_complex "vibrato=f=4:d=0.9" vibrato.mp3

        return this
    }

    fun applyReverbEffect(audioDepth: Float, reverbDepth: Float): AudioTool {

//        ffmpeg -y -i kygo.mp3 -i lev_cut.mp3 -filter_complex '[0] [1] afir=dry=0.1:wet=0.1' reverb.mp3

        return this
    }

    fun applyShifterEffect(transitionTime: Int, width: Int/*[0;2]*/): AudioTool {

        // ffmpeg -y -i kygo.mp3 -filter_complex "apulsator=mode=sine:hz=0.125:width=0" shifter.mp3

        // hz = 1/transitionTime

        //
        //   1
        // ----- = 7.8
        // 0.128
        //

        //
        //   1      7.8
        // ----- = -----
        //   x       1
        //

        //
        //   1      7.8
        // ----- = -----
        //   x       1
        //

        return this
    }

    fun joinAudios(vararg audio: File) {
//        ffmpeg -f concat -safe 0 -i join.txt -c copy output.mp4
    }




    private fun execFFmpegBinary(command: Array<String>) {
        try {
            ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
                override fun onFailure(s: String?) {
                    println("Fail $s")
                }
                override fun onSuccess(s: String?) {
                    println("Success $s")
                }
                override fun onProgress(s: String?) {
                    println("Progress $s")
                }
                override fun onStart() {
                    println("Start")
                }
                override fun onFinish() {
                    println("Finish")
                }
            })
        } catch (e: Exception) {}
    }
}