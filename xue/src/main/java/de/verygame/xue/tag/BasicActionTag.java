package de.verygame.xue.tag;

import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.tag.attribute.BasicActionAttributeTarget;
import de.verygame.xue.tag.attribute.BasicActionInterpolation;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.BasicAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class BasicActionTag extends XueAbstractElementTag<Action> {

    public BasicActionTag() {
        super(new BasicAction(0f));
    }

    @Override
    protected List<Attribute<Action, ?>> defineAttributes() {
        List<Attribute<Action, ?>> attributes = new ArrayList<>();
        attributes.add(BasicActionInterpolation.getInstance());
        return attributes;
    }

    @Override
    protected List<AttributeGroup<Action>> defineAttributeGroups() {
        List<AttributeGroup<Action>> attributes = new ArrayList<>();
        attributes.add(BasicActionAttributeTarget.getInstance());
        return attributes;
    }
}
