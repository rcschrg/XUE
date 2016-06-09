package de.verygame.xue;

import de.verygame.xue.input.GLMenuInputEvent;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public interface GLMenu<T> {

    /**
     * Returns element by name.
     *
     * @param name of the element
     * @return Element
     */
    T getElementByName(final String name);

    /**
     * Returns constant by name.
     *
     * @param name of the constant
     * @return constant
     */
    Object getConstByName(final String name);

    /**
     * Loads specified menu.
     */
    void loadMenu();

    /**
     * Binds an object as target for binding elements to fields.
     * The field you want the element ot be bind must have the same name as the element.
     *
     * @param bindTarget object, which contains annotated ({@link Bind}} fields
     */
    void bind(Object bindTarget);

    /**
     * Will propagate the event to listening handlers.
     * @param inputEvent input event
     */
    void onInputEvent(GLMenuInputEvent inputEvent);
}
