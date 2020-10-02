package linc.com.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static linc.com.library.Constant.AUDIO_TOOL_BUFFER;
import static linc.com.library.Constant.AUDIO_TOOL_TMP;

class FileManager {

    static void writeFile(String outputPath, String data) {
        try {
            FileWriter myWriter = new FileWriter(outputPath);
            myWriter.write(data);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    static File overwriteFromBuffer(File sourceFile, File bufferFile) throws IOException {
//        String sourcePath = sourceFile.getPath();
//        sourceFile.delete();
        File updated = copyFile(bufferFile, sourceFile);
        bufferFile.delete();
        return updated;

    }

    static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
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
        if(file.getPath().charAt(file.getPath().length() - 1) == '/') {
            return file.getPath() + title;
        }
        return file.getPath() + File.separator + title;
    }

    static String addFileTitle(String path, String title) {
        return addFileTitle(new File(path), title);
    }

}
