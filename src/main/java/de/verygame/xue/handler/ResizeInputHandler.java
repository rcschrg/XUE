package de.verygame.xue.handler;

import de.verygame.xue.annotation.DependencyHandler;
import de.verygame.xue.handler.dom.DomElement;
import de.verygame.xue.input.XueInputEvent;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class ResizeInputHandler implements XueInputHandler {

    @DependencyHandler
    protected ElementsTagGroupHandler<?> elementsTagGroupHandler;

    @Override
    public void onInputEvent(XueInputEvent event) {
        if (event == XueInputEvent.RESIZE) {
            List<? extends DomElement<?>> domElements = elementsTagGroupHandler.getDom();
            for (int i = 0; i < domElements.size(); ++i) {
                DomElement<?> domElement = domElements.get(i);
                domElement.resize();
            }
        }
    }
}
