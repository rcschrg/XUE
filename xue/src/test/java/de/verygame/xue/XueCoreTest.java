package de.verygame.xue;

import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.tag.XueTag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class XueCoreTest {

    public static final String exampleXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<menu>\n" +
            "    <constants>\n" +
            "        <const name=\"ELEMENT_LAYER\" value=\"2\" />\n" +
            "        <const name=\"PANEL_LAYER\" value=\"1\" />\n" +
            "        <const name=\"BACKGROUND_LAYER\" value=\"0\" />\n" +
            "    </constants>\n" +
            "    <elements>\n" +
            "        <text name=\"title\" x=\"50r\" y=\"90r\" text=\"@string/app_name\" font=\"typeoneTitle\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "        <text name=\"subTitle\" x=\"30r\" y=\"84r\" text=\"@string/options_title\" font=\"typeoneSubTitle\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "\n" +
            "        <panel name=\"mainPanel\" x=\"50r\" y=\"45r\" width=\"78r\" height=\"60r\" color=\"$MAIN_PANEL_COLOR\" >\n" +
            "\n" +
            "            <text name=\"soundText\" x=\"75r\" y=\"85r\" text=\"@string/options_sound\" font=\"quicksand\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "            <switch name=\"soundSwitch\" x=\"25r\" y=\"85r\" id=\"0\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "\n" +
            "            <text name=\"musicText\" x=\"25r\" y=\"70r\" text=\"@string/options_music\" font=\"quicksand\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "            <switch name=\"musicSwitch\" x=\"75r\" y=\"70r\" id=\"1\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "\n" +
            "            <text name=\"lowText\" x=\"75r\" y=\"55r\" text=\"@string/options_low\" font=\"quicksand\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "            <switch name=\"lowSwitch\" x=\"25r\" y=\"55r\" id=\"2\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "\n" +
            "            <text name=\"sensitivText\" x=\"50r\" y=\"30r\" text=\"@string/options_sensitivity\" font=\"quicksand\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "            <toucharea name=\"sensitivBarTouchArea\" x=\"50r\" y=\"40r\" width=\"70r\" height=\"10r\">\n" +
            "                <seekbar name=\"sensitivBar\" x=\"50r\" y=\"50r\" width=\"100r\" height=\"10r\" id=\"3\" zIndex=\"$ELEMENT_LAYER\" />\n" +
            "            </toucharea>\n" +
            "\n" +
            "        </panel>\n" +
            "    </elements>\n" +
            "</menu>";

    @Mock
    private BuilderMapping<Object> aM;
    @Mock
    private BuilderMapping<Object> cM;
    @Mock
    private XueTag gb;

    private XueCore<Object> core;
    private InputStream inputStream;

    @Before
    public void prepare() throws ElementTagUnknownException, XmlPullParserException {
        when(aM.createBuilder(anyString())).thenReturn(gb);
        when(cM.createBuilder(anyString())).thenReturn(gb);
        core = new XueCore<>();
        core.addElementMapping(aM);
        core.addConstantMapping(cM);
        inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testForCorrectParsing() throws XueException, XmlPullParserException, IOException {
        core.load(inputStream);
        assertTrue("Size is " + core.getConstMap().size() + " instead of the expected 3", core.getConstMap().size() == 3);
        assertTrue("Size is " + core.getElementMap().size() + " instead of the expected 12", core.getElementMap().size() == 12);
    }

}