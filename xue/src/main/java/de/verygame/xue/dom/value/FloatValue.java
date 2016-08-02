package de.verygame.xue.dom.value;

import de.verygame.util.math.CoordinateType;

/**
 * @author Rico Schrage
 */
public abstract class FloatValue<A> extends AbstractValue<Float, A> {

    public FloatValue(Float value) {
        super(value);
    }

    public FloatValue(Float value, A addition) {
        super(value, addition);
    }

    public static class Relative extends FloatValue<CoordinateType> {

        public Relative(Float value) {
            super(value);
        }

        public Relative(Float value, CoordinateType type) {
            super(value, type);
        }
    }

    public static class Density extends FloatValue<Object> {

        public Density(Float value) {
            super(value);
        }

    }

    public static class Normal extends FloatValue<Object> {

        public Normal(Float value) {
            super(value);
        }

    }

}
