package de.verygame.xue.handler.tag;

import de.verygame.xue.handler.tag.attribute.BasicActionAttributeTarget;
import de.verygame.xue.handler.tag.attribute.BasicActionInterpolation;
import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.util.action.BasicAction;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class BasicActionTag extends XueAbstractElementTag<BasicAction> {

    public BasicActionTag() {
        super(new BasicAction(0f));
    }

    @Override
    protected List<Attribute<? super BasicAction, ?>> defineAttributes() {
        return buildAttributeList(BasicActionInterpolation.getInstance());
    }

    @Override
    protected List<AttributeGroup<? super BasicAction>> defineAttributeGroups() {
        return buildAttributeGroupList(BasicActionAttributeTarget.getInstance());
    }
}
