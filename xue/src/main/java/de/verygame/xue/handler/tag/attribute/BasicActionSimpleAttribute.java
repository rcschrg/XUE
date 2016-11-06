package de.verygame.xue.handler.tag.attribute;

import de.verygame.xue.mapping.tag.attribute.AbstractAttribute;
import de.verygame.xue.util.action.BasicAction;

/**
 * @author Rico Schrage
 */
public class BasicActionSimpleAttribute {

    public static class From extends AbstractAttribute<BasicAction, Float> {
        private static final String NAME = "from";
        private static final From instance = new From();
        public static From getInstance() { return instance; }

        private From() {}

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(BasicAction element, Float value) {
            element.setFrom(value);
        }
    }
    public static class To extends AbstractAttribute<BasicAction, Float> {
        private static final String NAME = "to";
        private static final To instance = new To();
        public static To getInstance() { return instance; }

        private To() {}

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(BasicAction element, Float value) {
            element.setTo(value);
        }
    }

    public static class StartTime extends AbstractAttribute<BasicAction, Float> {
        private static final String NAME = "startTime";
        private static final StartTime instance = new StartTime();
        public static StartTime getInstance() { return instance; }

        private StartTime() {}

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(BasicAction element, Float value) {
            element.setStartTime(value);
        }
    }

    public static class StopTime extends AbstractAttribute<BasicAction, Float> {
        private static final String NAME = "stopTime";
        private static final StopTime instance = new StopTime();
        public static StopTime getInstance() { return instance; }

        private StopTime() {}

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(BasicAction element, Float value) {
            element.setStopTime(value);
        }
    }
}
