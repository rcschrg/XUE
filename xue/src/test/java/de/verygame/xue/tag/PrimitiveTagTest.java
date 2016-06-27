package de.verygame.xue.tag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Rico Schrage
 */
public class PrimitiveTagTest {

    private PrimitiveTag primitiveTag = new PrimitiveTag();

    @Test
    public void testApply() throws Exception {
        primitiveTag.apply("value", 1f);

        assertEquals(primitiveTag.getElement(), 1f);
    }

    @Test
    public void checkForNoExceptions() throws Exception {
        primitiveTag.onInputEvent(null);
        primitiveTag.onUpdate(1);
        primitiveTag.preBuild();
        primitiveTag.postBuild();
    }

}