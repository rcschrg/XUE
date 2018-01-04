package org.rschrage.xue.util;

import org.rschrage.xue.mapping.tag.attribute.AttributeGroupElementMeta;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Rico Schrage
 */
public class GroupMetaUtilsTest {

    @Test
    public void buildMetaList() throws Exception {
        List<AttributeGroupElementMeta> list = GroupMetaUtils.buildMetaList(new String[]{"a", "b"}, new Class<?>[]{Object.class, String.class});

        List<AttributeGroupElementMeta> check = new ArrayList<>();
        check.add(new AttributeGroupElementMeta("a", Object.class));
        check.add(new AttributeGroupElementMeta("b", String.class));

        assertEquals(check, list);
    }

}