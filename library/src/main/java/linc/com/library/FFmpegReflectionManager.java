package linc.com.library;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

class FFmpegReflectionManager {

    static Class<?> ffmpeg;
    static Class<?> ffprobe;
    static Class<?> config;
    private static Class<?> level;

    static int executeFFmpeg(final String command) {
        return (Integer) runMethod(
                ffmpeg,
                "execute",
                new Class[] { String.class },
                new String[] { command }
        );
    }

    static int executeFFprobe(final String command) {
        return (Integer) runMethod(
                ffprobe,
                "execute",
                new Class[] { String.class },
                new String[] { command }
        );
    }

     static Class<?> provideClassByName(final String clazz) throws FFmpegNotFoundException {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new FFmpegNotFoundException();
        }
    }

    static void initLogLevel() throws FFmpegNotFoundException {
        int lastPackageDot = config.getName().lastIndexOf('.');
        level = provideClassByName(String.format(
                "%s.Level",
                config.getName().substring(0, lastPackageDot)
        ));
    }

    private synchronized static Object runMethod(
            Class clazz,
            String method,
            @Nullable Class<?>[] types,
            @Nullable Object[] args
    ) {
        try {
            Method reflectMethod;
            if(types != null) {
                reflectMethod = clazz.getMethod(method, types);
                return reflectMethod.invoke(null, args);
            } else {
                reflectMethod = clazz.getMethod(method);
                return reflectMethod.invoke(null);
            }
        } catch (SecurityException |
                NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException e
        ) {
            e.printStackTrace();
        }
        return null;
    }

    static class Config {

        static void enableRedirection() {
            runMethod(
                    config,
                    "enableRedirection",
                    null,
                    null
            );
        }

        static void setLogLevel(int code) {
            runMethod(
                    config,
                    "setLogLevel",
                    new Class[] { level },
                    new Object[] { provideLevelFrom(code) }
            );
        }

        static String getLastCommandOutput() {
            return (String) runMethod(
                    config,
                    "getLastCommandOutput",
                    null,
                    null
            );
        }

        static Object provideLevelFrom(int code) {
            return (Object) runMethod(
                    level,
                    "from",
                    new Class[] { int.class },
                    new Integer[] { code }
            );
        }

    }


}
