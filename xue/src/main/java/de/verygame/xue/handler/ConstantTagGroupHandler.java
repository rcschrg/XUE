package de.verygame.xue.handler;

import de.verygame.xue.constants.CoreAttribute;
import de.verygame.xue.constants.Constant;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ConstTagUnknownException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.exception.XueSyntaxException;
import de.verygame.xue.handler.dom.DomObject;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.tag.PrimitiveTag;
import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public class ConstantTagGroupHandler extends BaseTagGroupHandler<Object, DomObject<? extends Object>> {

    public ConstantTagGroupHandler(Map<Constant, String> constantStringMap) {
        super(constantStringMap, Constant.CONST_TAG);
    }

    @Override
    public void handle(XmlPullParser xpp) throws XueSyntaxException, TagUnknownException, AttributeUnknownException {
        String nameAttr = "";
        XueTag<? extends Object> objectBuilder = null;
        for (BuilderMapping<? extends Object> m : mapping) {
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

        DomObject<Object> domObject = new DomObject<>(constantMap, objectBuilder);

        domObject.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);
            final String attributeName = xpp.getAttributeName(i);

            if (CoreAttribute.ELEMENT_ID.equals(xpp.getAttributeName(i))) {
                nameAttr = xpp.getAttributeValue(i);
                domObject.setName(nameAttr);

                if (nameAttr.isEmpty()) {
                    throw new XueSyntaxException(xpp.getLineNumber(), "Missing name attribute!");
                }
                continue;
            }
            domObject.apply(attributeName, attributeValue);
        }
        domObject.end();

        domList.add(domObject);
    }

    /**
     * Creates all primitive constants.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     * @throws XueSyntaxException if name attribute is missing.
     */
    private void handlePrimitiveConst(XmlPullParser xpp) throws XueSyntaxException {
        String nameAttr = "";
        DomObject<Object> dom = new DomObject<>(constantMap, new PrimitiveTag());

        dom.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            String attributeValue = xpp.getAttributeValue(i);
            String attributeName = xpp.getAttributeName(i);

            if (CoreAttribute.ELEMENT_ID.equals(attributeName)) {
                nameAttr = xpp.getAttributeValue(i);
                continue;
            }

           dom.apply(attributeName, attributeValue);
        }
        dom.setName(nameAttr);
        dom.end();

        if (nameAttr.isEmpty()) {
            throw new XueSyntaxException(xpp.getLineNumber(), "Missing name attribute!");
        }

        domList.add(dom);
    }
}
