package de.verygame.xue.mapping.tag;

import de.verygame.util.ReflectionUtils;
import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.annotation.Name;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.tag.attribute.Attribute;
import de.verygame.xue.mapping.tag.attribute.AttributeGroup;
import de.verygame.xue.mapping.tag.attribute.AttributeGroupElementMeta;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Rico Schrage
 */
public abstract class XueAbstractElementTag<T> implements XueTag<T> {
    protected T element;
    protected List<Attribute<? super T, ?>> attributes;
    protected List<AttributeGroup<? super T>> attributeGroups;
    protected List<XueTag<?>> xueTagList;
    protected Map<AttributeGroup<? super T>, Map<String, Object>> multiValueMap;
    private boolean first = true;

    public XueAbstractElementTag(T element) {
        this.element = element;
        this.multiValueMap = new HashMap<>();
        this.xueTagList = new ArrayList<>();
    }

    private String fetchName(Attribute<?, ?> attribute) {
        if (attribute.getClass().isAnnotationPresent(Name.class)) {
            return attribute.getClass().getAnnotation(Name.class).value();
        }
        return attribute.getName();
    }

    protected abstract List<Attribute<? super T, ?>> defineAttributes();

    protected abstract List<AttributeGroup<? super T>> defineAttributeGroups();

    protected void linkXueTag(XueTag<?> childTag) {
        xueTagList.add(childTag);
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
            sortAttributes();
            injectDependencies();
            first = false;
        }
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.begin(element);
        }
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<? super T> a = attributeGroups.get(i);
            a.begin(element);
        }
        for (int i = 0; i < xueTagList.size(); ++i) {
            xueTagList.get(i).preBuild();
        }
    }

    private void sortAttributes() {
        List<Attribute<? super T, ?>> attributeCopy = new ArrayList<>(attributes);
        attributes.clear();
        while (!attributeCopy.isEmpty()) {
            Iterator<Attribute<? super T, ?>> iterator = attributeCopy.iterator();
            while (iterator.hasNext()) {
                Attribute<? super T, ?> attribute = iterator.next();
                int dependencies = 0;
                Field[] f = attribute.getClass().getDeclaredFields();
                for (Field field : f) {
                    if (field.isAnnotationPresent(Dependency.class) && isAttributePresent(field.getName(), attributeCopy)) {
                        dependencies++;
                    }
                }
                if (dependencies == 0) {
                    attributes.add(attribute);
                    iterator.remove();
                }
            }
        }
    }

    private boolean isAttributePresent(String name, List<Attribute<? super T, ?>> attributeCopy) {
        for (Attribute<? super T, ?> att : attributeCopy) {
            if (att.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void injectDependencies() {
        for (Attribute<? super T, ?> attribute : attributes) {
            List<Field> f = ReflectionUtils.getAllFields(attribute.getClass());
            for (Field field : f) {
                if (!field.isAnnotationPresent(Dependency.class)) {
                    continue;
                }
                String fieldName = field.getName();
                for (Attribute<? super T, ?> otherAttribute : attributes) {
                    if (fieldName.equals(otherAttribute.getName())) {
                        try {
                            field.setAccessible(true);
                            field.set(attribute, otherAttribute);
                        }
                        catch (IllegalAccessException e) {
                            throw new XueException(e);
                        }
                    }
                }
            }

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void apply(String attribute, V value) {
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            if (fetchName(a).equals(attribute)) {
                Attribute<T, V> a2 = (Attribute<T, V>) a;
                a2.apply(element, value);
		        applyToLinkedXueTags(attribute, value);
                return;
            }
        }
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<? super T> a = attributeGroups.get(i);
            for (int j = 0; j < a.getGroupMeta().size(); ++j) {
                AttributeGroupElementMeta meta = a.getGroupMeta().get(j);
                if (meta.getName().equals(attribute)) {
                    if (!multiValueMap.containsKey(a)) {
                        multiValueMap.put(a, new HashMap<String, Object>());
                    }
                    multiValueMap.get(a).put(attribute, value);
		            applyToLinkedXueTags(attribute, value);
                    return;
                }
            }
        }
	    applyToLinkedXueTags(attribute, value);
    }

    public <V> void applyToLinkedXueTags(String attribute, V value) {
        for (int c = 0;  c < xueTagList.size(); ++c) {
            xueTagList.get(c).apply(attribute, value);
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
            Map<String, Object> entry = multiValueMap.get(a);
            if (entry == null) {
                continue;
            }
            for (Map.Entry<String,Object> valueEntry : entry.entrySet()) {
                for (int j = 0; j < a.getGroupMeta().size(); ++j) {
                    AttributeGroupElementMeta meta = a.getGroupMeta().get(j);
                    if (valueEntry.getKey().equals(meta.getName())) {
                        a.apply(element, valueEntry.getValue(), valueEntry.getKey());
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < attributes.size(); ++i) {
            Attribute<? super T, ?> a = attributes.get(i);
            a.end(element);
        }
        for (int i = 0; i < attributeGroups.size(); ++i) {
            AttributeGroup<? super T> a = attributeGroups.get(i);
            a.end(element);
        }
        for (int i = 0; i < xueTagList.size(); ++i) {
            xueTagList.get(i).postBuild();
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
