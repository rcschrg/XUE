package de.verygame.xue.tag.attribute;

import de.verygame.util.math.function.LinearEaseFunction;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.BasicAction;
import de.verygame.xue.mapping.tag.attribute.AbstractAttribute;

/**
 * @author Rico Schrage
 */
public class BasicActionInterpolation extends AbstractAttribute<Action, String> {
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
    public void apply(Action element, String value) {
        switch (value) {
            case "linear":
            case "none":
            case "square":
            case "cubic":
                ((BasicAction)element).setEaseFunction(LinearEaseFunction.getInstance());
                break;
            default:
                break;
        }
    }

}
