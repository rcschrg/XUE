package de.verygame.util.math.function;

/**
 * Description of a mathematical easing function
 *
 * @author Rico Schrage
 */
public interface EaseFunction {

    /**
     * Returns the currently reached percentage of <code>duration</code>.
     * <p>
     * The value is based on a mathematical function.
     *
     * @param currentDuration current duration
     * @param duration        estimated duration
     * @return percentage [0,1]
     */
    float getPercentage(final float currentDuration, final float duration);

}
