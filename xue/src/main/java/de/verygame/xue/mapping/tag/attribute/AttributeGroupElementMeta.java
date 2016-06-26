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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeGroupElementMeta that = (AttributeGroupElementMeta) o;

        return name != null ? name.equals(that.name) : that.name == null && (valueType != null ?
                valueType.equals(that.valueType) : that.valueType == null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        return result;
    }
}
