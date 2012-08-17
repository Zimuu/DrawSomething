package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Dictionary {
	
	public static final List<String> DICTIONARY = new ArrayList<String>();
	private static final Random random = new Random();
	
	private static final String FILE = "data\\dictionary.xml";
	
	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(FILE);
			doc.getDocumentElement().normalize();
			
			NodeList list = doc.getElementsByTagName("word");
			
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
					DICTIONARY.add(node.getTextContent());
			}
		} catch (Exception e) {}
	}
	
	public static boolean add(String word) {
		if (word.length() > 8 
				|| DICTIONARY.contains(word.trim().toLowerCase())
				) return false;
		
		DICTIONARY.add(word.trim().toLowerCase());
		return true;
	}
	
	public static boolean match(String word) {
		return DICTIONARY.contains(word);
	}
	
	public static String generate() {
		return DICTIONARY.get(random.nextInt(DICTIONARY.size()));
	}
	
	public static void writeXML() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element rootElement = doc.createElement("dictionary");
			doc.appendChild(rootElement);
			
			for (String word : DICTIONARY) {
				Element tag = doc.createElement("word");
				tag.appendChild(doc.createTextNode(word));
				rootElement.appendChild(tag);
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult stream = new StreamResult(FILE);
			transformer.transform(source, stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
