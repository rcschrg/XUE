package de.verygame.xue.handler;

import de.verygame.xue.constants.Constant;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.exception.XueSyntaxException;
import de.verygame.xue.dom.DomContainer;
import de.verygame.xue.mapping.TagMapping;
import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public interface TagGroupHandler<T, D> extends DomContainer<D> {

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
     * @param mapping tag mapping
     */
    void addBuilderMapping(TagMapping<? extends T> mapping);

    public void setConstantMap(Map<Constant, String> constantMap);

}
