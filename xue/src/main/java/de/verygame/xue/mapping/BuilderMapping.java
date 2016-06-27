package de.verygame.xue.mapping;

import de.verygame.xue.mapping.tag.XueTag;

/**
 * @author Rico Schrage
 */
public interface BuilderMapping<T> {
    XueTag<? extends T> createBuilder(String name);
}
