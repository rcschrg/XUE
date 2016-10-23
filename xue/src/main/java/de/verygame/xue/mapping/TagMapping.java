package de.verygame.xue.mapping;

import de.verygame.xue.mapping.tag.XueTag;

/**
 * @author Rico Schrage
 */
public interface TagMapping<T> {
    XueTag<? extends T> createTag(String tagClass);
}
