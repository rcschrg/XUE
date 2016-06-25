package de.verygame.xue.handler;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Globals;
import de.verygame.xue.exception.*;
import de.verygame.xue.handler.dom.DomElement;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.tag.XueContainerTag;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.DomUtils;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;

/**
 * @author Rico Schrage
 */
public class ElementsTagGroupHandler<T> extends BaseTagGroupHandler<T, DomElement<T>> {

    @Dependency
    protected ConstantTagGroupHandler constantTagHandler;

    /** stack of elements to determine current scope */
    private final Deque<XueContainerTag<T>> scopeStack;

    /** global mappings */
    private final GlobalMappings<T> globalMappings;

    private final Comparator<DomElement<T>> domElementComparator = new Comparator<DomElement<T>>() {
        @Override
        public int compare(DomElement<T> o1, DomElement<T> o2) {
            return o1.getLayer()-o2.getLayer();
        }
    };

    public ElementsTagGroupHandler(GlobalMappings<T> globalMappings) {
        super(Globals.ELEMENT_TAG);

        this.globalMappings = globalMappings;
        this.scopeStack = new ArrayDeque<>();
    }

    @Override
    public void handle(XmlPullParser xpp) throws XueSyntaxException, TagUnknownException, AttributeUnknownException {
        final String tag = xpp.getName();
        XueTag<T> elementBuilder = null;
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
        if (elementBuilder instanceof XueContainerTag) {
            // it is guaranteed because of the type of 'mapping'
            //noinspection unchecked
            scopeStack.push((XueContainerTag<T>) elementBuilder);
        }

        DomElement<T> domElement = new DomElement<>(elementBuilder, globalMappings, constantTagHandler.getDom());
        domElement.setLayer(scopeStack.size());
        DomUtils.applyTagToDom(domElement, xpp);

        if (domElement.getName() == null) {
            throw new XueException("The name attribute is missing!");
        }

        domList.add(domElement);
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws XueException {
        Collections.sort(domList, domElementComparator);
    }

}
