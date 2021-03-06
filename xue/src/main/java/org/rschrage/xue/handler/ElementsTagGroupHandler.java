package org.rschrage.xue.handler;

import org.rschrage.xue.annotation.Dependency;
import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.dom.DomObject;
import org.rschrage.xue.mapping.TagMapping;
import org.rschrage.xue.mapping.DummyGlobalMappings;
import org.rschrage.xue.mapping.GlobalMappings;
import org.rschrage.xue.mapping.tag.XueTag;
import org.rschrage.xue.util.DomUtils;
import org.rschrage.xue.exception.ElementTagUnknownException;
import org.xmlpull.v1.XmlPullParser;

import java.util.*;

/**
 * @author Rico Schrage
 */
public class ElementsTagGroupHandler<T> extends BaseTagGroupHandler<T, DomObject<T>> {

    @Dependency
    private ConstantTagGroupHandler constantTagHandler;

    private final Deque<XueTag<?>> scopeStack;
    private final GlobalMappings<T> globalMappings;

    private boolean init = false;
    private int initDepth = 1;

    private final Comparator<DomObject<? extends T>> domElementComparator = new Comparator<DomObject<? extends T>>() {
        @Override
        public int compare(DomObject<? extends T> o1, DomObject<? extends T> o2) {
            return o1.getLayer()-o2.getLayer();
        }
    };

    public ElementsTagGroupHandler() {
        this(Constant.obtainDefaultMap(), new DummyGlobalMappings<T>());
    }

    public ElementsTagGroupHandler(Map<Constant, String> constantStringMap) {
        this(constantStringMap, new DummyGlobalMappings<T>());
    }

    public ElementsTagGroupHandler(GlobalMappings<T> globalMappings) {
        this(Constant.obtainDefaultMap(), globalMappings);
    }

    public ElementsTagGroupHandler(Map<Constant, String> constantStringMap, GlobalMappings<T> globalMappings) {
        super(constantStringMap, Constant.ELEMENT_TAG);

        this.globalMappings = globalMappings;
        this.scopeStack = new ArrayDeque<>();
    }

    public GlobalMappings<T> getGlobalMappings() {
        return globalMappings;
    }

    @Override
    public void handle(XmlPullParser xpp) {
        if (!init) {
            initDepth = xpp.getDepth() - 1;
            init = true;
        }

        final String tag = xpp.getName();
        XueTag<? extends T> elementBuilder = null;
        for (TagMapping<? extends T> m : mapping) {
            elementBuilder = m.createTag(tag);
            if (elementBuilder != null) {
                break;
            }
        }
        if (elementBuilder == null) {
            throw new ElementTagUnknownException("Tag " + tag + " is unknown.");
        }
        while (xpp.getDepth()-initDepth <= scopeStack.size() && !scopeStack.isEmpty()) {
            scopeStack.pop();
        }
        XueTag<?> parentTag = scopeStack.peek();

        scopeStack.push(elementBuilder);

        DomObject<T> domElement = new DomObject<>(constantMap, elementBuilder, globalMappings, constantTagHandler.getDom());
        domElement.setLayer(scopeStack.size());
        DomUtils.applyTagToDom(domElement, tag, constantMap.get(Constant.ELEMENT_ID), xpp, domList);

        if (xpp.getDepth()-initDepth >= scopeStack.size() && parentTag != null) {
            parentTag.applyChild(elementBuilder.getElement());
        }

        addToDom(domElement);
    }

    @Override
    public void stopHandle(XmlPullParser xpp) {
        Collections.sort(domList, domElementComparator);
    }

}
