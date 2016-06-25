package de.verygame.xue.handler.dom;

import de.verygame.xue.constants.CoreAttribute;
import de.verygame.xue.constants.Globals;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.dom.value.*;
import de.verygame.util.math.CoordinateType;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.tag.XueTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 */
public class DomObject<T> implements DomRepresentation<T> {
    protected final XueTag<T> builder;
    private final Map<String, Value<?, ?>> attributeValueMap;
    private final GlobalMappings<T> mappings;
    private final List<DomObject<Object>> constantDom;
    protected String name;
    private int layer;

    public DomObject(XueTag<T> builder) {
        this(builder, new DummyGlobalMappings<T>());
    }

    public DomObject(XueTag<T> builder, GlobalMappings<T> mappings) {
        this(builder, mappings, new ArrayList<DomObject<Object>>());
    }

    public DomObject(XueTag<T> builder, GlobalMappings<T> mappings, List<DomObject<Object>> constantDom) {
        this.builder = builder;
        this.mappings = mappings;
        this.constantDom = constantDom;

        this.attributeValueMap = new HashMap<>();
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

        if (attributeValue.endsWith(Globals.REL_VALUE_SUFFIX_X)) {
            float relativeValueX = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.X);
            applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.X), relativeValueX);
        }
        else if (attributeValue.endsWith(Globals.REL_VALUE_SUFFIX_Y)) {
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

        switch (attributeName) {

            case CoreAttribute.ELEMENT_WIDTH:
            case CoreAttribute.ELEMENT_X:
                float absoluteValueX = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.X);
                applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.X), absoluteValueX);
                break;

            case CoreAttribute.ELEMENT_HEIGHT:
            case CoreAttribute.ELEMENT_Y:
                float absoluteValueY = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, CoordinateType.Y);
                applyRelative(attributeName, new FloatValue.Relative(relativeValue, CoordinateType.Y), absoluteValueY);
                break;

            default:
                break;
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
            DomObject<Object> domObject = constantDom.get(i);
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

    protected void onApplyConst(String attribute, Object value, XueTag<T> builder) {
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

    public void applyFloat(String attribute, Value<?, ?> value) {
        onApplyFloat(attribute, value.getValue(Float.class));
        builder.apply(attribute, value.getValue(Float.class));
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

    public void applyRelative(String attribute, Value<?, ?> value, float calculatedValue) {
        onApplyFloat(attribute, calculatedValue);
        builder.apply(attribute, calculatedValue);
        attributeValueMap.put(attribute, value);
    }

    public void applyDensity(String attribute, Value<?, ?> value, float calculatedValue) {
        onApplyFloat(attribute, calculatedValue);
        builder.apply(attribute, calculatedValue);
        attributeValueMap.put(attribute, value);
    }

    public void applyStringId(String attribute, Value<?, ?> value, String mappedString) {
        onApplyString(attribute, mappedString);
        builder.apply(attribute, mappedString);
        attributeValueMap.put(attribute, value);
    }

    @Override
    public void apply(String attribute, String value) throws XueException {
        if (value.matches(Globals.ATT_REL_GEN_VALUE_REGEX)) {
            applyGenericRelativeDigitToElement(attribute, value);
        }
        else if (value.matches(Globals.ATT_REL_VALUE_REGEX)) {
            applyRelativeDigitToElement(attribute, value);
        }
        else if (value.startsWith(Globals.ATT_CONST_ID)) {
            applyConstToElement(attribute, value);
        }
        else if (value.startsWith(Globals.ATT_STRING_ID)) {
            String stringKey = value.substring(Globals.ATT_STRING_ID.length());
            String string = mappings.getString(stringKey);

            applyStringId(attribute, new StringValue(stringKey, StringValue.StringType.ID), string);
        }
        else if (value.matches(Globals.ATT_DENSITY_REGEX)) {
            float num = Float.parseFloat(value.substring(0, value.length()-1));
            float result = Float.parseFloat(value.substring(0, value.length()-1)) * mappings.getDensity();

            applyDensity(attribute, new FloatValue.Density(num), result);
        }
        else if (value.matches(Globals.ATT_INT_REGEX)) {
            int integer = Integer.parseInt(value);

            applyInt(attribute, new IntValue(integer));
        }
        else if (value.matches(Globals.ATT_FLOAT_REGEX)) {
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

    @Override
    public void end() throws AttributeUnknownException {
        builder.postBuild();
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
    public XueTag<T> getTag() {
        return builder;
    }

}
