package org.rschrage.xue.util;

import org.rschrage.xue.dom.DomRepresentation;
import org.xmlpull.v1.XmlPullParser;

import java.util.List;

/**
 * @author Rico Schrage
 */
public class DomUtils {

    private DomUtils() {
        //utility class
    }

    public static <T> DomRepresentation<? extends T> searchFor(List<? extends DomRepresentation<? extends T>> domList, String name) {
        for (int i = 0; i < domList.size(); ++i) {
            DomRepresentation<? extends T> domElement = domList.get(i);
            if (domElement.getName().equals(name)) {
                return domElement;
            }
        }
        throw new IllegalArgumentException("There does not exists a DOM element with the name " + name);
    }

    public static <T> void applyTagToDom(DomRepresentation<?> domRepresentation, String tag, String idString, XmlPullParser xpp, List<? extends DomRepresentation<?>> other) {
        domRepresentation.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);
            final String attributeName = xpp.getAttributeName(i);

            if (idString.equals(attributeName)) {
                domRepresentation.setName(attributeValue);
                continue;
            }

            domRepresentation.apply(attributeName, attributeValue);
        }
        domRepresentation.end();

        if (other != null) {
            if (domRepresentation.getName() == null) {
                int num = 0;
                boolean eq = false;
                while (true) {
                    for (DomRepresentation<?> aDomList : other) {
                        if (aDomList.getName().equals(tag + num)) {
                            eq = true;
                            break;
                        }
                    }
                    if (eq) {
                        num++;
                        eq = false;
                        continue;
                    }
                    break;
                }
                domRepresentation.setName(tag + num);
            }
        }
        else {
            domRepresentation.setName(String.valueOf(domRepresentation.hashCode()));
        }
    }

}
