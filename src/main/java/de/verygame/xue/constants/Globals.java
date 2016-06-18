package de.verygame.xue.constants;

/**
 * @author Rico Schrage
 *
 * Contains various constants for glmenu.
 */
public class Globals {

    public static final String ELEMENT_TAG = "elements";
    public static final String CONST_TAG = "constants";
    public static final String AS_TAG = "actionSequence";

    public static final String ATT_STRING_ID = "@string/";
    public static final String ATT_CONST_ID = "$";

    public static final String REL_VALUE_SUFFIX_X = "x";
    public static final String REL_VALUE_SUFFIX_Y = "y";

    public static final String ATT_REL_GEN_VALUE_REGEX = "[0-9]*r[xy]";
    public static final String ATT_REL_VALUE_REGEX = "[0-9]*r";
    public static final String ATT_INT_REGEX = "[0-9]*";
    public static final String ATT_FLOAT_REGEX = "[0-9]*[.][0-9]*";
    public static final String ATT_DENSITY_REGEX = "[0-9]*d";

    private Globals() {
        // class for storing constants
    }

}
