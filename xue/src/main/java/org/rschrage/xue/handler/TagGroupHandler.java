package org.rschrage.xue.handler;

import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.dom.DomContainer;
import org.rschrage.xue.dom.DomRepresentation;
import org.rschrage.xue.exception.XueException;
import org.rschrage.xue.mapping.TagMapping;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public interface TagGroupHandler<T, D extends DomRepresentation<T>> extends DomContainer<D> {

    /**
     * Starts the handling of the group, means the start tag was reached.
     *
     * @param xpp parser
     */
    void startHandle(XmlPullParser xpp) throws XueException, XmlPullParserException;

    /**
     * Stops the handling of the group, means the end tag was reached.
     *
     * @param xpp parser
     */
    void stopHandle(XmlPullParser xpp) throws XueException, XmlPullParserException;

    /**
     * Called when a tag inside the specified ({@link #getName()}) tag has been reached.
     *
     * @param xpp parser of the xml
     *
     * @throws XueException
     * @throws XmlPullParserException
     */
    void handle(XmlPullParser xpp) throws XueException, XmlPullParserException;

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

    void setDomain(String newDomain);
    void addToDom(D e);
    void setConstantMap(Map<Constant, String> constantMap);

}
