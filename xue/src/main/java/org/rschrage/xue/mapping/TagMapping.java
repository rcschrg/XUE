package org.rschrage.xue.mapping;

import org.rschrage.xue.mapping.tag.XueTag;

/**
 * @author Rico Schrage
 */
public interface TagMapping<T> {
    XueTag<? extends T> createTag(String tagClass);
}
