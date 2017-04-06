package de.verygame.xue.handler;

import de.verygame.xue.constants.Constant;
import de.verygame.xue.dom.DomRepresentation;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.mapping.TagMapping;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 *
 * Convinient abstract implementation of the {@link TagGroupHandler} interface, which provides some default implementations
 * of the trivial methods.
 */
public abstract class BaseTagGroupHandler<T, D extends DomRepresentation<?>> implements TagGroupHandler<T, D> {
    private static final String DEFAULT_DOMAIN = "default";

    private boolean active = false;
    private String currentDomain = DEFAULT_DOMAIN;
    private Constant nameConstant;

    protected Map<Constant, String> constantMap;
    protected List<D> domList;
    protected List<TagMapping<? extends T>> mapping;

    public BaseTagGroupHandler(Constant nameConstant) {
        this(Constant.obtainDefaultMap(), nameConstant);
    }

    public BaseTagGroupHandler(Map<Constant, String> constantMap, Constant nameConstant) {
        this.nameConstant = nameConstant;
        this.constantMap = constantMap;
        this.domList = new ArrayList<>();
        this.mapping = new ArrayList<>();
    }

    @Override
    public void setDomain(String newDomain) {
        this.currentDomain = newDomain;
    }

    @Override
    public void addToDom(D domElement) {
        domElement.setDomain(currentDomain);
        domList.add(domElement);
    }

    public void setConstantMap(Map<Constant, String> constantMap) {
        this.constantMap = constantMap;
    }

    @Override
    public void addBuilderMapping(TagMapping<? extends T> mapping) {
        this.mapping.add(mapping);
    }

    public List<TagMapping<? extends T>> getBuilderMappings() { return this.mapping; }

    @Override
    public void startHandle(XmlPullParser xpp) throws XueException, XmlPullParserException {
        //default
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws XueException, XmlPullParserException {
        //default
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getName() {
        return constantMap.get(nameConstant);
    }

    @Override
    public List<D> getDom() {
        return domList;
    }

}
