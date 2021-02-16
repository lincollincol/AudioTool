package linc.com.library;

class FFmpegNotFoundException extends Exception {
    FFmpegNotFoundException() {
        super("FFMPEG library not found! Please add implementation 'com.arthenica:mobile-ffmpeg-full:4.3.1.LTS' to your gradle file!");
    }
}
