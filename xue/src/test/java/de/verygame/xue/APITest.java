package de.verygame.xue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.mapping.tag.attribute.AbstractAttribute;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;

import static org.junit.Assert.assertEquals;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class APITest {

    private static class TestBase {
        protected String name;
        protected int value;
    }

    private static class TestBaseName extends AbstractAttribute<TestBase, String> {
        private static final String NAME = "testName";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(TestBase element, String value) {
            element.name = value;
        }
    }

    private static class TestBaseValue extends AbstractAttribute<TestBase, Integer> {
        private static final String NAME = "value";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(TestBase element, Integer value) {
            element.value = value;
        }
    }

    private static class TestBaseTag extends XueAbstractElementTag<TestBase> {

        public TestBaseTag() {
            super(new TestBase());
        }

        @Override
        protected List<Attribute<TestBase, ?>> defineAttributes() {
            return buildAttributeList(new TestBaseName(), new TestBaseValue());
        }

        @Override
        protected List<AttributeGroup<TestBase>> defineAttributeGroups() {
            return buildAttributeGroupList();
        }
    }

    BuilderMapping<TestBase> testBaseBuilderMapping = new BuilderMapping<TestBase>() {
        @Override
        public XueTag<? extends TestBase> createBuilder(String name) {
            return new TestBaseTag();
        }
    };

    @Test
    public void testSimpleUsage() throws Exception{
        Xue<TestBase> xue = new Xue<>(getClass().getResourceAsStream("/simple.xml"));
        xue.addElementMapping(testBaseBuilderMapping);

        xue.load();

        assertEquals("Hallo", xue.getElementByName("testOne").name);
        assertEquals(42, xue.getElementByName("testOne").value);
    }

}
