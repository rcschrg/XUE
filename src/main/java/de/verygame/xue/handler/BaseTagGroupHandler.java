package de.verygame.xue.handler;

import de.verygame.xue.exception.XueException;
import de.verygame.xue.mapping.BuilderMapping;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rico Schrage
 *
 * Convinient abstract implementation of the {@link TagGroupHandler} interface, which provides some default implementations
 * of the trivial methods.
 */
public abstract class BaseTagGroupHandler<T, D> implements TagGroupHandler<T, D> {

    private boolean active = false;
    private String name;

    protected List<D> domList;

    protected List<BuilderMapping<T>> mapping;

    public BaseTagGroupHandler(String name) {
        this.name = name;
        this.domList = new ArrayList<>();
        this.mapping = new ArrayList<>();
    }

    @Override
    public void addBuilderMapping(BuilderMapping<T> mapping) {
        this.mapping.add(mapping);
    }

    @Override
    public void startHandle(XmlPullParser xpp) throws XueException {
        //default
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws XueException {
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
    public List<D> getDom() {
        return domList;
    }

}
