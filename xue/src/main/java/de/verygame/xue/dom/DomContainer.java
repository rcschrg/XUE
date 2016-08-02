package de.verygame.xue.dom;

import java.util.List;

/**
 * @author Rico Schrage
 */
public interface DomContainer<D> {

    /**
     * Returns a map of the resultMap element mapped to their key string.
     *
     * @return map
     */
    List<D> getDom();
}
