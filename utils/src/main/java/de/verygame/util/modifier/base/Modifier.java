package de.verygame.util.modifier.base;


/**
 * An Modifier can be created to successively update a callback.
 *
 * @author Rico Schrage
 */
public interface Modifier {

    /**
     * Describes what happens when an update occurs
     */
    void tick();

    /**
     * Returns whether the modifier has finished
     */
    boolean hasFinished();

    /**
     * Forces the modifier to stop
     */
    void kill();

    /**
     * Updates the modifier
     */
    void update(float delta);

    /**
     * Describes what happens on finish (last tick)
     */
    void onFinish();

    /**
     * You can add a {@link FinishListener} which will be called in
     * {@link Modifier#onFinish()}.
     *
     * @param finishListener {@link FinishListener}
     */
    void addFinishListener(final FinishListener finishListener);

    /**
     * Describes what happens on initialization (before first tick).
     */
    void onInit();

    /**
     * Resets the modifier
     */
    void reset();
}