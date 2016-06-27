package de.verygame.xue.handler;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.CoreAttribute;
import de.verygame.xue.constants.Globals;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.dom.DomObject;
import de.verygame.xue.handler.dom.DomRepresentation;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.mapping.tag.XueTag;
import de.verygame.xue.tag.BasicActionTag;
import de.verygame.xue.util.DomUtils;
import de.verygame.xue.util.XmlParserUtils;
import de.verygame.xue.util.action.Action;
import de.verygame.xue.util.action.ActionSequence;
import org.xmlpull.v1.XmlPullParser;

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

    public ActionSequenceTagGroupHandler() {
        super(Globals.AS_TAG);

        actionSequenceMap = new HashMap<>();
        addBuilderMapping(new BuilderMapping<Action>() {
            @Override
            public XueTag<Action> createBuilder(String name) {
                if ("basicAction".equals(name)) {
                    //noinspection unchecked
                    return (XueTag<Action>) (XueTag<? extends Action>) new BasicActionTag();
                }
                return null;
            }
        });
    }

    public Map<String, ActionSequence> getActionSequenceMap() {
        return actionSequenceMap;
    }

    @Override
    public void startHandle(XmlPullParser xpp) throws XueException {
        for (int i = 0; i  < xpp.getAttributeCount(); ++i) {
            if (xpp.getAttributeName(i).equals(CoreAttribute.ELEMENT_ID)) {
                currentActionSequenceName = xpp.getAttributeValue(i);
            }
        }
        if (currentActionSequenceName == null) {
            throw new XueException(xpp.getLineNumber() + " You have to specify a name!");
        }
        currentActionSequence = new ActionSequence();
        currentActionSequence.setStartEvent(XueInputEvent.valueOf(xpp.getAttributeValue(null, "startOn")));
    }

    @Override
    public void handle(XmlPullParser xpp) throws XueException {
        String tagName = xpp.getName();

        XueTag<Action> actionBuilder = null;
        for (BuilderMapping<Action> m : mapping) {
            actionBuilder = m.createBuilder(tagName);
        }
        if (actionBuilder == null) {
            throw new ElementTagUnknownException(tagName);
        }

        DomRepresentation<Action> domObject = new DomObject<Action>(actionBuilder);

        String nameId = XmlParserUtils.findValueOf(xpp, CoreAttribute.ELEMENT_ID);
        String targetId = XmlParserUtils.findValueOf(xpp, CoreAttribute.ACTION_TARGET_ID);
        if (nameId == null || targetId == null) {
            throw new XueException(xpp.getLineNumber() + ": You have to specify a name and a target id!");
        }
        actionBuilder.apply(CoreAttribute.ACTION_TARGET_ID, DomUtils.searchFor(tagHandler.getDom(), targetId).getTag());

        domObject.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            String name = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);

            if (name.equals(CoreAttribute.ELEMENT_ID) || name.equals(CoreAttribute.ACTION_TARGET_ID)) {
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
    public void stopHandle(XmlPullParser xpp) {
        actionSequenceMap.put(currentActionSequenceName, currentActionSequence);

        currentActionSequence = null;
        currentActionSequenceName = null;
    }

}
