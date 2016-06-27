package de.verygame.xue.tag.attribute;

import de.verygame.util.math.function.LinearEaseFunction;
import de.verygame.xue.mapping.tag.attribute.AbstractAttribute;
import de.verygame.xue.util.action.BasicAction;

/**
 * @author Rico Schrage
 */
public class BasicActionInterpolation extends AbstractAttribute<BasicAction, String> {
    private static final String ATTRIBUTE_NAME = "interpolation";
    private static final BasicActionInterpolation instance = new BasicActionInterpolation();

    public static BasicActionInterpolation getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public void apply(BasicAction element, String value) {
        switch (value) {
            case "linear":
            case "none":
            case "square":
            case "cubic":
                element.setEaseFunction(LinearEaseFunction.getInstance());
                break;
            default:
                break;
        }
    }

}
