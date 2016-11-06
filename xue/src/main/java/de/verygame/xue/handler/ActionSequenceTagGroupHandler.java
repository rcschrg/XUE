package de.verygame.xue.handler;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Constant;
import de.verygame.xue.dom.DomObject;
import de.verygame.xue.dom.DomRepresentation;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.tag.BasicActionTag;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.util.DomUtils;
import de.verygame.xue.util.XmlParserUtils;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.ActionSequence;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rico Schrage
 */
public class ActionSequenceTagGroupHandler extends BaseTagGroupHandler<Action, DomRepresentation<Action>> {

    @Dependency
    private ElementsTagGroupHandler<?> tagHandler;

    private ActionSequence currentActionSequence;
    private String currentActionSequenceName;

    private Map<String, ActionSequence> actionSequenceMap;

    public ActionSequenceTagGroupHandler(Map<Constant, String> constantStringMap) {
        super(constantStringMap, Constant.AS_TAG);

        actionSequenceMap = new HashMap<>();
        addBuilderMapping(new TagMapping<Action>() {
            @Override
            public XueTag<? extends Action> createTag(String name) {
                if ("basicAction".equals(name)) {
                    //noinspection unchecked
                    return new BasicActionTag();
                }
                return null;
            }
        });
    }

    public Map<String, ActionSequence> getActionSequenceMap() {
        return actionSequenceMap;
    }

    private void handleRootTag(XmlPullParser xpp) throws XmlPullParserException {
        if (currentActionSequence != null) {
            finishCurrentTag();
        }

        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            if (xpp.getAttributeName(i).equals(constantMap.get(Constant.ELEMENT_ID))) {
                currentActionSequenceName = xpp.getAttributeValue(i);
            }
        }
        if (currentActionSequenceName == null) {
            throw new XueException(xpp.getLineNumber() + " You have to specify a name!");
        }
        currentActionSequence = new ActionSequence();
        currentActionSequence.setStartEvent(XueInputEvent.valueOf(xpp.getAttributeValue(null, "startOn")));
    }

    private void finishCurrentTag() {
        actionSequenceMap.put(currentActionSequenceName, currentActionSequence);

        currentActionSequence = null;
        currentActionSequenceName = null;
    }

    @Override
    public void handle(XmlPullParser xpp) throws XmlPullParserException {
        if (xpp.getName().equals(Constant.AS_SUB_TAG.toString())) {
            handleRootTag(xpp);
            return;
        }

        String tagName = xpp.getName();

        XueTag<? extends Action> actionBuilder = null;
        for (TagMapping<? extends Action> m : mapping) {
            actionBuilder = m.createTag(tagName);
        }
        if (actionBuilder == null) {
            throw new ElementTagUnknownException(tagName);
        }

        DomRepresentation<Action> domObject = new DomObject<Action>(constantMap, actionBuilder);

        String nameId = XmlParserUtils.findValueOf(xpp, constantMap.get(Constant.ELEMENT_ID));
        String targetId = XmlParserUtils.findValueOf(xpp, constantMap.get(Constant.ACTION_TARGET_ID));
        if (nameId == null || targetId == null) {
            throw new XueException(xpp.getLineNumber() + ": You have to specify a name and a target id!");
        }

        domObject.begin();
        actionBuilder.apply(constantMap.get(Constant.ACTION_TARGET_ID), DomUtils.searchFor(tagHandler.getDom(), targetId).getTag());
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            String name = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);

            if (name.equals(constantMap.get(Constant.ELEMENT_ID)) || name.equals(constantMap.get(Constant.ACTION_TARGET_ID))) {
                continue;
            }
            domObject.apply(name, value);
        }
        domObject.setName(nameId);
        domObject.end();

        currentActionSequence.addAction(actionBuilder.getElement());

        domList.add(domObject);
    }

    @Override
    public void stopHandle(XmlPullParser xpp) throws XueException, XmlPullParserException {
        finishCurrentTag();
    }
}
