package de.verygame.xue.exception;

/**
 * Created by Rico on 09.07.2015.
 *
 * @author Rico Schrage
 */
public class ConstTagUnknownException extends TagUnknownException {

    public ConstTagUnknownException(String tagName) {
        super(String.format("The constant tag '%s' is unknown!", tagName));
    }

}
