package de.verygame.xue.exception;

/**
 * Created by Rico on 09.07.2015.
 *
 * @author Rico Schrage
 */
public class AttributeUnknownException extends XueException {

    public AttributeUnknownException(final String element, String attribute) {
        super(String.format("The attribute '%s' cannot be applied to element '%s'", attribute, element));
    }

}
