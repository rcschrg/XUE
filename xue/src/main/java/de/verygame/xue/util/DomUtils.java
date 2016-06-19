package de.verygame.xue.util;

import de.verygame.xue.constants.CoreAttribute;
import de.verygame.xue.handler.dom.DomRepresentation;
import org.xmlpull.v1.XmlPullParser;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class DomUtils {

    private DomUtils() {
        //utility class
    }

    public static <T> DomRepresentation<T> searchFor(List<? extends DomRepresentation<T>> domList, String name) {
        for (int i = 0; i < domList.size(); ++i) {
            DomRepresentation<T> domElement = domList.get(i);
            if (domElement.getName().equals(name)) {
                return domElement;
            }
        }
        throw new IllegalArgumentException("There does not exists a DOM element with the name " + name);
    }

    public static <T> void applyTagToDom(DomRepresentation<T> domRepresentation, XmlPullParser xpp) {
        domRepresentation.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);
            final String attributeName = xpp.getAttributeName(i);

            if (CoreAttribute.ELEMENT_ID.equals(attributeName)) {
                domRepresentation.setName(attributeValue);
                continue;
            }

            domRepresentation.apply(attributeName, attributeValue);
        }
        domRepresentation.end();
    }

}
