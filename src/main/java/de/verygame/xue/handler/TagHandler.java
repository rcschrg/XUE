package de.verygame.xue.handler;

import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.GLMenuException;
import de.verygame.xue.exception.GLMenuSyntaxException;
import de.verygame.xue.exception.TagUnknownException;

/**
 * @author Rico Schrage
 */
public interface TagHandler<T, D> {

    /**
     * Starts the handling of the group, means the start tag was reached.
     *
     * @param xpp parser
     */
    void startHandle(XmlPullParser xpp) throws GLMenuException;

    /**
     * Stops the handling of the group, means the end tag was reached.
     *
     * @param xpp parser
     */
    void stopHandle(XmlPullParser xpp) throws GLMenuException ;

    /**
     * Called when a tag inside the specified ({@link #getName()}) tag has been reached.
     *
     * @param xpp parser of the xml
     *
     * @throws GLMenuSyntaxException
     * @throws TagUnknownException
     * @throws AttributeUnknownException
     */
    void handle(XmlPullParser xpp) throws GLMenuException;

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

}
