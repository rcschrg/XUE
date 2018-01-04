package org.rschrage.xue.handler.tag.attribute;

import org.rschrage.xue.mapping.tag.XueTag;
import org.rschrage.xue.util.action.Action;
import org.rschrage.xue.util.action.BasicAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicActionAttributeTargetTest {

    BasicActionAttributeTarget basicActionAttributeTarget = new BasicActionAttributeTarget();
    BasicAction action = mock(BasicAction.class);

    @Mock
    XueTag<Action> xueTag;

    @Test
    public void applyTarget() throws Exception {
        basicActionAttributeTarget.applyTarget(action, mock(XueTag.class));
    }

    @Test
    public void applyAttribute() throws Exception {
        basicActionAttributeTarget.applyTarget(action, xueTag);
        basicActionAttributeTarget.applyAttribute(action, "asd");
    }

    @Test
    public void end() throws Exception {

    }

}