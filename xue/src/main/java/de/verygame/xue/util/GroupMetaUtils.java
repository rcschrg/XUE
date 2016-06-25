package de.verygame.xue.util;

import de.verygame.xue.mapping.tag.attribute.AttributeGroupElementMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class GroupMetaUtils {

    public static List<AttributeGroupElementMeta> buildMetaList(String[] names, Class<?>[] valueTypes) {
        List<AttributeGroupElementMeta> metaList = new ArrayList<>();
        if (names.length != valueTypes.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < names.length; ++i) {
            String name = names[i];
            Class<?> type = valueTypes[i];
            metaList.add(new AttributeGroupElementMeta(name, type));
        }
        return metaList;
    }

}
