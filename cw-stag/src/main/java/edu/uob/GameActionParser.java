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
                Element triggers = (Element)actionElement.getElementsByTagName("triggers").item(0);
                NodeList keyPhraseList = triggers.getElementsByTagName("keyphrase");
                Element subjects = (Element)actionElement.getElementsByTagName("subjects").item(0);
                NodeList subjectEntityList = subjects.getElementsByTagName("entity");
                Element consumed = (Element)actionElement.getElementsByTagName("consumed").item(0);
                NodeList consumedEntityList = consumed.getElementsByTagName("entity");
                Element produced = (Element)actionElement.getElementsByTagName("produced").item(0);
                NodeList producedEntityList = produced.getElementsByTagName("entity");
                Element narration = (Element)actionElement.getElementsByTagName("narration").item(0);
                for (int j = 0; j < keyPhraseList.getLength(); j++) {
                    HashSet<GameAction> actionSet = new HashSet<>();
                    // Get all current trigger in current game action
                    String curTrigger = triggers.getElementsByTagName("keyphrase").item(j).getTextContent();
                    HashSet<String> subjectSet = new HashSet<>();
                    for (int k = 0; k < subjectEntityList.getLength(); k++) {
                        String curSubject =subjects.getElementsByTagName("entity").item(k).getTextContent();
                        subjectSet.add(curSubject);
                    }
                    HashSet<String> consumedSet = new HashSet<>();
                    for (int k = 0; k < consumedEntityList.getLength(); k++) {
                        String curConsumed =consumed.getElementsByTagName("entity").item(k).getTextContent();
                        consumedSet.add(curConsumed);
                    }
                    HashSet<String> producedSet = new HashSet<>();
                    for (int k = 0; k < producedEntityList.getLength(); k++) {
                        String curProduced =produced.getElementsByTagName("entity").item(k).getTextContent();
                        producedSet.add(curProduced);
                    }
                    String curNarration = narration.getTextContent();
                    GameAction newGameAction = new GameAction(subjectSet, consumedSet, producedSet, curNarration);
                    actionSet.add(newGameAction);
                    actions.put(curTrigger,actionSet);
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


}
