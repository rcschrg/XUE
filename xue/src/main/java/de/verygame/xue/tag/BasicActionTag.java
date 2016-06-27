package de.verygame.xue.tag;

import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.tag.attribute.BasicActionAttributeTarget;
import de.verygame.xue.tag.attribute.BasicActionInterpolation;
import de.verygame.xue.util.action.BasicAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class BasicActionTag extends XueAbstractElementTag<BasicAction> {

    public BasicActionTag() {
        super(new BasicAction(0f));
    }

    @Override
    protected List<Attribute<BasicAction, ?>> defineAttributes() {
        List<Attribute<BasicAction, ?>> attributes = new ArrayList<>();
        attributes.add(BasicActionInterpolation.getInstance());
        return attributes;
    }

    @Override
    protected List<AttributeGroup<BasicAction>> defineAttributeGroups() {
        List<AttributeGroup<BasicAction>> attributes = new ArrayList<>();
        attributes.add(BasicActionAttributeTarget.getInstance());
        return attributes;
    }
}
