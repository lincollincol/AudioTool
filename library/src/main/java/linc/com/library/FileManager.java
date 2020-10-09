package linc.com.library;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import static linc.com.library.Constant.AUDIO_TOOL_BUFFER;
import static linc.com.library.Constant.AUDIO_TOOL_TMP;
import static linc.com.library.Constant.DOT;
import static linc.com.library.Constant.EMPTY_STRING;

/**
 * Package private lib class
 * File manipulator
 */
class FileManager {

    static void validateInputFile(File inputFile) throws IOException {
        if(!inputFile.exists() || inputFile.isDirectory())
            throw new FileNotFoundException();
    }

    static void validateInputFile(String path) throws IOException {
        validateInputFile(new File(path));
    }

    static void validateOutputFile(File inputFile) throws IOException {
        if(!inputFile.getParentFile().exists() || inputFile.isDirectory())
            throw new FileNotFoundException("Invalid output path or you use directory path instead of file path!");
    }

    static void validateOutputFile(String path) throws IOException {
        validateOutputFile(new File(path));
    }

    static void writeFile(String outputPath, String data) {
        try {
            FileWriter myWriter = new FileWriter(outputPath);
            myWriter.write(data);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    static File copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel destChannel = new FileOutputStream(destFile).getChannel();
        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        sourceChannel.close();
        destChannel.close();
        return destFile;
    }

    static File copyFile(File sourceFile, String destFile) throws IOException {
        return copyFile(sourceFile, new File(destFile));
    }

    static File createBuffer(File sourceFile) throws IOException {
        return copyFile(sourceFile, addFileTitle(
                sourceFile.getParent(),
                AUDIO_TOOL_TMP + AUDIO_TOOL_BUFFER + getFileExtension(sourceFile))
        );
    }

    static void overwriteFromBuffer(File sourceFile, File bufferFile) throws IOException {
        copyFile(bufferFile, sourceFile);
        bufferFile.delete();
    }

    static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(DOT);
        if (lastIndexOf == -1) {
            return EMPTY_STRING; // Empty extension
        }
        return name.substring(lastIndexOf);
    }

    static String[] getPathFromFiles(File[] files) {
        String[] pathArray = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            pathArray[i] = files[i].getPath();
        }
        return pathArray;
    }

    static String addFileTitle(File file, String title) {
        if(file.getPath().charAt(file.getPath().length() - 1) == File.pathSeparatorChar) {
            return file.getPath() + title;
        }
        return file.getPath() + File.separator + title;
    }

    static String addFileTitle(String path, String title) {
        return addFileTitle(new File(path), title);
    }

    static File bufferAssetAudio(Context context, String audioDirectory, String assetAudio) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(assetAudio);
            File outFile = new File(audioDirectory, AUDIO_TOOL_TMP + assetAudio);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return outFile;
        } catch(IOException e) {
            return null;
        }
    }

}
