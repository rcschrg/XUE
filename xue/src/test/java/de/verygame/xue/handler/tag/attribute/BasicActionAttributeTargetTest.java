package de.verygame.xue.handler.tag.attribute;

import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.BasicAction;
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

    BasicActionAttributeTarget basicActionAttributeTarget = BasicActionAttributeTarget.getInstance();
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