package linc.com.library;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.arthenica.mobileffmpeg.FFmpeg;
import java.io.File;
import java.io.IOException;
import java.util.Locale;


import linc.com.library.callback.OnFileComplete;
import linc.com.library.callback.OnNumberComplete;
import linc.com.library.types.Duration;
import linc.com.library.types.Echo;

import static linc.com.library.Constant.AUDIO_TOOL_TMP;

public class AudioTool {

    // Tool settings
    private Context context;
    private File audio;
    private String audioDirectory;
    private String imageDirectory;

    private AudioTool(Context context) {
        this.context = context;
        this.audioDirectory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_MUSIC)[0].getPath();
        this.imageDirectory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DCIM)[0].getPath();
    }

    public static AudioTool getInstance(Context context) {
        return new AudioTool(context);
    }

    /**
     * Set audio file source
     * @param sourceAudio path to the source file
     */
    public AudioTool withAudio(String sourceAudio) throws IOException {
        withAudio(new File(sourceAudio));
        return this;
    }

    public AudioTool withAudio(File sourceAudio) throws IOException {
        FileManager.validateInputFile(sourceAudio);
        this.audio = new File(
                audioDirectory
                        + File.separator
                        + AUDIO_TOOL_TMP
                        + System.currentTimeMillis()
                        + FileManager.getFileExtension(sourceAudio)
        );
        FileManager.copyFile(sourceAudio, audio);
        return this;
    }

    /**
     * Save output file
     * @param fullPath path to the output directory with file name
     */
    public AudioTool saveCurrentTo(String fullPath) throws IOException {
        FileManager.validateOutputFile(fullPath);
        FileManager.copyFile(audio, fullPath);
        return this;
    }

    /**
     * Remove temporary files from all previous sessions
     */
    public void release() {
        File tmpStorage = new File(audioDirectory);

        for(File tmpFile : tmpStorage.listFiles()) {
            if(tmpFile.getName().startsWith(AUDIO_TOOL_TMP))
                tmpFile.delete();
        }

        clearReferences();
    }

    /**
     * @param start format in second -> 0
     * @param end   format in second -> 0
     */
    public AudioTool cutAudio(int start, int end, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -ss %d -i %s -t %d",
                start, audio.getPath(), end
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param start format -> "00:00:00"
     * @param end   format -> "00:20:30"
     */
    public AudioTool cutAudio(String start, String end, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -ss %s -i %s -to %s",
                start, audio.getPath(), end
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param width      format with in px -> 1920
     * @param height     format height in px -> 1080
     * @param color      format hex color value -> #4a4a4a
     * @param outputPath path to source directory
     * @param onCompleteCallback lambda with result image
     */
    public AudioTool generateWaveform(int width,
                                      int height,
                                      String color,
                                      String outputPath,
                                      @Nullable OnFileComplete onCompleteCallback
    ) throws IOException {
        // ffmpeg -i input -filter_complex "aformat=channel_layouts=mono,showwavespic=colors=#ad9557" -frames:v 1 output.png
        //        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex \"aformat=channel_layouts=mono,showwavespic=s=" + width + "x" + height + ":colors=" + color + "\"" + outputPath);
        FileManager.validateOutputFile(outputPath);
        String command = String.format(Locale.US,
                "-y -i %s -filter_complex \"aformat=channel_layouts=mono,showwavespic=colors=%s:s=%dx%d\" -frames:v 1 %s",
                audio.getPath(), color, width, height, outputPath
        );
        FFmpegExecutor.executeCommand(command);
        if(onCompleteCallback != null) onCompleteCallback.onComplete(new File(outputPath));
        return this;
    }

    /**
     * @param volume     volume percent -> 0.75 as 75% of volume
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioVolume(float volume, int start, int end, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        volume = Limiter.limit(0f, 12000f, volume);
        start = Limiter.limit(0, ((int) getDurationMillis() / 1000), start);
        end = Limiter.limit(0, ((int) getDurationMillis() / 1000), end);

        String command = String.format(Locale.US,
                "-y -i %s -af volume=enable='between(t,%d,%d)':volume=%f",
                audio.getPath(), start, end, volume
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-af volume=enable='between(t," + start + "," + end + ")':volume=" + volume + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioVolume(float volume, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        volume = Limiter.limit(0f, 12000f, volume);

        String command = String.format(Locale.US,
                "-y -i %s -filter:a \"volume=%f\"",
                audio.getPath(), volume
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-filter:a \"volume=" + volume + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool normalizeAudioVolume(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -filter:a loudnorm",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + " -filter:a loudnorm" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioSpeed(float xSpeed, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        xSpeed = Limiter.limit(0.5f, 2, xSpeed);
        //
//        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=" + xSpeed + " " + audio.getPath());
//        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=" + xSpeed + " " + audio.getPath());
        String command = String.format(Locale.US,
                "-y -i %s -af atempo=%f",
                audio.getPath(), xSpeed
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=2 " + audio.getPath());// + " -af atempo=" + xSpeed + " " + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioPitch(int sampleRate, float pitch, float tempo, @Nullable OnFileComplete onCompleteCallback) throws IOException {
//        ffmpeg -y -i kygo.mp3 -filter_complex "asetrate=48000*2^(-10/12),atempo=1/2^(-10/12)" p_10_ky.mp3
        tempo = Limiter.limit(0.5f, 2, tempo);

        String command = String.format(Locale.US,
                "-y -i %s -filter_complex asetrate=%d*%f^(-10/12),atempo=%f^(-10/12)",
                audio.getPath(), sampleRate, pitch, tempo
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex asetrate=" + sampleRate + "*" + pitch + "^(-10/12),atempo=" + tempo + "^(-10/12)" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioBass(float bass, float width, int frequency, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        // ffmpeg -y -i kygo.mp3 -af bass=g=10:w=0.5:f=150 bass.mp3
        bass = Limiter.limit(-20f, 20f, bass);
        width = Limiter.limit(0f, 1f, width);
        frequency = Limiter.limit(0, 999999, frequency);

        String command = String.format(Locale.US,
                "-y -i %s -af bass=g=%f:w=%f:f=%d",
                audio.getPath(), bass, width, frequency
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-af" + "bass=g=" + bass + ":w=" + width + ":f=" + frequency + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool removeAudioNoise(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        filterAudio(400, 4000, onCompleteCallback);
        return this;
    }

    public AudioTool removeVocal(@Nullable OnFileComplete onCompleteCallback) throws IOException {
//        ffmpeg -i song.mp3 -af pan="stereo|c0=c0|c1=-1*c1" -ac 1 karaoke.mp3

        String command = String.format(Locale.US,
                "-y -i %s -af pan=\"stereo|c0=c0|c1=-1*c1\" -ac 1",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-af pan=\"stereo|c0=c0|c1=-1*c1\" -ac 1" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool filterAudio(int highpass, int lowpass, @Nullable OnFileComplete onCompleteCallback) throws IOException  {
        highpass = Limiter.limit(0, 999999, highpass);
        lowpass = Limiter.limit(0, 999999, lowpass);

        String command = String.format(Locale.US,
                "-y -i %s -af \"highpass=f=%d, lowpass=f=%d\"",
                audio.getPath(), highpass, lowpass
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-af \"highpass=f=" + highpass + ", lowpass=f=" + lowpass + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool reverseAudio(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -map 0 -c:v copy -af \"areverse\"",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-map 0 -c:v copy -af \"areverse\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyEchoEffect(Echo echo, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        String filter;
        switch (echo) {
            case ECHO_TWICE_INSTRUMENTS: filter = "\"aecho=0.8:0.88:60:0.4\""; break;
            case ECHO_METALLIC: filter = "\"aecho=0.8:0.88:6:0.4\""; break;
            case ECHO_OPEN_AIR: filter = "\"aecho=0.8:0.9:1000:0.3\""; break;
            default: filter = "\"aecho=0.8:0.9:1000|1800:0.3|0.25\""; break;
        }

        String command = String.format(Locale.US,
                "-y -i %s -filter_complex %s",
                audio.getPath(), filter
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex" + filter  + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    /*
    @Deprecated
    public AudioTool applyVibratoEffect(float frequency, float depth, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        frequency = Limiter.limit(0.1f, 20000f, frequency);
        depth = Limiter.limit(0f, 1f, depth);
        String command = String.format(Locale.US,
                "-y -i %s -filter_complex vibrato=f=%f:d=%f",
                audio.getPath(), frequency, depth
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }
    */

    public AudioTool applyReverbEffect(float audioDepth, float reverbDepth, @Nullable OnFileComplete onCompleteCallback) throws IOException {
//        ffmpeg -y -i kygo.mp3 -i lev_cut.mp3 -filter_complex '[0] [1] afir=dry=0.1:wet=0.1' reverb.mp3
        audioDepth = Limiter.limit(0f, 1f, audioDepth);
        reverbDepth = Limiter.limit(0f, 1f, reverbDepth);

        File reverbEffect = FileManager.bufferAssetAudio(context, audioDirectory, "audiotool_ir_reverb.mp3");

        String command = String.format(Locale.US,
                "-y -i %s -i %s -filter_complex '[0] [1] afir=dry=%f:wet=%f'",
                audio.getPath(), reverbEffect.getPath(), audioDepth, reverbDepth
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);


//        FFmpeg.execute("-y -i" + audio.getPath() + "-i" + effectPath + "-filter_complex '[0] [1] afir=dry=" + audioDepth + ":wet=" + reverbDepth + "'" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyShifterEffect(int transitionTime, float width, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        // ffmpeg -y -i kygo.mp3 -filter_complex "apulsator=mode=sine:hz=0.125:width=0" shifter.mp3
        float hz =  1/ (float) transitionTime;
        hz = Limiter.limit(0.01f, 100f, hz);
        width = Limiter.limit(0.1f, 2f, width);

        String command = String.format(Locale.US,
                "-y -i %s -filter_complex \"apulsator=mode=sine:hz=%f:width=%f\"",
                audio.getPath(), hz, width
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex \"apulsator=mode=sine:hz=" + hz + ":width=" + width + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool convertVideoToAudio(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -vn",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);

//        FFmpeg.execute("-y -i" + audio.getPath() + "-vn" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }


    public void joinAudios(File[] audios, String outputPath, OnFileComplete onCompleteCallback) throws IOException {
        joinAudios(FileManager.getPathFromFiles(audios), outputPath, onCompleteCallback);
    }

    public AudioTool joinAudios(String[] audios, String outputPath, OnFileComplete onCompleteCallback) throws IOException {
        //      ffmpeg -f concat -safe 0 -i join.txt -c copy output.mp4

        String joinPath = audioDirectory + File.separator + AUDIO_TOOL_TMP + "join.txt";
        StringBuilder joinData = new StringBuilder();
        for(String path : audios) {
            joinData.append("file ")
              .append(path)
              .append("\n");
        }

        if(outputPath.isEmpty()) outputPath = audioDirectory + File.separator + AUDIO_TOOL_TMP + System.currentTimeMillis();

        FileManager.writeFile(joinPath, joinData.toString());
        FFmpeg.execute("-y -f concat -safe 0 -i " + joinPath + " -c copy " + outputPath);
        if(onCompleteCallback != null) onCompleteCallback.onComplete(new File(outputPath));
        return this;
    }

    public AudioTool getDuration(Duration duration, OnNumberComplete onNumberComplete) {
        switch (duration) {
            case MILLIS: onNumberComplete.onComplete(getDurationMillis());
                break;
            case SECONDS: onNumberComplete.onComplete(getDurationMillis() / 1000);
                break;
            case MINUTES: onNumberComplete.onComplete((getDurationMillis() % (1000 * 60 * 60)) / (1000 * 60));
                break;
        }
        return this;
    }

    public AudioTool executeFFmpeg(String... command) {
        FFmpeg.execute(command);
        return this;
    }

    private long getDurationMillis() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(audio.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }

    private void onResultFile(@Nullable OnFileComplete onCompleteCallback) {
        if(onCompleteCallback != null) {
            onCompleteCallback.onComplete(audio);
        }
    }

    private void clearReferences() {
        audio = null;
        audioDirectory = null;
        context = null;
    }

}
