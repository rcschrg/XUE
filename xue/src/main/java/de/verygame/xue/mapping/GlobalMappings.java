package de.verygame.xue.mapping;

/**
 * Created by Rico on 09.07.2015.
 *
 * @author Rico Schrage
 */
public interface GlobalMappings<T>  {

    /**
     * Coordinate type
     */
    enum CoordinateType {
        X, Y
    }

    /**
     * Calculates a positional value regarding the target and the coordinate type.
     *
     * @param target reference of the calculation
     * @param value relative value
     * @param coordinateType reference type
     * @return absolute value
     */
    float calcFromRelativeValue(final T target, final float value, final CoordinateType coordinateType);

    /**
     * Returns value mapped to the given key.
     *
     * @param key key of the string
     * @return string, or null if key does not exist
     */
    String getString(String key);

    /**
     * @return density of the screen
     */
    float getDensity();

}
