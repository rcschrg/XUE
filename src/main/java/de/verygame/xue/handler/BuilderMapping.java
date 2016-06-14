package de.verygame.xue.handler;

import de.verygame.xue.mapping.builder.XueTag;

/**
 * @author Rico Schrage
 */
public interface BuilderMapping<T> {
    XueTag<T> createBuilder(String name);
}
