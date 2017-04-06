package de.verygame.xue;

import de.verygame.xue.annotation.Bind;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.handler.ConstantTagGroupHandler;
import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.InjectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rico Schrage
 */
public class BasicXue<T> extends AbstractXue {
    private static final String LOAD_BEFORE_MESSAGE = "You have to load the xml-file first!";

    /** Contains all elements, which have the attribute <code>name</code> */
    protected Map<String, T> elementMap;
    /** Main tag group */
    protected ElementsTagGroupHandler<T> elementsTagGroupHandler;
    /** see {@link #bind(Object)} */
    protected Object bindTarget;

    public BasicXue() {
        this(new DummyGlobalMappings<T>());
    }

    public BasicXue(GlobalMappings<T> globalMappings) {
        super();

        this.elementsTagGroupHandler = new ElementsTagGroupHandler<>(globalMappings);

        this.core.addHandler(elementsTagGroupHandler);
        this.core.addHandler(new ConstantTagGroupHandler());
    }

    public void bind(Object bindTarget) {
        this.bindTarget = bindTarget;
    }

    public int getElementSize() {
        if (elementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }

        return elementMap.size();
    }

    public Map<String, T> getElementMap() {
        return elementMap;
    }

    public List<T> getElementsByName(String... names) {
        List<T> elementList = new ArrayList<>();
        for (String name : names) {
            elementList.add(elementMap.get(name));
        }
        return elementList;
    }

    public List<T> getElementsByTagName(String name) {
        List<T> elementList = new ArrayList<>();
        Class<?> target = null;
        for (TagMapping<? extends T> tagMapping : elementsTagGroupHandler.getBuilderMappings()) {
            XueTag<? extends T> tag = tagMapping.createTag(name);
            if (tag != null) {
                target = tag.getElement().getClass();
            }
        }
        if (target == null) {
            throw new ElementTagUnknownException("There is no tag with the name: " + name);
        }
        for (Map.Entry<String, T> entry : elementMap.entrySet()) {
            if (target.isInstance(entry.getValue())) {
                elementList.add(entry.getValue());
            }
        }
        return elementList;
    }

    public T getElementByName(final String name) {
        if (elementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }

        return elementMap.get(name);
    }

    @Override
    protected void postLoad() {
        //noinspection unchecked
        this.elementMap = (Map<String, T>) core.getResultUnsafe(elementsTagGroupHandler.getClass(), Object.class);

        Set<Map.Entry<String, T>> entries = elementMap.entrySet();
        if (bindTarget == null) {
            return;
        }
        for (final Map.Entry<String, T> entry : entries) {
            InjectionUtils.injectByKey(Bind.class, bindTarget, entry.getValue(), entry.getKey());
        }
    }
}
