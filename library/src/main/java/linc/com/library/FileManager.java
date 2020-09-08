package linc.com.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

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

    static void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel destChannel = new FileOutputStream(destFile).getChannel();
        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        sourceChannel.close();
        destChannel.close();
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

}
