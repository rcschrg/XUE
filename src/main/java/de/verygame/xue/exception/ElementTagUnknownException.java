package de.verygame.xue.exception;

/**
 * Created by Rico on 09.07.2015.
 *
 * @author Rico Schrage
 */
public class ElementTagUnknownException extends TagUnknownException {

    public ElementTagUnknownException(final String tagName) {
        super(String.format("The element tag '%s' is unknown!", tagName));
    }

}
