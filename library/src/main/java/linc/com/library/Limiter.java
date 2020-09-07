package linc.com.library;

class Limiter {

    static float limit(float min, float max, float value) {
        if(value < min) {
            value = min;
        } else if(value > max) {
            value = max;
        }
        return value;
    }

    static int limit(int min, int max, int value) {
        if(value < min) {
            value = min;
        } else if(value > max) {
            value = max;
        }
        return value;
    }

}
