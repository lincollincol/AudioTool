package linc.com.library.callback;

public interface OnFFmpegComplete {
    void onSuccess();
    void onFailure(String message);
}
