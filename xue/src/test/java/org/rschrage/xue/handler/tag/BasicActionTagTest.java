package org.rschrage.xue.handler.tag;

import org.rschrage.xue.handler.tag.attribute.BasicActionInterpolation;
import org.rschrage.xue.mapping.tag.attribute.Attribute;
import org.rschrage.xue.util.action.BasicAction;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Rico Schrage
 */
public class BasicActionTagTest {

    private BasicActionTag basicActionTag = new BasicActionTag();

    @Test
    public void defineAttributes() throws Exception {
        List<Attribute<? super BasicAction, ?>> attributeList = basicActionTag.defineAttributes();

        assertTrue(attributeList.contains(BasicActionInterpolation.getInstance()));
    }

}