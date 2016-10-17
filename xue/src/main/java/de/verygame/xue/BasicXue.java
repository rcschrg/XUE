package de.verygame.xue;

import de.verygame.xue.annotation.Bind;
import de.verygame.xue.handler.ConstantTagGroupHandler;
import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.util.InjectionUtils;

import java.io.InputStream;
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

    public BasicXue(InputStream xml) {
        this(xml, new DummyGlobalMappings<T>());
    }

    /**
     * Creates a container with the given mappings.
     *
     * @param xml xml
     */
    public BasicXue(InputStream xml, GlobalMappings<T> globalMappings) {
        super(xml);

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
