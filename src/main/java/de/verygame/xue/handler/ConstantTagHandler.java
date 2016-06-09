package de.verygame.xue.handler;

import org.xmlpull.v1.XmlPullParser;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ConstTagUnknownException;
import de.verygame.xue.exception.GLMenuSyntaxException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.mapping.builder.GLMenuBuilder;
import de.verygame.xue.handler.dom.DomObject;

/**
 * @author Rico Schrage
 */
public class ConstantTagHandler extends BaseTagHandler<Object, DomObject<Object>> {

    public ConstantTagHandler() {
        super(Globals.CONST_TAG);
    }

    @Override
    public void handle(XmlPullParser xpp) throws GLMenuSyntaxException, TagUnknownException, AttributeUnknownException {
        String nameAttr = "";
        GLMenuBuilder<Object> objectBuilder = null;
        for (BuilderMapping<Object> m : mapping) {
            objectBuilder = m.createBuilder(xpp.getName());
            if (objectBuilder != null) {
                break;
            }
        }

        if ("const".equals(xpp.getName())) {
            handlePrimitiveConst(xpp);
            return;
        }

        if (objectBuilder == null) {
            throw new ConstTagUnknownException(xpp.getName());
        }

        DomObject<Object> domObject = new DomObject<>(objectBuilder);

        domObject.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);
            final String attributeName = xpp.getAttributeName(i);

            if (CoreAttribute.ELEMENT_ID.equals(xpp.getAttributeName(i))) {
                nameAttr = xpp.getAttributeValue(i);

                if (nameAttr.isEmpty()) {
                    throw new GLMenuSyntaxException(xpp.getLineNumber(), "Missing name attribute!");
                }
                continue;
            }

            domObject.apply(attributeName, attributeValue);
        }
        domObject.end();

        resultMap.put(nameAttr, objectBuilder.getElement());
        domMap.put(nameAttr, domObject);
    }

    /**
     * Creates all primitive constants.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     * @throws GLMenuSyntaxException if name attribute is missing.
     */
    private void handlePrimitiveConst(XmlPullParser xpp) throws GLMenuSyntaxException {

        String nameAttr = "";
        Object o = 0;

        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);

            if (CoreAttribute.ELEMENT_ID.equals(xpp.getAttributeName(i))) {
                nameAttr = xpp.getAttributeValue(i);
                continue;
            }

            if (attributeValue.matches(Globals.ATT_INT_REGEX)) {
                o = Integer.parseInt(attributeValue);
            }
            else if (attributeValue.matches(Globals.ATT_FLOAT_REGEX)) {
                o = Float.parseFloat(attributeValue);
            }
            else {
                o = attributeValue;
            }
        }

        if (nameAttr.isEmpty()) {
            throw new GLMenuSyntaxException(xpp.getLineNumber(), "Missing name attribute!");
        }

        resultMap.put(nameAttr, o);
    }
}
