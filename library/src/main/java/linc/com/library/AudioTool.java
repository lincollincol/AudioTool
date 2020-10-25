package linc.com.library;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.Level;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import linc.com.library.callback.OnFileComplete;
import linc.com.library.callback.OnNumberComplete;
import linc.com.library.callback.OnListComplete;
import linc.com.library.types.Duration;
import linc.com.library.types.Echo;
import linc.com.library.types.Pitch;

import static linc.com.library.Constant.AUDIO_TOOL_LOCAL_EFFECT_REVERB;
import static linc.com.library.Constant.AUDIO_TOOL_LOCAL_JOIN_FILE;
import static linc.com.library.Constant.AUDIO_TOOL_RATE_PER_FRAME;
import static linc.com.library.Constant.AUDIO_TOOL_TMP;

public class AudioTool {

    private Context context;
    private File audio;
    private String audioDirectory;

    private AudioTool(Context context) {
        this.context = context;
        this.audioDirectory = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_MUSIC)[0].getPath();
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
     * @param start     format -> "00:01:00"
     * @param duration  format -> "00:00:30"
     * @example Cut 30 seconds audio from 1:00 min. Start 1 min / end 1:30
     */
    public AudioTool cutAudio(String start, String duration, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -ss %s -i %s -to %s",
                start, audio.getPath(), duration
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param width              format with in px -> 1920
     * @param height             format height in px -> 1080
     * @param color              format hex color value -> #4a4a4a
     * @param outputPath         path to source directory
     * @param onCompleteCallback lambda with result image
     */
    public AudioTool generateWaveform(int width,
                                      int height,
                                      String color,
                                      String outputPath,
                                      @Nullable OnFileComplete onCompleteCallback
    ) throws IOException {
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
     * @param volume             volume percent -> 0.75 as 75% of volume
     * @param start              start position in seconds
     * @param end                end position in seconds
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
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param volume             volume percent -> 0.75 as 75% of volume
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioVolume(float volume, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        volume = Limiter.limit(0f, 12000f, volume);
        String command = String.format(Locale.US,
                "-y -i %s -filter:a \"volume=%f\"",
                audio.getPath(), volume
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool normalizeAudioVolume(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -filter:a loudnorm",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param xSpeed             new audio speed
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioSpeed(float xSpeed, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        xSpeed = Limiter.limit(0.5f, 2, xSpeed);
        String command = String.format(Locale.US,
                "-y -i %s -af atempo=%f",
                audio.getPath(), xSpeed
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param sampleRate         audio sample rate
     * @param deltaRate          audio custom rate value
     * @param deltaTempo         audio custom tempo value
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioPitch(int sampleRate, float deltaRate, float deltaTempo, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        deltaTempo = Limiter.limit(-12f, 12f, deltaTempo);
        deltaRate = Limiter.limit(-12f, 12f, deltaRate);

        String command = String.format(Locale.US,
                "-y -i %s -filter_complex asetrate=%d*2^(%f/12),atempo=1/2^(%f/12)",
                audio.getPath(), sampleRate, deltaRate, deltaTempo
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param sampleRate         audio sample rate
     * @param pitch              audio semitone value
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioPitch(int sampleRate, float pitch, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        changeAudioPitch(sampleRate, pitch, pitch, onCompleteCallback);
        return this;
    }

    /**
     * @param sampleRate         audio sample rate
     * @param pitchValue         audio semitone value
     * @param pitch              pitch type: UP or DOWN
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioPitch(int sampleRate, float pitchValue, Pitch pitch, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        pitchValue = pitch == Pitch.UP ? Math.abs(pitchValue) : -Math.abs(pitchValue);
        changeAudioPitch(sampleRate, pitchValue, pitchValue, onCompleteCallback);
        return this;
    }

        /**
         * @param bass               audio bass
         * @param width              audio bass width
         * @param frequency          audio frequency
         * @param onCompleteCallback lambda with result audio
         */
    public AudioTool changeAudioBass(float bass, float width, int frequency, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        bass = Limiter.limit(-20f, 20f, bass);
        width = Limiter.limit(0f, 1f, width);
        frequency = Limiter.limit(0, 999999, frequency);

        String command = String.format(Locale.US,
                "-y -i %s -af bass=g=%f:w=%f:f=%d",
                audio.getPath(), bass, width, frequency
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool removeAudioNoise(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        filterAudio(400, 4000, onCompleteCallback);
        return this;
    }

    /**
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool removeVocal(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -af pan=\"stereo|c0=c0|c1=-1*c1\" -ac 1",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param highpass           high pass of audio
     * @param lowpass            low pass of audio
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool filterAudio(int highpass, int lowpass, @Nullable OnFileComplete onCompleteCallback) throws IOException  {
        highpass = Limiter.limit(0, 999999, highpass);
        lowpass = Limiter.limit(0, 999999, lowpass);

        String command = String.format(Locale.US,
                "-y -i %s -af \"highpass=f=%d, lowpass=f=%d\"",
                audio.getPath(), highpass, lowpass
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool reverseAudio(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -map 0 -c:v copy -af \"areverse\"",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param echo               echo type (more in the enum Echo)
     * @param onCompleteCallback lambda with result audio
     */
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
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param audioDepth         audio domination
     * @param reverbDepth        reverb effect domination
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool applyReverbEffect(float audioDepth, float reverbDepth, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        audioDepth = Limiter.limit(0f, 1f, audioDepth);
        reverbDepth = Limiter.limit(0f, 1f, reverbDepth);

        File reverbEffect = FileManager.bufferAssetAudio(context, audioDirectory, AUDIO_TOOL_LOCAL_EFFECT_REVERB);
        String command = String.format(Locale.US,
                "-y -i %s -i %s -filter_complex '[0] [1] afir=dry=%f:wet=%f'",
                audio.getPath(), reverbEffect.getPath(), audioDepth, reverbDepth
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param transitionTime     shifter effect timing
     * @param width              shifter effect width
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool applyShifterEffect(int transitionTime, float width, @Nullable OnFileComplete onCompleteCallback) throws IOException {
        float hz =  1 / (float) transitionTime;
        hz = Limiter.limit(0.01f, 100f, hz);
        width = Limiter.limit(0.1f, 2f, width);

        String command = String.format(Locale.US,
                "-y -i %s -filter_complex \"apulsator=mode=sine:hz=%f:width=%f\"",
                audio.getPath(), hz, width
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool convertVideoToAudio(@Nullable OnFileComplete onCompleteCallback) throws IOException {
        String command = String.format(Locale.US,
                "-y -i %s -vn",
                audio.getPath()
        );
        FFmpegExecutor.executeCommandWithBuffer(command, audio);
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param audios             audio that will be merged
     * @param outputPath         output audio file
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool joinAudios(String[] audios, String outputPath, OnFileComplete onCompleteCallback) throws IOException {
        // Validate output file path
        FileManager.validateOutputFile(outputPath);

        String joinPath = audioDirectory + File.separator + AUDIO_TOOL_TMP + AUDIO_TOOL_LOCAL_JOIN_FILE;
        StringBuilder joinData = new StringBuilder();

        // Create files sequence command
        for(String path : audios) {
            joinData.append("file ")
              .append(path)
              .append("\n");
        }

        // Copy audio effect to internal storage
        FileManager.writeFile(joinPath, joinData.toString());

        String command = String.format(Locale.US,
                "-y -f concat -safe 0 -i %s -c copy %s",
                joinPath, outputPath
        );

        FFmpeg.execute(command);
        if(onCompleteCallback != null) onCompleteCallback.onComplete(new File(outputPath));
        return this;
    }

    public void joinAudios(File[] audios, String outputPath, OnFileComplete onCompleteCallback) throws IOException {
        joinAudios(FileManager.getPathFromFiles(audios), outputPath, onCompleteCallback);
    }

    /**
     * @param duration         time unit
     * @param onNumberComplete lambda with result audio
     */
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

    /**
     * @param fps            frames per second in the result data
     * @param outputPath     txt output file path
     * @param onListComplete result callback
     */
    public AudioTool getMaxLevelData(int fps, String outputPath, OnListComplete onListComplete) throws IOException {
        if(outputPath != null) FileManager.validateOutputFile(outputPath);
        fps = Limiter.limit(1, 20, fps);

        String command = String.format(Locale.US,"-f lavfi -i amovie=%s,asetnsamples=%d,astats=metadata=1:reset=1 -show_entries frame_tags=lavfi.astats.Overall.MAX_level -of csv=p=0",
                audio.getPath(),
                (AUDIO_TOOL_RATE_PER_FRAME / fps)
        );

        // Setup log
        Config.enableRedirection();
        Config.setLogLevel(Level.AV_LOG_QUIET);

        FFprobe.execute(command);

        // Get data from log
        String log = Config.getLastCommandOutput();

        if(outputPath != null) FileManager.writeFile(outputPath, log);
        if(onListComplete == null) return this;

        // Prepare audio data as list
        List<Float> audioData = new ArrayList<>();
        for (String temp : log.split("\n")) {
            audioData.add(temp.isEmpty() ? 0 : Float.parseFloat(temp));
        }
        onListComplete.onComplete(audioData);
        return this;
    }

    /**
     * @param command ffmpeg command
     */
    public AudioTool executeFFmpeg(String command) {
        FFmpeg.execute(command);
        return this;
    }

    /**
     * @param command ffprobe command
     */
    public AudioTool executeFFprobe(String command) {
        FFprobe.execute(command);
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

    /* This function is deprecated now because of bugs. Will be fixed soon.
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
}
