package de.verygame.xue.handler;

import de.verygame.xue.exception.XueException;
import de.verygame.xue.input.XueInputEvent;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
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
    private String name;

    protected Map<String, T> resultMap;
    protected Map<String, D> domMap;

    protected List<BuilderMapping<T>> mapping;

    public BaseTagGroupHandler(String name) {
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
    public Map<String, T> getResultMap() {
        return resultMap;
    }

    @Override
    public Map<String, D> getDomObjectMap() {
        return domMap;
    }

    @Override
    public void onInputEvent(XueInputEvent event) {
        //default: do nothing
        //most TagHandlers won't need to handle input events,
    }

    @Override
    public void update(float delta) {
        //default: do nothing
        //most TagHandlers won't need to handle update ticks.
    }
}
