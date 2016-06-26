package de.verygame.xue.util;

import de.verygame.xue.handler.dom.DomRepresentation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kxml2.io.KXmlParser;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class DomUtilsTest {

    @Mock
    DomRepresentation<Object> domRepresentation;
    @Mock
    DomRepresentation<Object> secondDom;

    @Before
    public void setUp() throws Exception {
        when(domRepresentation.getName()).thenReturn("a");
        when(secondDom.getName()).thenReturn("b");
    }

    @Test
    public void searchFor() throws Exception {
        //given
        List<DomRepresentation<Object>> testList = new ArrayList<>();
        testList.add(secondDom);
        testList.add(domRepresentation);

        //when
        DomRepresentation<Object> result = DomUtils.searchFor(testList, "a");

        //then
        assertEquals(result, domRepresentation);
    }

    @Test
    public void applyTagToDom() throws Exception {
        //given
        XmlPullParser xmlPullParser = new KXmlParser();
        xmlPullParser.setInput(new ByteArrayInputStream("<a name=\"d\" c=\"a\"></a>".getBytes()), "UTF-8");
        xmlPullParser.next();

        //when
        DomUtils.applyTagToDom(domRepresentation, xmlPullParser);

        //then
        verify(domRepresentation).apply("c", "a");
        verify(domRepresentation).setName("d");
    }

}