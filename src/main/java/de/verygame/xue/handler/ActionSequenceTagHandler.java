package de.verygame.xue.handler;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

import de.verygame.xue.handler.dom.DomObject;
import de.verygame.xue.handler.dom.DomRepresentation;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.action.Action;
import de.verygame.xue.handler.action.BasicActionBuilder;
import de.verygame.xue.handler.annotation.DependencyHandler;
import de.verygame.xue.mapping.builder.GLMenuBuilder;

/**
 * @author Rico Schrage
 */
public class ActionSequenceTagHandler extends BaseTagHandler<Action, DomRepresentation<Action>> {

    @DependencyHandler
    private ElementsTagHandler<?> tagHandler;

    private ActionSequence currentActionSequence;
    private String currentActionSequenceName;

    private Map<String, ActionSequence> actionSequenceMap;

    public ActionSequenceTagHandler() {
        super(Globals.AS_TAG);

        actionSequenceMap = new HashMap<>();
        addBuilderMapping(new BuilderMapping<Action>() {
            @Override
            public GLMenuBuilder<Action> createBuilder(String name) {
                if ("basicAction".equals(name)) {
                    return new BasicActionBuilder();
                }
                return null;
            }
        });
    }

    public Map<String, ActionSequence> getActionSequenceMap() {
        return actionSequenceMap;
    }

    public void updateActionSequences(float delta) {
        for (final Map.Entry<String, ActionSequence> entry : actionSequenceMap.entrySet()) {
            entry.getValue().update(delta);
        }
    }

    public void onInputEvent(XueInputEvent inputEvent) {
        for (final Map.Entry<String, ActionSequence> entry : actionSequenceMap.entrySet()) {
            entry.getValue().onInputEvent(inputEvent);
        }
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

        GLMenuBuilder<Action> actionBuilder = null;
        for (BuilderMapping<Action> m : mapping) {
            actionBuilder = m.createBuilder(tagName);
        }
        if (actionBuilder == null) {
            throw new ElementTagUnknownException(tagName);
        }

        DomRepresentation<Action> domObject = new DomObject<>(actionBuilder);

        String nameId = findValueOf(xpp, CoreAttribute.ELEMENT_ID);
        String targetId = findValueOf(xpp, CoreAttribute.ACTION_TARGET_ID);
        if (nameId == null || targetId == null) {
            throw new XueException(xpp.getLineNumber() + ": You have to specify a name and a target id!");
        }
        actionBuilder.applyObject(CoreAttribute.ACTION_TARGET_ID, tagHandler.getDomObjectMap().get(targetId).getBuilder());

        domObject.begin();
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            String name = xpp.getAttributeName(i);
            String value = xpp.getAttributeValue(i);

            if (name.equals(CoreAttribute.ELEMENT_ID) || name.equals(CoreAttribute.ACTION_TARGET_ID)) {
                continue;
            }
            domObject.apply(name, value);
        }
        domObject.end();

        currentActionSequence.addAction(actionBuilder.getElement());

        resultMap.put(nameId, actionBuilder.getElement());
        domMap.put(nameId, domObject);
    }

    @Override
    public void stopHandle(XmlPullParser xpp) {
        actionSequenceMap.put(currentActionSequenceName, currentActionSequence);

        currentActionSequence = null;
        currentActionSequenceName = null;
    }

    private static String findValueOf(XmlPullParser xpp, String aName) {
        String value = null;
        for (int i = 0; i < xpp.getAttributeCount(); ++i) {
            if (xpp.getAttributeName(i).equals(aName)) {
                value = xpp.getAttributeValue(i);
            }
        }
        return value;
    }

}
