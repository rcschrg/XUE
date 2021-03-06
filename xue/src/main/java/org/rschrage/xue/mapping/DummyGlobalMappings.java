package org.rschrage.xue.mapping;

import org.rschrage.util.math.CoordinateType;

/**
 * @author Rico Schrage
 */
public class DummyGlobalMappings<T> implements GlobalMappings<T> {

    @Override
    public float calcFromRelativeValue(T target, float value, CoordinateType coordinateType) {
        return value;
    }

    @Override
    public String getString(String key) {
        return key;
    }

    @Override
    public float getDensity() {
        return 1;
    }
}
