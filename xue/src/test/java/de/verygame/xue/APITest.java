package de.verygame.xue;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.annotation.Name;
import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.mapping.tag.XueAbstractElementTag;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.mapping.tag.attribute.AbstractAttribute;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Rico Schrage
 */
@RunWith(MockitoJUnitRunner.class)
public class APITest {

    private static class TestBase {
        protected String name;
        protected int value;
        protected TestChild child = new TestChild();
    }

    private static class TestChild {
        protected float a;
        protected float b;
    }

    private static class TestBaseA extends AbstractAttribute<TestChild, Float> {
        private static final String NAME = "a";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(TestChild element, Float value) {
            element.a = value;
        }
    }

    private static class TestBaseB extends AbstractAttribute<TestChild, Float> {
        private static final String NAME = "b";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(TestChild element, Float value) {
            element.b = value;
        }
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
        private int v = 0;

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void apply(TestBase element, Integer value) {
            this.v = value;
            element.value = value;
        }

        protected int getValue() { return v; }
    }

    /**
     * Testing new APIs
     */
    @Name("valueChild")
    private static class TestBaseValueChild extends AbstractAttribute<TestBase, Integer> {

        @Dependency
        private TestBaseValue value;

        @Override
        public void apply(TestBase element, Integer value) {
            element.value = value * this.value.getValue();
        }
    }

    private static class TestChildTag extends XueAbstractElementTag<TestChild> {

        public TestChildTag() {
            this(new TestChild());
        }

        public TestChildTag(TestChild testChild) {
            super(testChild);
        }

        @Override
        protected List<Attribute<? super TestChild, ?>> defineAttributes() {
            return buildAttributeList(new TestBaseA(), new TestBaseB());
        }

        @Override
        protected List<AttributeGroup<? super TestChild>> defineAttributeGroups() {
            return buildAttributeGroupList();
        }
    }

    private static class TestBaseTag extends XueAbstractElementTag<TestBase> {

        public TestBaseTag() {
            super(new TestBase());

            linkXueTag(new TestChildTag(getElement().child));
        }

        @Override
        protected List<Attribute<? super TestBase, ?>> defineAttributes() {
            return buildAttributeList(new TestBaseName(), new TestBaseValue(), new TestBaseValueChild());
        }

        @Override
        public void applyChild(Object child) {
            System.out.print(this.element.name + ":" + ((TestBase)child).name + " ");
            super.applyChild(child);
        }

        @Override
        protected List<AttributeGroup<? super TestBase>> defineAttributeGroups() {
            return buildAttributeGroupList();
        }
    }

    TagMapping<TestBase> testBaseTagMapping = new TagMapping<TestBase>() {
        @Override
        public XueTag<? extends TestBase> createTag(String name) {
            return new TestBaseTag();
        }
    };

    @Test
    public void testSimpleUsage() throws Exception{
        BasicXue<TestBase> xue = new BasicXue<>();
        xue.addFile(getClass().getResourceAsStream("/simple.xml"), "simple.xml");
        xue.addMappingUnsafe(ElementsTagGroupHandler.class, testBaseTagMapping);

        xue.load();

        assertEquals("Hallo", xue.getElementByName("testOne").name);
        assertEquals(42, xue.getElementByName("testOne").value);
        assertEquals(84, xue.getElementByName("testTwo").value);
        assertEquals(0.0f, xue.getElementByName("testOne").child.a, 0.001f);
        assertEquals(1.0f, xue.getElementByName("testOne").child.b, 0.001f);
        assertEquals(11, xue.getElementsByTagName("BaseTag").size());
    }

}
