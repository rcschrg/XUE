package de.verygame.xue.handler.dom;

import java.util.HashMap;
import java.util.Map;

import de.verygame.xue.util.Tuple;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.handler.CoreAttribute;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.GlobalMappings.CoordinateType;
import de.verygame.xue.handler.Globals;
import de.verygame.xue.mapping.builder.XueTag;

/**
 * @author Rico Schrage
 */
public class DomElement<T> extends DomObject<T> {
    private final static int DEFAULT_REL_VALUE_SIZE = 5;

    private final GlobalMappings<T> mappings;
    private final Map<String, Object> constantMap;
    private final Map<String, Tuple<CoordinateType, Float>> relativeValueMap;
    private int layer;

    private float width;
    private float height;

    private float minWidth = -1;
    private float minHeight = -1;
    private float maxWidth = -1;
    private float maxHeight = -1;

    public DomElement(XueTag<T> builder, GlobalMappings<T> mappings, Map<String, Object> constantMap) {
        super(builder);

        this.mappings = mappings;
        this.constantMap = constantMap;
        this.relativeValueMap = new HashMap<>(DEFAULT_REL_VALUE_SIZE);
    }

    public void resize() throws AttributeUnknownException {
        begin();
        for (Map.Entry<String, Tuple<CoordinateType, Float>> entry : relativeValueMap.entrySet()) {
            float f = mappings.calcFromRelativeValue(builder.getElement(), entry.getValue().getSecond(), entry.getValue().getFirst());
            onApplyFloat(entry.getKey(), f);
            builder.applyFloat(entry.getKey(), f);
        }
        end();
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
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
    private void applyGenericRelativeDigitToElement(String attributeName, String attributeValue) throws AttributeUnknownException {
        float relativeValue = Float.parseFloat(attributeValue.substring(0, attributeValue.length() - 2));

        if (attributeValue.endsWith(Globals.REL_VALUE_SUFFIX_X)) {
            builder.applyFloat(attributeName, mappings.calcFromRelativeValue(builder.getElement(), relativeValue, GlobalMappings.CoordinateType.X));
            relativeValueMap.put(attributeName, new Tuple<>(CoordinateType.X, relativeValue));
        }
        else if (attributeValue.endsWith(Globals.REL_VALUE_SUFFIX_Y)) {
            builder.applyFloat(attributeName, mappings.calcFromRelativeValue(builder.getElement(), relativeValue, GlobalMappings.CoordinateType.Y));
            relativeValueMap.put(attributeName, new Tuple<>(CoordinateType.Y, relativeValue));
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
    private void applyRelativeDigitToElement(String attributeName, String attributeValue) throws AttributeUnknownException {
        float relativeValue = Float.parseFloat(attributeValue.substring(0, attributeValue.length() - 1));

        switch (attributeName) {

            case CoreAttribute.ELEMENT_WIDTH:
            case CoreAttribute.ELEMENT_X:
                float absoluteValueX = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, GlobalMappings.CoordinateType.X);
                onApplyFloat(attributeName, absoluteValueX);
                builder.applyFloat(attributeName, absoluteValueX);
                relativeValueMap.put(attributeName, new Tuple<>(CoordinateType.X, relativeValue));
                break;

            case CoreAttribute.ELEMENT_HEIGHT:
            case CoreAttribute.ELEMENT_Y:
                float absoluteValueY = mappings.calcFromRelativeValue(builder.getElement(), relativeValue, GlobalMappings.CoordinateType.Y);
                onApplyFloat(attributeName, absoluteValueY);
                builder.applyFloat(attributeName, absoluteValueY);
                relativeValueMap.put(attributeName, new Tuple<>(CoordinateType.Y, relativeValue));
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
     * @param element element, which should get applied the attribute
     * @throws AttributeUnknownException
     * @throws ElementTagUnknownException
     */
    private void applyConstToElement(String attributeName, String attributeValue, XueTag<T> element) throws AttributeUnknownException, TagUnknownException {
        Object constObj = constantMap.get(attributeValue.substring(1, attributeValue.length()));
        if (constObj == null) {
            return;
        }

        if (constObj instanceof Integer) {
            element.applyInt(attributeName, (int) constObj);
        }
        else if (constObj instanceof Float) {
            element.applyFloat(attributeName, (float) constObj);
        }
        else {
            element.applyObject(attributeName, constObj);
        }
    }

    @Override
    public void apply(String attribute, String value) throws AttributeUnknownException, TagUnknownException {

        if (value.matches(Globals.ATT_REL_GEN_VALUE_REGEX)) {
            applyGenericRelativeDigitToElement(attribute, value);
        }
        else if (value.matches(Globals.ATT_REL_VALUE_REGEX)) {
            applyRelativeDigitToElement(attribute, value);
        }
        else if (value.startsWith(Globals.ATT_CONST_ID)) {
            onApplyConst(attribute, value, builder);
            applyConstToElement(attribute, value, builder);
        }
        else if (value.startsWith(Globals.ATT_STRING_ID)) {
            final String stringKey = value.substring(Globals.ATT_STRING_ID.length());
            final String string = mappings.getString(stringKey);
            builder.applyString(attribute, string);
        }
        else if (value.matches(Globals.ATT_DENSITY_REGEX)) {
            float number = Float.parseFloat(value.substring(0, value.length()-1)) * mappings.getDensity();
            onApplyFloat(attribute, number);
            builder.applyFloat(attribute, number);
        }
        else {
            super.apply(attribute, value);
        }
    }

    @Override
    public void end() throws AttributeUnknownException {
        super.end();

        if (minWidth > width && Float.compare(minWidth, -1f) != 0) {
            builder.applyFloat(CoreAttribute.ELEMENT_WIDTH, minWidth);
        }
        else if (minHeight > height && Float.compare(minHeight, -1f) != 0) {
            builder.applyFloat(CoreAttribute.ELEMENT_HEIGHT, minHeight);
        }

        if (height > maxHeight && Float.compare(maxHeight, -1f) != 0) {
            builder.applyFloat(CoreAttribute.ELEMENT_HEIGHT, maxHeight);
        }
        else if (width > maxWidth && Float.compare(maxWidth, -1f) != 0) {
            builder.applyFloat(CoreAttribute.ELEMENT_WIDTH, maxWidth);
        }
    }

    @Override
    protected void onApplyFloat(String attribute, float f) {
        switch (attribute) {
            case CoreAttribute.ELEMENT_WIDTH:
                width = f;
                break;

            case CoreAttribute.ELEMENT_HEIGHT:
                height = f;
                break;

            case CoreAttribute.ELEMENT_MIN_WIDTH:
                minWidth = f;
                break;

            case CoreAttribute.ELEMENT_MIN_HEIGHT:
                minHeight = f;
                break;

            case CoreAttribute.ELEMENT_MAX_WIDTH:
                maxWidth = f;
                break;

            case CoreAttribute.ELEMENT_MAX_HEIGHT:
                maxHeight = f;
                break;

            default:
                break;
        }
    }
}
