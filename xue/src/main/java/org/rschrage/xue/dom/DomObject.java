package org.rschrage.xue.dom;

import org.rschrage.util.math.CoordinateType;
import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.dom.value.*;
import org.rschrage.xue.exception.AttributeUnknownException;
import org.rschrage.xue.exception.ElementTagUnknownException;
import org.rschrage.xue.exception.XueException;
import org.rschrage.xue.mapping.DummyGlobalMappings;
import org.rschrage.xue.mapping.GlobalMappings;
import org.rschrage.xue.mapping.tag.XueTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 */
public class DomObject<T> implements DomRepresentation<T> {
    protected final XueTag<? extends T> builder;
    private final Map<String, Value<?, ?>> attributeValueMap;
    private final Map<FloatValue, Float> floatValueToAbsoluteValueMap;
    private final GlobalMappings<T> mappings;
    private final List<DomObject<Object>> constantDom;
    protected final Map<Constant, String> constantMap;
    protected String name;
    protected String domain;
    private int layer;

    public DomObject(Map<Constant, String> constantStringMap, XueTag<? extends T> builder) {
        this(constantStringMap, builder, new DummyGlobalMappings<T>());
    }

    public DomObject(Map<Constant, String> constantStringMap, XueTag<? extends T> builder, GlobalMappings<T> mappings) {
        this(constantStringMap, builder, mappings, new ArrayList<DomObject<Object>>());
    }

    public DomObject(Map<Constant, String> constantStringMap, XueTag<? extends T> builder, GlobalMappings<T> mappings, List<DomObject<Object>> constantDom) {
        this.builder = builder;
        this.mappings = mappings;
        this.constantDom = constantDom;
        this.constantMap = constantStringMap;

        this.attributeValueMap = new HashMap<>();
        this.floatValueToAbsoluteValueMap = new HashMap<>();
    }

    /**
     * Applies the attribute <code>attributeName</code> with the value <code>attributeValue</code> to the given element <code>element</code>.
     * <br>
     * This method will be called if the value is a relative number.
     *
     * @param attributeName name of the attribute, which should be applied
     * @param attributeValue value of the attribute, which should be applied
     * @throws AttributeUnknownException thrown if the given attribute is unknown
     */
    private void applyGenericRelativeDigitToElement(String attributeName, String attributeValue) throws XueException {
        float relativeValue = Float.parseFloat(attributeValue.substring(0, attributeValue.length() - 2));

        if (attributeValue.endsWith(constantMap.get(Constant.REL_VALUE_SUFFIX_X))) {
            float relativeValueX = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.X);
            applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.X), relativeValueX);
        }
        else if (attributeValue.endsWith(constantMap.get(Constant.REL_VALUE_SUFFIX_Y))) {
            float relativeValueY = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.Y);
            applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.Y), relativeValueY);
        }
    }

    /**
     * Applies the attribute <code>attributeName</code> with the value <code>attributeValue</code> to the given element <code>element</code>.
     * <br>
     * This method will be called if the value is a relative number.
     *
     * @param attributeName name of the attribute, which should be applied
     * @param attributeValue value of the attribute, which should be applied
     * @throws AttributeUnknownException thrown if the given attribute is unknown
     */
    private void applyRelativeDigitToElement(String attributeName, String attributeValue) throws XueException {
        float relativeValue = Float.parseFloat(attributeValue.substring(0, attributeValue.length() - 1));

        String constWidth = constantMap.get(Constant.ELEMENT_WIDTH);
        String constHeight = constantMap.get(Constant.ELEMENT_HEIGHT);
        String constX = constantMap.get(Constant.ELEMENT_X);
        String constY = constantMap.get(Constant.ELEMENT_Y);

        if (constWidth.equals(attributeName) || constX.equals(attributeName)) {
            float absoluteValueX = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.X);
            applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.X), absoluteValueX);
        }
        else if (constHeight.equals(attributeName) || constY.equals(attributeName)) {
            float absoluteValueY = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.Y);
            applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.Y), absoluteValueY);
        }
    }

    /**
     * Applies the attribute <code>attributeName</code> with the value <code>attributeValue</code> to the given element <code>element</code>.
     * <br>
     * This method will be called if the value is a constant.
     *
     * @param attributeName name of the attribute, which should be applied
     * @param attributeValue value of the attribute, which should be applied
     * @throws AttributeUnknownException
     * @throws ElementTagUnknownException
     */
    private void applyConstToElement(String attributeName, String attributeValue) throws XueException {
        Object constObj = null;
        for (int i = 0 ; i < constantDom.size(); ++i) {
            DomObject<?> domObject = constantDom.get(i);
            if (domObject.getName().equals(attributeValue.substring(1, attributeValue.length()))) {
                constObj = domObject.getObject();
            }
        }
        if (constObj == null) {
            return;
        }

        applyConst(attributeName, new ObjectValue(constObj));
    }

    protected void onApplyFloat(String attribute, float floatValue) {
        //for subclasses
    }

    protected void onApplyConst(String attribute, Object value, XueTag<? extends T> builder) {
        //for subclasses
    }

    protected void onApplyString(String attribute, String value) {
        //for subclasses
    }

    protected void onApplyInt(String attribute, int integer) {
        //for subclasses
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public void applyString(String attribute, Value<?, ?> value) {
        onApplyString(attribute, value.getValue(String.class));
        builder.apply(attribute, value.getValue(String.class));
        attributeValueMap.put(attribute, value);
    }

    public void applyFloat(String attribute, FloatValue<?> value) {
        onApplyFloat(attribute, value.getValue(Float.class));
        builder.apply(attribute, value.getValue(Float.class));
        floatValueToAbsoluteValueMap.put(value, value.getValue());
        attributeValueMap.put(attribute, value);
    }

    public void applyInt(String attribute, Value<?, ?> value) {
        onApplyInt(attribute, value.getValue(Integer.class));
        builder.apply(attribute, value.getValue(Integer.class));
        attributeValueMap.put(attribute, value);
    }

    public void applyConst(String attribute, Value<?, ?> value) {
        onApplyConst(attribute, value.getValue(Object.class), builder);
        builder.apply(attribute, value.getValue());
        attributeValueMap.put(attribute, value);
    }

    public void applyRelative(String attribute, FloatValue<?> value, float calculatedValue) {
        onApplyFloat(attribute, calculatedValue);
        builder.apply(attribute, calculatedValue);
        floatValueToAbsoluteValueMap.put(value, calculatedValue);
        attributeValueMap.put(attribute, value);
    }

    public void applyDensity(String attribute, FloatValue<?> value, float calculatedValue) {
        onApplyFloat(attribute, calculatedValue);
        builder.apply(attribute, calculatedValue);
        floatValueToAbsoluteValueMap.put(value, calculatedValue);
        attributeValueMap.put(attribute, value);
    }

    public void applyStringId(String attribute, Value<?, ?> value, String mappedString) {
        onApplyString(attribute, mappedString);
        builder.apply(attribute, mappedString);
        attributeValueMap.put(attribute, value);
    }

    @Override
    public void apply(String attribute, String value) throws XueException {
        if (value.matches(constantMap.get(Constant.ATT_REL_GEN_VALUE_REGEX))) {
            applyGenericRelativeDigitToElement(attribute, value);
        }
        else if (value.matches(constantMap.get(Constant.ATT_REL_VALUE_REGEX))) {
            applyRelativeDigitToElement(attribute, value);
        }
        else if (value.startsWith(constantMap.get(Constant.ATT_CONST_ID))) {
            applyConstToElement(attribute, value);
        }
        else if (value.startsWith(constantMap.get(Constant.ATT_STRING_ID))) {
            String stringKey = value.substring(constantMap.get(Constant.ATT_STRING_ID).length());
            String string = mappings.getString(stringKey);

            applyStringId(attribute, new StringValue(stringKey, StringValue.StringType.ID), string);
        }
        else if (value.matches(constantMap.get(Constant.ATT_DENSITY_REGEX))) {
            float num = Float.parseFloat(value.substring(0, value.length()-1));
            float result = Float.parseFloat(value.substring(0, value.length()-1)) * mappings.getDensity();

            applyDensity(attribute, new FloatValue.Density(num), result);
        }
        else if (value.matches(constantMap.get(Constant.ATT_INT_REGEX))) {
            int integer = Integer.parseInt(value);

            applyInt(attribute, new IntValue(integer));
        }
        else if (value.matches(constantMap.get(Constant.ATT_FLOAT_REGEX))) {
            float f = Float.parseFloat(value);

            applyFloat(attribute, new FloatValue.Normal(f));
        }
        else {
            applyString(attribute, new StringValue(value, StringValue.StringType.RAW));
        }
    }

    @Override
    public void begin() {
        builder.preBuild();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void end() throws AttributeUnknownException {
        builder.postBuild();

        String stringWidth = constantMap.get(Constant.ELEMENT_WIDTH);
        String stringHeight = constantMap.get(Constant.ELEMENT_HEIGHT);

        String stringMaxWidth = constantMap.get(Constant.ELEMENT_MAX_WIDTH);
        String stringMinWidth = constantMap.get(Constant.ELEMENT_MIN_WIDTH);
        String stringMaxHeight = constantMap.get(Constant.ELEMENT_MAX_HEIGHT);
        String stringMinHeight = constantMap.get(Constant.ELEMENT_MIN_HEIGHT);

        float width = valueExists(stringWidth) ? floatValueToAbsoluteValueMap.get(getValue(stringWidth)) : 0;
        float height = valueExists(stringHeight) ? floatValueToAbsoluteValueMap.get(getValue(stringHeight)) : 0;
        float minWidth = valueExists(stringMinWidth) ? floatValueToAbsoluteValueMap.get(getValue(stringMinWidth)) : 0;
        float maxWidth = valueExists(stringMaxWidth) ? floatValueToAbsoluteValueMap.get(getValue(stringMaxWidth)) : Float.MAX_VALUE;
        float minHeight = valueExists(stringMinHeight) ? floatValueToAbsoluteValueMap.get(getValue(stringMinHeight)) : 0;
        float maxHeight = valueExists(stringMaxHeight) ? floatValueToAbsoluteValueMap.get(getValue(stringMaxHeight)) : Float.MAX_VALUE;

        if (minWidth > width) {
            builder.apply(stringWidth, minWidth);
        }
        else if (minHeight > height) {
            builder.apply(stringHeight, minHeight);
        }

        if (height > maxHeight) {
            builder.apply(stringHeight, maxHeight);
        }
        else if (width > maxWidth) {
            builder.apply(stringWidth, maxWidth);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public Value<?, ?> getValue(String attribute) {
        return attributeValueMap.get(attribute);
    }

    @Override
    public Map<String, Value<?, ?>> getValues() {
        return attributeValueMap;
    }

    @Override
    public boolean valueExists(String... attributes) {
        boolean exists = true;
        for (String attribute : attributes) {
            exists = exists && attributeValueMap.containsKey(attribute);
        }
        return exists;
    }

    @Override
    public T getObject() {
        return builder.getElement();
    }

    @Override
    public XueTag<? extends T> getTag() {
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomObject<?> domObject = (DomObject<?>) o;

        if (layer != domObject.layer) return false;
        if (builder != null ? !builder.equals(domObject.builder) : domObject.builder != null) return false;
        if (attributeValueMap != null ? !attributeValueMap.equals(domObject.attributeValueMap) : domObject.attributeValueMap != null)
            return false;
        if (floatValueToAbsoluteValueMap != null ? !floatValueToAbsoluteValueMap.equals(domObject.floatValueToAbsoluteValueMap) : domObject.floatValueToAbsoluteValueMap != null)
            return false;
        if (mappings != null ? !mappings.equals(domObject.mappings) : domObject.mappings != null) return false;
        if (constantDom != null ? !constantDom.equals(domObject.constantDom) : domObject.constantDom != null)
            return false;
        if (constantMap != null ? !constantMap.equals(domObject.constantMap) : domObject.constantMap != null)
            return false;
        return name != null ? name.equals(domObject.name) : domObject.name == null;

    }

    @Override
    public int hashCode() {
        int result = builder != null ? builder.hashCode() : 0;
        result = 31 * result + (attributeValueMap != null ? attributeValueMap.hashCode() : 0);
        result = 31 * result + (floatValueToAbsoluteValueMap != null ? floatValueToAbsoluteValueMap.hashCode() : 0);
        result = 31 * result + (mappings != null ? mappings.hashCode() : 0);
        result = 31 * result + (constantDom != null ? constantDom.hashCode() : 0);
        result = 31 * result + (constantMap != null ? constantMap.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + layer;
        return result;
    }
}
