package treehou.se.habit.util.logging;


public interface Logger {
    void d(String tag, String message);
    void i(String tag, String message);
    void w(String tag, String message);
    void w(String tag, String message, Throwable error);
    void e(String tag, String message);
    void e(String tag, String message, Throwable error);
}
