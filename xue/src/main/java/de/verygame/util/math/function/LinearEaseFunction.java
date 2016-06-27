package de.verygame.util.math.function;

/**
 * Linear easing function (<code>currentDuration/duration</code>)
 *
 * @author Rico Schrage
 */
public class LinearEaseFunction implements EaseFunction {

    private static LinearEaseFunction instance = new LinearEaseFunction();

    private LinearEaseFunction() {
        // nothing to do
    }

    /**
     * @return the static instance of the function
     */
    public static LinearEaseFunction getInstance() {
        return instance;
    }

    @Override
    public float getPercentage(float currentDuration, float duration) {
        return currentDuration / duration;
    }

}