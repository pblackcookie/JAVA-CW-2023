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
                processActionElement(actionElement);
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

    private void processActionElement(Element actionElement) {
        // Get all actions element and put them into the actions map
        Element triggers = (Element) actionElement.getElementsByTagName("triggers").item(0);
        NodeList keyPhraseList = triggers.getElementsByTagName("keyphrase");
        Element subjects = (Element) actionElement.getElementsByTagName("subjects").item(0);
        NodeList subjectEntityList = subjects.getElementsByTagName("entity");
        Element consumed = (Element) actionElement.getElementsByTagName("consumed").item(0);
        NodeList consumedEntityList = consumed.getElementsByTagName("entity");
        Element produced = (Element) actionElement.getElementsByTagName("produced").item(0);
        NodeList producedEntityList = produced.getElementsByTagName("entity");
        Element narration = (Element) actionElement.getElementsByTagName("narration").item(0);

        for (int i = 0; i < keyPhraseList.getLength(); i++) {
            HashSet<GameAction> actionSet = new HashSet<>();
            String curTrigger = keyPhraseList.item(i).getTextContent();
            HashSet<String> subjectSet = getNodeTextContentSet(subjectEntityList);
            HashSet<String> consumedSet = getNodeTextContentSet(consumedEntityList);
            HashSet<String> producedSet = getNodeTextContentSet(producedEntityList);
            String curNarration = narration.getTextContent();
            GameAction newGameAction = new GameAction(subjectSet, consumedSet, producedSet, curNarration);
            actionSet.add(newGameAction);
            actions.put(curTrigger, actionSet);
        }
    }

    // Add the subjects & consumed & produced HashSet.
    private HashSet<String> getNodeTextContentSet(NodeList nodeList) {
        HashSet<String> textContentSet = new HashSet<>();
        for (int k = 0; k < nodeList.getLength(); k++) {
            String curContent = nodeList.item(k).getTextContent();
            textContentSet.add(curContent);
        }
        return textContentSet;
    }


}
