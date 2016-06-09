package de.verygame.xue.handler;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.verygame.xue.exception.GLMenuException;

/**
 * @author Rico Schrage
 */
public abstract class BaseTagHandler<T, D> implements TagHandler<T, D> {

    private boolean active = false;
    private String name;

    protected Map<String, T> resultMap;
    protected Map<String, D> domMap;

    protected List<BuilderMapping<T>> mapping;

    public BaseTagHandler(String name) {
        this.name = name;
        this.resultMap = new HashMap<>();
        this.domMap = new HashMap<>();
        this.mapping = new ArrayList<>();
    }

    @Override
    public void addBuilderMapping(BuilderMapping<T> mapping) {
        this.mapping.add(mapping);
    }

    @Override
    public void startHandle(XmlPullParser xpp) throws GLMenuException {
        //default
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws GLMenuException {
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
        return name;
    }

    @Override
    public Map<String, T> getResultMap() {
        return resultMap;
    }

    @Override
    public Map<String, D> getDomObjectMap() {
        return domMap;
    }
}
