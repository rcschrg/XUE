package de.verygame.xue.handler;

import de.verygame.xue.constants.Constant;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.mapping.BuilderMapping;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 *
 * Convinient abstract implementation of the {@link TagGroupHandler} interface, which provides some default implementations
 * of the trivial methods.
 */
public abstract class BaseTagGroupHandler<T, D> implements TagGroupHandler<T, D> {

    private boolean active = false;
    private Constant nameConstant;

    protected Map<Constant, String> constantMap;
    protected List<D> domList;
    protected List<BuilderMapping<? extends T>> mapping;

    public BaseTagGroupHandler(Map<Constant, String> constantMap, Constant constant) {
        this.nameConstant = constant;
        this.constantMap = constantMap;
        this.domList = new ArrayList<>();
        this.mapping = new ArrayList<>();
    }

    @Override
    public void addBuilderMapping(BuilderMapping<? extends T> mapping) {
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
        return constantMap.get(nameConstant);
    }

    @Override
    public List<D> getDom() {
        return domList;
    }

}
