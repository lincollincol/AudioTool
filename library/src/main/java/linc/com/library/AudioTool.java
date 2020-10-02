package linc.com.library;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static linc.com.library.Constant.AUDIO_TOOL_TMP;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

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
    public AudioTool withAudio(String sourceAudio) {
        withAudio(new File(sourceAudio));
        return this;
    }

    public AudioTool withAudio(File sourceAudio) {
        try {
            if(!sourceAudio.exists()) throw new FileNotFoundException();
            this.audio = new File(
                    audioDirectory
                            + File.separator
                            + AUDIO_TOOL_TMP
                            + System.currentTimeMillis()
                            + FileManager.getFileExtension(sourceAudio)
            );
            FileManager.copyFile(sourceAudio, audio);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return this;
    }

    /**
     * Save output file
     * @param fullPath path to the output directory with file name
     */
    public AudioTool saveCurrentTo(String fullPath) {
        try {
            FileManager.copyFile(audio, new File(fullPath));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
    public AudioTool cutAudio(int start, int end, @Nullable OnFileComplete onCompleteCallback) throws Exception {
//        FileManager.copyFile(audio, audio);
        File outputBuffer = FileManager.createBuffer(audio);
        String command = String.format(Locale.getDefault(),
                "-y -ss %d -i %s -t %d %s",
                start, audio.getPath(), end, outputBuffer.getPath()
        );
        long rc = FFmpeg.execute(command);
        audio = FileManager.overwriteFromBuffer(audio, outputBuffer);
        /*long rc = FFmpeg.execute(formatCommand + audio.getPath(), (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Async command execution completed successfully.");
            } else if (returnCode == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "Async command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("Async command execution failed with rc=%d.", returnCode));
            }
        });*/

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
            Config.printLastCommandOutput(Log.INFO);
        }
//        FFmpeg.execute("-y -i" + audio.getPath() + "-ss" + start + "-to" + end + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    /**
     * @param start format -> "00:00:00"
     * @param end   format -> "00:20:30"
     */
    public AudioTool cutAudio(String start, String end, @Nullable OnFileComplete onCompleteCallback) {
        FFmpeg.execute("-y -ss" + start + "-i" + audio.getPath() + "-to" + end + audio.getPath());
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
                                      OnFileComplete onCompleteCallback
    ) {
        // ffmpeg -i input -filter_complex "aformat=channel_layouts=mono,showwavespic=colors=#ad9557" -frames:v 1 output.png
        File output = new File(outputPath);
        if(outputPath.isEmpty() || !output.exists())
            outputPath = imageDirectory + File.separator + AUDIO_TOOL_TMP + System.currentTimeMillis();
        else if(output.isDirectory())
            outputPath = FileManager.addFileTitle(output, AUDIO_TOOL_TMP + System.currentTimeMillis());
        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex \"aformat=channel_layouts=mono,showwavespic=s=" + width + "x" + height + ":colors=" + color + "\"" + outputPath);
        onCompleteCallback.onComplete(new File(outputPath));
        return this;
    }

    /**
     * @param volume     volume percent -> 0.75 as 75% of volume
     * @param onCompleteCallback lambda with result audio
     */
    public AudioTool changeAudioVolume(float volume, int start, int end, @Nullable OnFileComplete onCompleteCallback) {
        volume = Limiter.limit(0f, 12000f, volume);
        start = Limiter.limit(0, ((int) getDurationMillis() / 1000), start);
        end = Limiter.limit(0, ((int) getDurationMillis() / 1000), end);

        FFmpeg.execute("-y -i" + audio.getPath() + "-af volume=enable='between(t," + start + "," + end + ")':volume=" + volume + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioVolume(float volume, @Nullable OnFileComplete onCompleteCallback) {
        volume = Limiter.limit(0f, 12000f, volume);

        FFmpeg.execute("-y -i" + audio.getPath() + "-filter:a \"volume=" + volume + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool normalizeAudioVolume(@Nullable OnFileComplete onCompleteCallback) {
        FFmpeg.execute("-y -i" + audio.getPath() + " -filter:a loudnorm" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioSpeed(float xSpeed, @Nullable OnFileComplete onCompleteCallback) {
        xSpeed = Limiter.limit(0.5f, 2, xSpeed);
        //
//        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=" + xSpeed + " " + audio.getPath());
//        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=" + xSpeed + " " + audio.getPath());
        FFmpeg.execute("-y -i " + audio.getPath() + " -af atempo=2 " + audio.getPath());// + " -af atempo=" + xSpeed + " " + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioPitch(int sampleRate, float pitch, float tempo, @Nullable OnFileComplete onCompleteCallback) {
//        ffmpeg -y -i kygo.mp3 -filter_complex "asetrate=48000*2^(-10/12),atempo=1/2^(-10/12)" p_10_ky.mp3
        tempo = Limiter.limit(0.5f, 2, tempo);

        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex asetrate=" + sampleRate + "*" + pitch + "^(-10/12),atempo=" + tempo + "^(-10/12)" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool changeAudioBass(float bass, float width, int frequency, @Nullable OnFileComplete onCompleteCallback) {
        // ffmpeg -y -i kygo.mp3 -af bass=g=10:w=0.5:f=150 bass.mp3
        bass = Limiter.limit(-20f, 20f, bass);
        width = Limiter.limit(0f, 1f, width);
        frequency = Limiter.limit(0, 999999, frequency);

        FFmpeg.execute("-y -i" + audio.getPath() + "-af" + "bass=g=" + bass + ":w=" + width + ":f=" + frequency + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool removeAudioNoise(@Nullable OnFileComplete onCompleteCallback) {
        filterAudio(400, 4000, onCompleteCallback);
        return this;
    }

    public AudioTool removeVocal(@Nullable OnFileComplete onCompleteCallback) {
//        ffmpeg -i song.mp3 -af pan="stereo|c0=c0|c1=-1*c1" -ac 1 karaoke.mp3
        FFmpeg.execute("-y -i" + audio.getPath() + "-af pan=\"stereo|c0=c0|c1=-1*c1\" -ac 1" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool filterAudio(int highpass, int lowpass, @Nullable OnFileComplete onCompleteCallback) {
        highpass = Limiter.limit(0, 999999, highpass);
        lowpass = Limiter.limit(0, 999999, lowpass);

        FFmpeg.execute("-y -i" + audio.getPath() + "-af \"highpass=f=" + highpass + ", lowpass=f=" + lowpass + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool reverseAudio(@Nullable OnFileComplete onCompleteCallback) {
        FFmpeg.execute("-y -i" + audio.getPath() + "-map 0 -c:v copy -af \"areverse\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyEchoEffect(Echo echo, @Nullable OnFileComplete onCompleteCallback) {
        String filter;
        switch (echo) {
            case ECHO_TWICE_INSTRUMENTS: filter = "\"aecho=0.8:0.88:60:0.4\"";break;
            case ECHO_METALLIC: filter = "\"aecho=0.8:0.88:6:0.4\"";break;
            case ECHO_OPEN_AIR: filter = "\"aecho=0.8:0.9:1000:0.3\"";break;
            default: filter = "\"aecho=0.8:0.9:1000|1800:0.3|0.25\"";
        }
        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex" + filter  + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyVibratoEffect(float frequency, float depth, @Nullable OnFileComplete onCompleteCallback) {
        frequency = Limiter.limit(0.1f, 20000f, frequency);
        depth = Limiter.limit(0f, 1f, depth);

        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex vibrato=f=" + frequency + ":d=" + depth + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyReverbEffect(float audioDepth, float reverbDepth, @Nullable OnFileComplete onCompleteCallback) {
//        ffmpeg -y -i kygo.mp3 -i lev_cut.mp3 -filter_complex '[0] [1] afir=dry=0.1:wet=0.1' reverb.mp3
        String effectPath = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + File.pathSeparator
                + File.separator
                + context.getPackageName()
                + "/raw/"
                + "audiotool_ir_reverb.mp3"
        ).getPath();
        audioDepth = Limiter.limit(0f, 1f, audioDepth);
        reverbDepth = Limiter.limit(0f, 1f, reverbDepth);

        FFmpeg.execute("-y -i" + audio.getPath() + "-i" + effectPath + "-filter_complex '[0] [1] afir=dry=" + audioDepth + ":wet=" + reverbDepth + "'" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool applyShifterEffect(int transitionTime, float width, @Nullable OnFileComplete onCompleteCallback) {
        // ffmpeg -y -i kygo.mp3 -filter_complex "apulsator=mode=sine:hz=0.125:width=0" shifter.mp3
        float hz =  1/ (float) transitionTime;
        hz = Limiter.limit(0.01f, 100f, hz);
        width = Limiter.limit(0.1f, 2f, width);

        FFmpeg.execute("-y -i" + audio.getPath() + "-filter_complex \"apulsator=mode=sine:hz=" + hz + ":width=" + width + "\"" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }

    public AudioTool convertVideoToAudio(@Nullable OnFileComplete onCompleteCallback) {
        FFmpeg.execute("-y -i" + audio.getPath() + "-vn" + audio.getPath());
        onResultFile(onCompleteCallback);
        return this;
    }


    public void joinAudios(File[] audios, String outputPath, OnFileComplete onCompleteCallback) {
        joinAudios(FileManager.getPathFromFiles(audios), outputPath, onCompleteCallback);
    }

    public AudioTool joinAudios(String[] audios, String outputPath, OnFileComplete onCompleteCallback) {
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
        FFmpeg.execute("-f concat -safe 0 -i" + joinPath + "-c copy" + outputPath);
        onCompleteCallback.onComplete(new File(outputPath));
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

    interface OnCompleteCallback<T> {
        void onComplete(T output);
    }

    public interface OnFileComplete extends OnCompleteCallback<File> {
        /* ! Empty body !*/
    }

    public interface OnNumberComplete extends OnCompleteCallback<Long> {
        /* ! Empty body !*/
    }
}
