package de.verygame.xue.handler;

import de.verygame.xue.mapping.GlobalMappings;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.exception.XueSyntaxException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.handler.annotation.DependencyHandler;
import de.verygame.xue.mapping.builder.ContainerBuilder;
import de.verygame.xue.mapping.builder.GLMenuBuilder;
import de.verygame.xue.handler.dom.DomElement;

/**
 * @author Rico Schrage
 */
public class ElementsTagHandler<T> extends BaseTagHandler<T, DomElement<T>> {

    @DependencyHandler
    protected ConstantTagHandler constantTagHandler;

    /** stack of elements to determine current scope */
    private final Deque<ContainerBuilder<T>> scopeStack;

    /** global mappings */
    private final GlobalMappings<T> globalMappings;

    private final List<DomElement<T>> internalDomModel;

    private final Comparator<DomElement<T>> domElementComparator = new Comparator<DomElement<T>>() {
        @Override
        public int compare(DomElement<T> o1, DomElement<T> o2) {
            return o1.getLayer()-o2.getLayer();
        }
    };

    public ElementsTagHandler(GlobalMappings<T> globalMappings) {
        super(Globals.ELEMENT_TAG);

        this.globalMappings = globalMappings;
        this.scopeStack = new ArrayDeque<>();
        this.internalDomModel = new ArrayList<>();
    }

    public void onResizeEvent() {
        for (int i = 0; i < internalDomModel.size(); ++i) {
            DomElement<T> domElement = internalDomModel.get(i);
            try {
                domElement.resize();
            }
            catch (AttributeUnknownException e) {
                //TODO LOGGER
            }
        }
    }

    @Override
    public void handle(XmlPullParser xpp) throws XueSyntaxException, TagUnknownException, AttributeUnknownException {
        final String tag = xpp.getName();
        GLMenuBuilder<T> elementBuilder = null;
        for (BuilderMapping<T> m : mapping) {
            elementBuilder = m.createBuilder(xpp.getName());
            if (elementBuilder != null) {
                break;
            }
        }

        if (elementBuilder == null) {
            throw new ElementTagUnknownException(tag);
        }

        if (xpp.getDepth()-1 == scopeStack.size()+1 && !scopeStack.isEmpty()) {
            scopeStack.pop();
        }

        if (xpp.getDepth()-1 > scopeStack.size()+1 && !scopeStack.isEmpty()) {
            scopeStack.peek().applyChild(elementBuilder.getElement());
        }

        if (elementBuilder instanceof ContainerBuilder) {
            // it guaranteed because of the type of 'mapping'
            //noinspection unchecked
            scopeStack.push((ContainerBuilder<T>) elementBuilder);
        }

        String name = null;
        DomElement<T> domElement = new DomElement<>(elementBuilder, globalMappings, constantTagHandler.getResultMap());
        domElement.setLayer(scopeStack.size());
        domElement.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {

            final String attributeValue = xpp.getAttributeValue(i);
            final String attributeName = xpp.getAttributeName(i);

            if (CoreAttribute.ELEMENT_ID.equals(attributeName)) {
                name = attributeValue;
                continue;
            }

            domElement.apply(attributeName, attributeValue);
        }
        domElement.end();

        if (name != null) {
            domMap.put(name, domElement);
            internalDomModel.add(domElement);
            resultMap.put(name, elementBuilder.getElement());
        }
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws XueException {
        Collections.sort(internalDomModel, domElementComparator);
    }

}
