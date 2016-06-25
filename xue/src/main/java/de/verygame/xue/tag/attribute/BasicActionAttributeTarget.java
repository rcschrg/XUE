package de.verygame.xue.tag.attribute;

import de.verygame.util.modifier.base.SimpleModifierCallback;
import de.verygame.xue.annotation.AttributeHandler;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.BasicAction;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.mapping.tag.attribute.AbstractAttributeGroup;
import de.verygame.xue.mapping.tag.attribute.AttributeGroupElementMeta;
import de.verygame.xue.util.GroupMetaUtils;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class BasicActionAttributeTarget extends AbstractAttributeGroup<Action> {
    private static final List<AttributeGroupElementMeta> GROUP_META = GroupMetaUtils.buildMetaList(new String[]{ "target", "attribute" },
            new Class<?>[]{ Object.class, String.class });
    private static final BasicActionAttributeTarget instance = new BasicActionAttributeTarget();

    private XueTag<?> builder;

    public static BasicActionAttributeTarget getInstance() {
        return instance;
    }

    @Override
    public List<AttributeGroupElementMeta> getGroupMeta() {
        return GROUP_META;
    }

    @AttributeHandler
    public void applyTarget(Action element, Object value) {
        builder = (XueTag<?>) value;
    }

    @AttributeHandler
    public void applyAttribute(Action element, final String value) {
        ((BasicAction)element).setActionCallback(new SimpleModifierCallback() {
            @Override
            protected void action(float v) {
                builder.apply(value, v);
            }
        });
    }

    @Override
    public void end(Action element) {
        ((BasicAction)element).createModifier();
    }
}
