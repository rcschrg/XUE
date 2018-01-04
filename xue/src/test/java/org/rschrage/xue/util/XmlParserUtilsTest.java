package org.rschrage.xue.util;

import org.junit.Before;
import org.junit.Test;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Rico Schrage
 */
public class XmlParserUtilsTest {

    XmlPullParser xmlPullParser;

    @Before
    public void setUp() throws Exception {
        xmlPullParser = new KXmlParser();
        xmlPullParser.setInput(new ByteArrayInputStream("<a b=\"d\" c=\"a\"></a>".getBytes()), "UTF-8");
        xmlPullParser.next();
    }

    @Test
    public void findValueOf() throws Exception {
        String value = XmlParserUtils.findValueOf(xmlPullParser, "b");
        assertEquals("d", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findValueOfException() {
        XmlParserUtils.findValueOf(xmlPullParser, "g");
    }

}