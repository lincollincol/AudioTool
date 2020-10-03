package linc.com.library;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.io.IOException;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

/**
 * Package private lib class
 * FFMPEG library wrapper
 */
class FFmpegExecutor {

    static void executeCommandWithBuffer(String command, File input) throws IOException{
        File outputBuffer = FileManager.createBuffer(input);
        long rc = FFmpeg.execute(String.format("%s %s", command, outputBuffer.getPath()));

        if (rc == RETURN_CODE_SUCCESS) {
            FileManager.overwriteFromBuffer(input, outputBuffer);
        } else {
            throw new IOException(Config.getLastCommandOutput());
        }
    }

    static void executeCommand(String command) throws IOException{
        long rc = FFmpeg.execute(command);
        if (rc != RETURN_CODE_SUCCESS) {
            throw new IOException(Config.getLastCommandOutput());
        }
    }

}
