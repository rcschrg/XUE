package de.verygame.xue.mapping.tag;

import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.mapping.tag.attribute.AttributeGroupElementMeta;

import java.util.*;

/**
 * @author Rico Schrage
 */
public abstract class XueAbstractElementTag<T> implements XueTag<T> {
    protected T element;
    protected List<Attribute<? super T, ?>> attributes;
    protected List<AttributeGroup<? super T>> attributeGroups;
    protected List<XueTag<?>> childTagList;
    protected Map<AttributeGroup<? super T>, Map<String, Object>> multiValueMap;
    private boolean first = true;

    public XueAbstractElementTag(T element) {
        this.element = element;
        this.multiValueMap = new HashMap<>();
        this.childTagList = new ArrayList<>();
    }

    protected abstract List<Attribute<? super T, ?>> defineAttributes();

    protected abstract List<AttributeGroup<? super T>> defineAttributeGroups();

    protected void addChildTag(XueTag<?> childTag) {
        childTagList.add(childTag);
    }

    @SafeVarargs
    protected final List<Attribute<? super T, ?>> buildAttributeList(Attribute<? super T, ?>... attributes) {
        List<Attribute<? super T, ?>> attributeList = new ArrayList<>(attributes.length);
        Collections.addAll(attributeList, attributes);
        return attributeList;
    }

    @SafeVarargs
    protected final List<AttributeGroup<? super T>> buildAttributeGroupList(AttributeGroup<? super T>... attributes) {
        List<AttributeGroup<? super T>> attributeList = new ArrayList<>(attributes.length);
        Collections.addAll(attributeList, attributes);
        return attributeList;
    }

    @Override
    public void preBuild() {
        if (first) {
            attributes = defineAttributes();
            attributeGroups = defineAttributeGroups();
            first = false;
        }
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.begin(element);
        }
        for (int i = 0; i < childTagList.size(); ++i) {
            childTagList.get(i).preBuild();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void apply(String attribute, V value) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            if (a.getName().equals(attribute)) {
                Attribute<T, V> a2 = (Attribute<T, V>) a;
                a2.apply(element, value);
                return;
            }
        }
        for (int c = 0;  c < childTagList.size(); ++c) {
            childTagList.get(c).apply(attribute, value);
        }
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<? super T> a = attributeGroups.get(i);
            for (int j = 0; j < a.getGroupMeta().size(); ++j) {
                AttributeGroupElementMeta meta = a.getGroupMeta().get(i);
                if (meta.getName().equals(attribute)) {
                    if (!multiValueMap.containsKey(a)) {
                        multiValueMap.put(a, new HashMap<String, Object>());
                    }
                    multiValueMap.get(a).put(attribute, value);
                    return;
                }
            }
        }
    }

    @Override
    public void applyChild(Object child) {
        //default: nothing to do
    }

    @Override
    public void postBuild() {
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<? super T> a = attributeGroups.get(i);
            for (int j = 0; j < a.getGroupMeta().size(); ++j) {
                AttributeGroupElementMeta meta = a.getGroupMeta().get(i);
                for (Map.Entry<AttributeGroup<? super T>, Map<String, Object>> entry : multiValueMap.entrySet()) {
                    for (Map.Entry<String, Object> valueEntry : entry.getValue().entrySet()) {
                        if (valueEntry.getKey().equals(meta.getName())) {
                            a.apply(element, valueEntry.getValue(), valueEntry.getKey());
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.end(element);
        }
        for (int i = 0; i < childTagList.size(); ++i) {
            childTagList.get(i).postBuild();
        }
    }

    @Override
    public void onInputEvent(XueInputEvent inputEvent) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.onInputEvent(inputEvent);
        }
    }

    @Override
    public void onUpdate(float delta) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.onUpdate(delta);
        }
    }

    @Override
    public T getElement() {
        return element;
    }

}
