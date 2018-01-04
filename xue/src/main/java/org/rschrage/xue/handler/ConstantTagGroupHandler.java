package org.rschrage.xue.handler;

import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.exception.AttributeUnknownException;
import org.rschrage.xue.exception.ConstTagUnknownException;
import org.rschrage.xue.exception.TagUnknownException;
import org.rschrage.xue.exception.XueSyntaxException;
import org.rschrage.xue.dom.DomObject;
import org.rschrage.xue.mapping.TagMapping;
import org.rschrage.xue.mapping.tag.XueTag;
import org.rschrage.xue.handler.tag.PrimitiveTag;
import org.xmlpull.v1.XmlPullParser;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public class ConstantTagGroupHandler extends BaseTagGroupHandler<Object, DomObject<?>> {

    public ConstantTagGroupHandler() {
        super(Constant.obtainDefaultMap(), Constant.CONST_TAG);
    }

    public ConstantTagGroupHandler(Map<Constant, String> constantStringMap) {
        super(constantStringMap, Constant.CONST_TAG);
    }

    @Override
    public void handle(XmlPullParser xpp) throws XueSyntaxException, TagUnknownException, AttributeUnknownException {
        XueTag<?> objectBuilder = null;
        for (TagMapping<?> m : mapping) {
            objectBuilder = m.createTag(xpp.getName());
            if (objectBuilder != null) {
                break;
            }
        }

        if (PrimitiveTag.NAME.equals(xpp.getName())) {
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

            if (constantMap.get(Constant.ELEMENT_ID).equals(xpp.getAttributeName(i))) {
                String nameAttr = xpp.getAttributeValue(i);
                domObject.setName(nameAttr);

                if (nameAttr.isEmpty()) {
                    throw new XueSyntaxException(xpp.getLineNumber(), "Missing name attribute!");
                }
                continue;
            }
            domObject.apply(attributeName, attributeValue);
        }
        domObject.end();

        addToDom(domObject);
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

            if (constantMap.get(Constant.ELEMENT_ID).equals(attributeName)) {
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

        addToDom(dom);
    }
}
