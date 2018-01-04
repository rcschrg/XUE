package org.rschrage.util.modifier.base;

/**
 * @author Rico Schrage
 */
public interface ModifierCallback {

    void call(float value);
    void finish(float value);
    void init(float value);

}
