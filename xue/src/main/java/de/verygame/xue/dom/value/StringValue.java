package de.verygame.xue.dom.value;

/**
 * @author Rico Schrage
 */
public class StringValue extends AbstractValue<String, StringValue.StringType> {

    public StringValue(String value, StringType addition) {
        super(value, addition);
    }

    public enum StringType {
        RAW, ID
    }
}
