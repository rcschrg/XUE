package org.rschrage.xue.util;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Rico Schrage
 */
public class XmlParserUtils {

    private XmlParserUtils() {
        //utility class
    }

    public static String findValueOf(XmlPullParser xpp, String aName) {
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            if (xpp.getAttributeName(i).equals(aName)) {
                return xpp.getAttributeValue(i);
            }
        }
        throw new IllegalArgumentException("The attribute " + aName + " does not exist!");
    }

}
