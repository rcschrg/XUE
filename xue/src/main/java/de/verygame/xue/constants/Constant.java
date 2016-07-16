package de.verygame.xue.constants;

/**
 * @author Rico Schrage
 *
 * Contains various constants for glmenu.
 */
public enum Constant {

    ELEMENT_TAG("elements"),
    CONST_TAG("constants"),
    AS_TAG("actionSequence"),

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
    ATT_DENSITY_REGEX("[0-9]*" + ATT_DENSITY_CHAR);

    private final String constant;

    Constant(String constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return constant;
    }
}
