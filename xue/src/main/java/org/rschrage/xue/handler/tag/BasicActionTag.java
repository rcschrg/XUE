package org.rschrage.xue.handler.tag;

import org.rschrage.xue.handler.tag.attribute.BasicActionAttributeTarget;
import org.rschrage.xue.handler.tag.attribute.BasicActionInterpolation;
import org.rschrage.xue.handler.tag.attribute.BasicActionSimpleAttribute;
import org.rschrage.xue.mapping.tag.XueAbstractElementTag;
import org.rschrage.xue.mapping.tag.attribute.Attribute;
import org.rschrage.xue.mapping.tag.attribute.AttributeGroup;
import org.rschrage.xue.util.action.BasicAction;

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
        return buildAttributeList(BasicActionInterpolation.getInstance(), BasicActionSimpleAttribute.From.getInstance(), BasicActionSimpleAttribute.To.getInstance(),
            BasicActionSimpleAttribute.StartTime.getInstance(), BasicActionSimpleAttribute.StopTime.getInstance());
    }

    @Override
    protected List<AttributeGroup<? super BasicAction>> defineAttributeGroups() {
        return buildAttributeGroupList(new BasicActionAttributeTarget());
    }
}
