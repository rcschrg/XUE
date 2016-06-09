package de.verygame.xue.handler.action;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.mapping.builder.AbstractElementBuilder;
import de.verygame.xue.mapping.builder.GLMenuBuilder;
import de.verygame.xue.util.math.function.LinearEaseFunction;
import de.verygame.xue.util.modifier.base.SimpleModifierCallback;

/**
 * @author Rico Schrage
 */
public class BasicActionBuilder extends AbstractElementBuilder<Action> {

    private GLMenuBuilder<?> builder;

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
            builder = (GLMenuBuilder<?>) value;
        }
    }

    @Override
    public void postBuild() {
        ((BasicAction)element).createModifier();
    }
}
