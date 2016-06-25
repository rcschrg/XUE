package de.verygame.xue.mapping.tag;

import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.mapping.tag.attribute.AttributeGroupElementMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 */
public abstract class XueAbstractElementTag<T> implements XueTag<T> {
    protected T element;
    protected List<Attribute<T, ?>> attributes;
    protected List<AttributeGroup<T>> attributeGroups;
    protected Map<AttributeGroup<T>, Map<String, Object>> multiValueMap;

    public XueAbstractElementTag(T element) {
        this.element = element;
        this.multiValueMap = new HashMap<>();
        this.attributes = defineAttributes();
        this.attributeGroups = defineAttributeGroups();
    }

    protected abstract List<Attribute<T, ?>> defineAttributes();
    protected abstract List<AttributeGroup<T>> defineAttributeGroups();

    @Override
    public void preBuild() {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<T, ?> a = attributes.get(i);
            a.begin(element);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void apply(String attribute, V value) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<T, ?> a = attributes.get(i);
            if (a.getName().equals(attribute)) {
                Attribute<T, V> a2 = (Attribute<T, V>) a;
                a2.apply(element, value);
                return;
            }
        }
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<T> a = attributeGroups.get(i);
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
    public void postBuild() {
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<T> a = attributeGroups.get(i);
            for (int j = 0; j < a.getGroupMeta().size(); ++j) {
                AttributeGroupElementMeta meta = a.getGroupMeta().get(i);
                for (Map.Entry<AttributeGroup<T>, Map<String, Object>> entry : multiValueMap.entrySet()) {
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
            Attribute<T, ?> a = attributes.get(i);
            a.end(element);
        }
    }

    @Override
    public void onInputEvent(XueInputEvent inputEvent) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<T, ?> a = attributes.get(i);
            a.onInputEvent(inputEvent);
        }
    }

    @Override
    public void onUpdate(float delta) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<T, ?> a = attributes.get(i);
            a.onUpdate(delta);
        }
    }

    @Override
    public T getElement() {
        return element;
    }

}
