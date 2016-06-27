package de.verygame.xue;

import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Rico Schrage
 */
public class XueTest {

    private Xue<Object> xue;

    @Before
    public void setUp() throws Exception {
        xue = new Xue<>(new ByteArrayInputStream("".getBytes()));
    }

    @Test
    public void inputHandler() throws Exception {
        XueInputHandler inputHandler = mock(XueInputHandler.class);
        xue.addInputHandler(inputHandler);
        xue.onInputEvent(XueInputEvent.ACTIVATE);

        verify(inputHandler).onInputEvent(XueInputEvent.ACTIVATE);
    }

    @Test
    public void updateHandler() throws Exception {
        XueUpdateHandler updateHandler = mock(XueUpdateHandler.class);
        xue.addUpdateHandler(updateHandler);
        xue.onUpdate(1f);

        verify(updateHandler).onUpdate(anyFloat());
    }

    @Test(expected = IllegalStateException.class)
    public void getElementSize() throws Exception {
        xue.getElementSize();
    }

    @Test(expected = IllegalStateException.class)
    public void getElementByName() throws Exception {
        xue.getElementByName("");
    }

    @Test(expected = IllegalStateException.class)
    public void getConstByName() throws Exception {
        xue.getConstByName("");
    }

    @Test
    public void load() throws Exception {
        xue.load();

        assertTrue(xue.getElementSize() == 0);
    }

}