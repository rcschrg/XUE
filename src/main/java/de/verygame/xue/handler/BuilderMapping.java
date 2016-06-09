package de.verygame.xue.handler;

import de.verygame.xue.mapping.builder.GLMenuBuilder;

/**
 * @author Rico Schrage
 */
public interface BuilderMapping<T> {
    GLMenuBuilder<T> createBuilder(String name);
}
