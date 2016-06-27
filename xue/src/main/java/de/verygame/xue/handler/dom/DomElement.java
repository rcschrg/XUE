package de.verygame.xue.handler.dom;

import de.verygame.xue.constants.CoreAttribute;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.tag.XueTag;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class DomElement<T> extends DomObject<T> {

    public DomElement(XueTag<T> builder, GlobalMappings<T> mappings, List<DomObject<Object>> constantDom) {
        super(builder, mappings, constantDom);
    }



    @Override
    public void end() throws AttributeUnknownException {
        super.end();

        if (valueExists(CoreAttribute.ELEMENT_WIDTH, CoreAttribute.ELEMENT_HEIGHT, CoreAttribute.ELEMENT_MAX_HEIGHT,
                CoreAttribute.ELEMENT_MAX_WIDTH, CoreAttribute.ELEMENT_MIN_HEIGHT, CoreAttribute.ELEMENT_MIN_WIDTH)) {

            float width = getValue(CoreAttribute.ELEMENT_WIDTH).getValue(float.class);
            float height = getValue(CoreAttribute.ELEMENT_HEIGHT).getValue(float.class);
            float minWidth = getValue(CoreAttribute.ELEMENT_MIN_WIDTH).getValue(float.class);
            float maxWidth = getValue(CoreAttribute.ELEMENT_MAX_WIDTH).getValue(float.class);
            float minHeight = getValue(CoreAttribute.ELEMENT_MIN_HEIGHT).getValue(float.class);
            float maxHeight = getValue(CoreAttribute.ELEMENT_MAX_HEIGHT).getValue(float.class);

            if (minWidth > width && Float.compare(minWidth, -1f) != 0) {
                builder.apply(CoreAttribute.ELEMENT_WIDTH, minWidth);
            }
            else if (minHeight > height && Float.compare(minHeight, -1f) != 0) {
                builder.apply(CoreAttribute.ELEMENT_HEIGHT, minHeight);
            }

            if (height > maxHeight && Float.compare(maxHeight, -1f) != 0) {
                builder.apply(CoreAttribute.ELEMENT_HEIGHT, maxHeight);
            }
            else if (width > maxWidth && Float.compare(maxWidth, -1f) != 0) {
                builder.apply(CoreAttribute.ELEMENT_WIDTH, maxWidth);
            }
        }
    }
}
