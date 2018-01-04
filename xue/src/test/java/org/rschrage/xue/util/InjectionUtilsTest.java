package org.rschrage.xue.util;

import org.rschrage.xue.annotation.Dependency;
import org.rschrage.xue.input.XueInputEvent;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Rico Schrage
 */
public class InjectionUtilsTest {

    InjectTarget injectTarget = new InjectTarget();

    private static class InjectTarget {
        @Dependency
        protected Object a = null;
        @Dependency
        protected Object xueInputEvent = null;
    }

    @Test
    public void injectByType() throws Exception {
        InjectionUtils.injectByType(Dependency.class, injectTarget, new Object());

        assertTrue(injectTarget.a != null);
        assertTrue(injectTarget.xueInputEvent != null);
    }

    @Test
    public void injectByName() throws Exception {
        InjectionUtils.injectByName(Dependency.class, injectTarget, XueInputEvent.ACTIVATE);

        assertTrue(injectTarget.a == null);
        assertTrue(injectTarget.xueInputEvent != null);
    }

}