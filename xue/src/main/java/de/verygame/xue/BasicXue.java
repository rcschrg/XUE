package de.verygame.xue;

import de.verygame.util.Tuple;
import de.verygame.xue.annotation.Bind;
import de.verygame.xue.dom.DomObject;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.handler.ConstantTagGroupHandler;
import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.InjectionUtils;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @author Rico Schrage
 */
public class BasicXue<T> extends AbstractXue {
    private static final String LOAD_BEFORE_MESSAGE = "You have to load the xml-file first!";

    /** Contains all elements */
    protected Map<String, Map<String, T>> elements;
    /** Contains all elements, from the first file*/
    protected Map<String, T> defaultElementMap;
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

    @Deprecated
    public int getElementSize() {
        if (defaultElementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }

        return defaultElementMap.size();
    }

    public T getElementByName(final String name) {
        if (defaultElementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }
        return defaultElementMap.get(name);
    }

    public List<T> getElementsByName(String... names) {
        List<T> elementList = new ArrayList<>();
        for (String name : names) {
            elementList.add(defaultElementMap.get(name));
        }
        return elementList;
    }

    public T getElementInDomainByName(String name, String domain) {
        if (elements == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }
        return elements.get(domain).get(name);
    }

    public List<T> getElementsByTagName(String name) {
        return getElementsByTagNameInDomain(name, null);
    }

    public List<T> getElementsByTagNameInDomain(String tagName, String domainName) {
        List<T> elementList = new ArrayList<>();
        Class<?> target = null;
        for (TagMapping<? extends T> tagMapping : elementsTagGroupHandler.getBuilderMappings()) {
            XueTag<? extends T> tag = tagMapping.createTag(tagName);
            if (tag != null) {
                target = tag.getElement().getClass();
            }
        }
        if (target == null) {
            throw new ElementTagUnknownException("There is no tag with the name: " + tagName);
        }
        for (Map.Entry<String, Map<String, T>> entry : elements.entrySet()) {
            if (domainName != null && !entry.getKey().equals(domainName)) {
                continue;
            }
            for (final Map.Entry<String, T> sEntries : entry.getValue().entrySet()) {
                if (target.isInstance(sEntries.getValue())) {
                    elementList.add(sEntries.getValue());
                }
            }
        }
        return elementList;
    }

    @Override
    protected void postLoad() {
        if (!files.isEmpty()) {
            //noinspection unchecked
            this.defaultElementMap = (Map<String, T>) core.getResultUnsafe(elementsTagGroupHandler.getClass(), files.get(0).getSecond(), Object.class);
        }

        this.elements = new HashMap<>();
        for (File dir: dirs) {
            for (File file : dir.listFiles()) {
                //noinspection unchecked
                Map<String, T> map = (Map<String, T>) core.getResultUnsafe(elementsTagGroupHandler.getClass(), file.getName(), Object.class);
                this.elements.put(file.getName(), map);
            }
        }
        for (Tuple<InputStream, String> tuple : files) {
            //noinspection unchecked
            Map<String, T> map = (Map<String, T>) core.getResultUnsafe(elementsTagGroupHandler.getClass(), tuple.getSecond(), Object.class);
            this.elements.put(tuple.getSecond(), map);
        }

        if (bindTarget != null) {
            Set<Map.Entry<String, Map<String, T>>> e = this.elements.entrySet();
            for (final Map.Entry<String, Map<String, T>> entry : e) {
                Set<Map.Entry<String, T>> allEntries = entry.getValue().entrySet();
                for (final Map.Entry<String, T> elements : allEntries) {
                    InjectionUtils.injectByKey(Bind.class, bindTarget, elements.getValue(), elements.getKey());
                }
            }
        }

        for (DomObject<? extends T> domElement : elementsTagGroupHandler.getDom()) {
            addInputHandler(domElement.getTag());
        }
    }

}
