package linc.com.library;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;

public class AudioTool {

    // /storage/emulated/0/viber/kygo.mp3

    private Context context;

    // Tool settings
    private File audio;
    private String outputDirectory;

    private AudioTool(Context context) {
        this.context = context;
    }

    static AudioTool getInstance(Context context) {
        return new AudioTool(context);
    }

    /**
     * Set audio file source
     * @param audio path to the source file
     */
    AudioTool withAudio(File audio) throws FileNotFoundException {
        if(!audio.exists()) throw new FileNotFoundException();
        this.audio = audio;

        return this;
    }

    /**
     * Save output file
     * @param outputDirectory path to the output directory
     */
    AudioTool withOutputDirectory(String outputDirectory) {

        return this;
    }

    /**
     * Release data
     */
    void release(boolean saveAudio) {

    }

    /**
     * @param start format in second -> 0
     * @param end   format in second -> 0
     */
    AudioTool cutAudio(int start, int end, OnFileComplete onCompleteCallback) {

        return this;
    }

    /**
     * @param start format -> "00:00:00"
     * @param end   format -> "00:20:30"
     * "mm:ss:ms"
     */
    AudioTool cutAudio(String start, String end, OnFileComplete onCompleteCallback) {

        return this;
    }

    /**
     * @param format     format output image format -> Image.PNG
     * @param width      format with in px -> 1920
     * @param height     format height in px -> 1080
     * @param color      format hex color value -> #4a4a4a
     * @param onCompleteCallback lambda with result image
     */
    AudioTool generateWaveform(String format,
                               int width,
                               int height,
                               String color,
                               OnFileComplete onCompleteCallback
    ) {

        return this;
    }

    /**
     * @param volume     volume percent -> 0.75 as 75% of volume
     * @param onCompleteCallback lambda with result audio
     */
    AudioTool changeAudioVolume(float volume, int start, int end, OnFileComplete onCompleteCallback) {
        /*
            If we want our volume to be half of the input volume:

            ffmpeg -i input.wav -filter:a "volume=0.5" output.wav
            150% of current volume:

            ffmpeg -i input.wav -filter:a "volume=1.5" output.wav


            // normalize
            ffmpeg -i input.wav -filter:a loudnorm output.wav
         */

        // todo start and end ffmpeg -y -i kygo.mp3 -af "volume=enable='between(t,5,10)':volume=0" muted.mp3
        return this;
    }

    AudioTool changeAudioSpeed(Float xSpeed, OnFileComplete onCompleteCallback) {
//        ffmpeg -i pre.mp3 -filter:a "atempo=2.0" -vn tempoout.mp3
        changeAudioSpeed(xSpeed);
        onCompleteCallback.onComplete(audio);
        return this;
    }

    AudioTool changeAudioSpeed(Float xSpeed) {
//        ffmpeg -i pre.mp3 -filter:a "atempo=2.0" -vn tempoout.mp3
        return this;
    }

    AudioTool changeAudioPitch() {
        //							rep---	the same   rep---
//        ffmpeg -y -i kygo.mp3 -filter_complex "asetrate=48000*2^(-10/12),atempo=1/2^(-10/12)" p_10_ky.mp3
        return this;
    }

    AudioTool changeAudioBass(int bass/*[-20;20]*/, float width/*[0;1]*/, int frequency) {
        // ffmpeg -y -i kygo.mp3 -af bass=g=10:w=0.5:f=150 bass.mp3
        return this;
    }

    AudioTool removeAudioNoise() {
        // todo filterAudio(400, 4000)
        return this;
    }

    void removeVocal() {
//        ffmpeg -i song.mp3 -af pan="stereo|c0=c0|c1=-1*c1" -ac 1 karaoke.mp3
    }

    AudioTool filterAudio(int highpass, int lowpass) {
//        ffmpeg -y -i kygo.mp3 -af "highpass=f=400, lowpass=f=4000" noise.mp3
//        ffmpeg -y -i kygo.mp3 -af "bandreject=f=900:width_type=h:w=600, bandreject=f=900:width_type=h:w=600" instr.mp3
        return this;
    }

    AudioTool reverseAudio() {
//        ffmpeg -i levels.mp3 -map 0 -c:v copy -af "areverse" reversed_levels.mp3
        return this;
    }

    AudioTool applyEchoEffect(/*type*/) {

        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.88:60:0.4" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.88:6:0.4" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.9:1000:0.3" echo.mp3
        // ffmpeg -y -i kygo.mp3 -filter_complex "aecho=0.8:0.9:1000|1800:0.3|0.25" echo.mp3

        return this;
    }

    AudioTool applyVibratoEffect(int frequency/*[0.1 - 20000.0]*/, float depth/*[0;1]*/) {

        // ffmpeg -y -i kygo.mp3 -filter_complex "vibrato=f=4:d=0.9" vibrato.mp3

        return this;
    }

    AudioTool applyReverbEffect(float audioDepth, float reverbDepth) {

//        ffmpeg -y -i kygo.mp3 -i lev_cut.mp3 -filter_complex '[0] [1] afir=dry=0.1:wet=0.1' reverb.mp3

        return this;
    }

    AudioTool applyShifterEffect(int transitionTime, int width/*[0;2]*/) {

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

        return this;
    }

    void joinAudios(File ... audios  ) {
//        ffmpeg -f concat -safe 0 -i join.txt -c copy output.mp4
    }

    interface OnCompleteCallback<T> {
        void onComplete(T output);
    }

    interface OnFileComplete extends OnCompleteCallback<File> {}

}
