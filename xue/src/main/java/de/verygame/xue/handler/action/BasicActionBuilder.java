package de.verygame.xue.handler.action;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.mapping.builder.XueAbstractElementTag;
import de.verygame.xue.mapping.builder.XueTag;
import de.verygame.util.math.function.LinearEaseFunction;
import de.verygame.util.modifier.base.SimpleModifierCallback;

/**
 * @author Rico Schrage
 */
public class BasicActionBuilder extends XueAbstractElementTag<Action> {

    private XueTag<?> builder;

    public BasicActionBuilder() {
        super(BasicAction.class);

        this.element = new BasicAction(0f);
    }

    @Override
    protected void applyStringSpecial(final String attribute, final String value) throws AttributeUnknownException {
        if ("attribute".equals(attribute)) {
            ((BasicAction)element).setActionCallback(new SimpleModifierCallback() {
                @Override
                protected void action(float v) {
                    try {
                        builder.applyFloat(value, v);
                    }
                    catch (AttributeUnknownException e) {
                        //TODO LOGGER
                    }
                }
            });
        }
        else if ("interpolation".equals(attribute)) {
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

    @Override
    protected void applyObjectSpecial(String attribute, Object value) throws AttributeUnknownException {
        if ("target".equals(attribute)) {
            builder = (XueTag<?>) value;
        }
    }

    @Override
    public void postBuild() {
        ((BasicAction)element).createModifier();
    }
}
