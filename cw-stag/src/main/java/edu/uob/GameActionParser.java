package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class GameActionParser {
    HashMap<String, HashSet<GameAction>> actions = new HashMap<String, HashSet<GameAction>>();
    public void actionsFileReader(String filePath) {
        try {
            //Get all action information from the actions file
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(filePath);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getChildNodes();

            // Get all actions (only the odd items are actually actions - 1, 3, 5 etc.)
            for (int i = 1; i < actionNodes.getLength(); i+=2) {
                Element actionElement = (Element) actionNodes.item(i);
                GameAction action = createAction(actionElement);
                for (String trigger : action.getTriggers()) {
                    if (!actions.containsKey(trigger)) {
                        actions.put(trigger, new HashSet<>());
                    }
                    actions.get(trigger).add(action);
                }
            }
        } catch(ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when attempting to read basic actions file");
            pce.printStackTrace();
        } catch(SAXException saxe) {
            System.out.println("SAXException was thrown when attempting to read basic actions file");
            saxe.printStackTrace();
        } catch(IOException ioe) {
            System.out.println("IOException was thrown when attempting to read basic actions file");
            ioe.printStackTrace();
        }
    }

    private GameAction createAction(Element actionElement) {
        HashSet<String> triggers = getElementSet(actionElement, "triggers");
        HashSet<String> subjects = getElementSet(actionElement, "subjects");
        HashSet<String> consumed = getElementSet(actionElement, "consumed");
        HashSet<String> produced = getElementSet(actionElement, "produced");
        String narration = getElementText(actionElement);

        return new GameAction(triggers, subjects, consumed, produced, narration);
    }

    private HashSet<String> getElementSet(Element actionElement, String tagName) {
        HashSet<String> set = new HashSet<>();
        NodeList nodeList = actionElement.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            set.add(element.getTextContent());
        }
        return set;
    }

    private String getElementText(Element actionElement) {
        return actionElement.getElementsByTagName("narration").item(0).getTextContent();
    }
}
