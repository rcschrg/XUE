package org.rschrage.util.modifier;

import org.rschrage.util.math.function.EaseFunction;
import org.rschrage.util.math.function.LinearEaseFunction;
import org.rschrage.util.modifier.base.AbstractModifier;
import org.rschrage.util.modifier.base.ModifierCallback;

/**
 * Implements an Modifier.
 * <p>
 * It should be used when you want to modify exactly one value (stepwise).
 *
 * @author Rico Schrage
 */
public class SingleValueModifier extends AbstractModifier {

    /**
     * Starting value
     */
    protected final float fromValue;

    /**
     * Ending value which always will be the last value the modifier will set
     */
    protected final float toValue;

    /**
     * {@link EaseFunction}
     */
    protected final EaseFunction easeFunction;

    /**
     * Constructs SingleValueEntityModifier using {@link LinearEaseFunction}.
     *
     * @param target    target
     * @param duration  estimated duration
     * @param fromValue starting value
     * @param toValue   ending value
     */
    public SingleValueModifier(float duration, float fromValue, float toValue, ModifierCallback target) {
        this(duration, fromValue, toValue, LinearEaseFunction.getInstance(), target);
    }

    /**
     * Constructs SingleValueEntityModifier.
     *
     * @param target       target
     * @param duration     estimated duration
     * @param fromValue    starting value
     * @param toValue      ending value
     * @param easeFunction {@link EaseFunction}
     */
    public SingleValueModifier(float duration, float fromValue, float toValue, EaseFunction easeFunction, ModifierCallback target) {
        super(target, duration);

        this.fromValue = fromValue;
        this.toValue = toValue;

        this.easeFunction = easeFunction;
    }

    @Override
    public void tick() {

        if (fromValue >= toValue) {
            target.call(easeFunction.getPercentage(currentDuration, duration) * Math.abs(toValue - fromValue) + toValue);
        }
        else {
            target.call(easeFunction.getPercentage(duration - currentDuration, duration) * Math.abs(toValue - fromValue) + fromValue);
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();

        target.finish(toValue);
    }

    @Override
    public void onInit() {
        target.init(fromValue);
    }

}