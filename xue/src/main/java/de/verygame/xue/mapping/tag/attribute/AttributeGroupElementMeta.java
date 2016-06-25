package de.verygame.xue.mapping.tag.attribute;

/**
 * @author Rico Schrage
 */
public class AttributeGroupElementMeta {

    private String name;
    private Class<?> valueType;

    public AttributeGroupElementMeta(String name, Class<?> valueType) {
        this.valueType = valueType;
        this.name = name;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
