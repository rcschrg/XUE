package de.verygame.xue.handler;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.exception.XueSyntaxException;
import de.verygame.xue.input.XueInputEvent;
import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public interface TagGroupHandler<T, D> {

    /**
     * Starts the handling of the group, means the start tag was reached.
     *
     * @param xpp parser
     */
    void startHandle(XmlPullParser xpp) throws XueException;

    /**
     * Stops the handling of the group, means the end tag was reached.
     *
     * @param xpp parser
     */
    void stopHandle(XmlPullParser xpp) throws XueException;

    /**
     * Called when a tag inside the specified ({@link #getName()}) tag has been reached.
     *
     * @param xpp parser of the xml
     *
     * @throws XueSyntaxException
     * @throws TagUnknownException
     * @throws AttributeUnknownException
     */
    void handle(XmlPullParser xpp) throws XueException;

    /**
     * Set the handling active
     *
     * @param active true if handling is active
     */
    void setActive(boolean active);

    /**
     * @return true if the tag is currently handled
     */
    boolean isActive();

    /**
     * @return name of the tag
     */
    String getName();

    /**
     * Returns a map of the resultMap element mapped to their key string.
     *
     * @return map
     */
    Map<String, T> getResultMap();

    /**
     * Returns a map of the resultMap element mapped to their key string.
     *
     * @return map
     */
    Map<String, D> getDomObjectMap();

    /**
     * @param mapping builder mapping
     */
    void addBuilderMapping(BuilderMapping<T> mapping);

    /**
     * Called when an input event occurs. Input event have to thrown by the user.
     *
     * @param inputEvent {@link XueInputEvent}
     */
    void onInputEvent(XueInputEvent inputEvent);

    /**
     * Will be called when the {@link de.verygame.xue.Xue} gets updated. It might be necessary to call it
     * with very short intervals for features like the {@link ActionSequenceTagGroupHandler}.
     *
     * @param delta time passed
     */
    void update(float delta);

}
