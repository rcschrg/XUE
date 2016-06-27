package de.verygame.xue.handler;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Globals;
import de.verygame.xue.exception.*;
import de.verygame.xue.handler.dom.DomElement;
import de.verygame.xue.handler.dom.DomObject;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.DomUtils;
import org.xmlpull.v1.XmlPullParser;

import java.util.*;

/**
 * @author Rico Schrage
 */
public class ElementsTagGroupHandler<T> extends BaseTagGroupHandler<T, DomElement<? extends T>> {

    @Dependency
    protected ConstantTagGroupHandler constantTagHandler;

    /** stack of elements to determine current scope */
    private final Deque<XueTag<?>> scopeStack;

    /** global mappings */
    private final GlobalMappings<T> globalMappings;

    private final List<BuilderMapping<Object>> childMapping;

    private final Comparator<DomElement<? extends T>> domElementComparator = new Comparator<DomElement<? extends T>>() {
        @Override
        public int compare(DomElement<? extends T> o1, DomElement<? extends T> o2) {
            return o1.getLayer()-o2.getLayer();
        }
    };

    public ElementsTagGroupHandler() {
        this(new DummyGlobalMappings<T>());
    }

    public ElementsTagGroupHandler(GlobalMappings<T> globalMappings) {
        super(Globals.ELEMENT_TAG);

        this.childMapping = new ArrayList<>();
        this.globalMappings = globalMappings;
        this.scopeStack = new ArrayDeque<>();
    }

    private void updateScope(XmlPullParser xpp, XueTag<?> currentTag) {
        if (xpp.getDepth()-1 == scopeStack.size()+1 && !scopeStack.isEmpty()) {
            scopeStack.pop();
        }
        if (xpp.getDepth()-1 > scopeStack.size()+1 && !scopeStack.isEmpty()) {
            scopeStack.peek().applyChild(currentTag.getElement());
        }
        scopeStack.push(currentTag);
    }

    private void handleChildObject(XmlPullParser xpp) {
        final String tag = xpp.getName();
        XueTag<?> childBuilder = null;
        for (BuilderMapping<?> m : childMapping) {
            childBuilder = m.createBuilder(tag);
            if (childBuilder != null) {
                break;
            }
        }

        if (childBuilder == null) {
            throw new ElementTagUnknownException(tag);
        }
        updateScope(xpp, childBuilder);

        DomObject<Object> childDom = new DomObject<>(childBuilder);
        DomUtils.applyTagToDom(childDom, xpp);
    }

    public GlobalMappings<T> getGlobalMappings() {
        return globalMappings;
    }

    @Override
    public void handle(XmlPullParser xpp) {
        final String tag = xpp.getName();
        XueTag<? extends T> elementBuilder = null;
        for (BuilderMapping<? extends T> m : mapping) {
            elementBuilder = m.createBuilder(tag);
            if (elementBuilder != null) {
                break;
            }
        }

        if (elementBuilder == null) {
            handleChildObject(xpp);
            return;
        }
        updateScope(xpp, elementBuilder);

        DomElement<? extends T> domElement = new DomElement<>(elementBuilder, globalMappings, constantTagHandler.getDom());
        domElement.setLayer(scopeStack.size());
        DomUtils.applyTagToDom(domElement, xpp);

        if (domElement.getName() == null) {
            throw new XueException("The name attribute is missing!");
        }

        domList.add(domElement);
    }

    @Override
    public void stopHandle(XmlPullParser xpp) {
        Collections.sort(domList, domElementComparator);
    }

}
