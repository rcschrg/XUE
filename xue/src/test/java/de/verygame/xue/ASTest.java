package de.verygame.xue;

import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.mapping.tag.attribute.SimpleGenericAttribute;
import de.verygame.xue.util.action.ActionSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class ASTest {

    public static class Counter {
        float i = 0;
        public void setI(float i) { this.i = i;}
    }

    private static class CounterTag extends XueAbstractElementTag<Counter> {

        public CounterTag(Counter element) {
            super(element);
        }

        @Override
        protected List<Attribute<? super Counter, ?>> defineAttributes() {
            return buildAttributeList(new SimpleGenericAttribute<Counter, Float>("i"));
        }

        @Override
        protected List<AttributeGroup<? super Counter>> defineAttributeGroups() {
            return buildAttributeGroupList();
        }
    }

    @Test
    public void testLoad() {
        GuiXue<Counter> xue  = new GuiXue<Counter>();
        xue.addFile(getClass().getResourceAsStream("/as.xml"), "");
        xue.addMappingUnsafe(ElementsTagGroupHandler.class, new TagMapping<Object>() {
            @Override
            public XueTag<?> createTag(String tagClass) {
                return new CounterTag(new Counter());
            }
        });

        try {
            xue.load();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ActionSequence as = xue.getActionSequenceMap().get("deactivateSequence");
        ActionSequence sas = xue.getActionSequenceMap().get("activateSequence");
        assertTrue(as != null && sas != null);

        xue.onInputEvent(XueInputEvent.ACTIVATE);
        xue.onUpdate(0.5f);
        xue.onUpdate(0.5f);
        xue.onUpdate(0.5f);

        Counter counter1 = xue.getElementByName("test");
        Counter counter2 = xue.getElementByName("test2");

        assertTrue(counter1.i == 11 && counter2.i == 10);
    }
}
