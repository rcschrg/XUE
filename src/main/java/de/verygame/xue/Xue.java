package de.verygame.xue;

import de.verygame.xue.annotation.Bind;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.input.XueInputEvent;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public interface Xue<T> {

    /**
     * Returns element by name.
     *
     * @param name of the element
     * @return Element
     */
    T getElementByName(String name);

    /**
     * Returns constant by name.
     *
     * @param name of the constant
     * @return constant
     */
    Object getConstByName(String name);

    /**
     * Loads specified menu.
     */
    void load() throws XueException;

    /**
     * Binds an object as target for binding elements to fields.
     * The field you want the element to be bound to must have the same name as the element.
     *
     * @param bindTarget object, which contains annotated ({@link Bind}} fields
     */
    void bind(Object bindTarget);

    /**
     * Will propagate the event to listening handlers.
     *
     * @param inputEvent input event
     */
    void onInputEvent(XueInputEvent inputEvent);

    /**
     * Updates the TagGroupHandler.
     *
     * @param delta time passed since last update
     */
    void update(float delta);

}
