package de.verygame.xue.constants;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Rico Schrage
 *
 * Contains various constants for glmenu.
 */
public enum Constant {

    ELEMENT_TAG("elements"),
    CONST_TAG("constants"),
    AS_TAG("actionSequences"),
    AS_SUB_TAG("actionSequence"),

    ATT_STRING_ID("@string/"),
    ATT_CONST_ID("$"),

    REL_VALUE_SUFFIX_X("x"),
    REL_VALUE_SUFFIX_Y("y"),

    ATT_REL_CHAR("r"),
    ATT_DENSITY_CHAR("d"),
    ATT_REL_GEN_VALUE_REGEX("[0-9]*" + ATT_REL_CHAR + "[xy]"),
    ATT_REL_VALUE_REGEX("[0-9]*" + ATT_REL_CHAR),
    ATT_INT_REGEX("[0-9]*"),
    ATT_FLOAT_REGEX("[0-9]*[.][0-9]*"),
    ATT_DENSITY_REGEX("[0-9]*" + ATT_DENSITY_CHAR),

    ELEMENT_ID("name"),
    ELEMENT_X("x"),
    ELEMENT_Y("y"),
    ELEMENT_WIDTH("width"),
    ELEMENT_HEIGHT("height"),
    ELEMENT_MAX_WIDTH("maxWidth"),
    ELEMENT_MAX_HEIGHT("maxHeight"),
    ELEMENT_MIN_WIDTH("minWidth"),
    ELEMENT_MIN_HEIGHT("minHeight"),
    ACTION_TARGET_ID("target");

    private static final Map<Constant, String> defaultMap = new EnumMap<>(Constant.class);

    static {
        for (Constant constant : Constant.values()) {
            defaultMap.put(constant, constant.toString());
        }
    }

    public static Map<Constant, String> obtainDefaultMap(){
        return defaultMap;
    }

    private final String constant;

    Constant(String constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return constant;
    }
}
