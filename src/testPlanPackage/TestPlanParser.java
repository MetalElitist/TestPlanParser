
package testPlanPackage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;


public class TestPlanParser {

	HashMap<String, String> httpSamplers = new HashMap<String, String>();
	MainWindow window;
	
	public TestPlanParser(String filename) {
		window = new MainWindow(this);
		window.setVisible(true);
	}
	
	public void openFile(File fXmlFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			NodeList httpSamplersNodes = doc.getElementsByTagName("HTTPSamplerProxy");
			Node sampler;
			ArrayList<String> samplerNamesList = new ArrayList<String>();
			for (int i = 0; true; i++) {
				sampler = httpSamplersNodes.item(i);
				if (sampler == null) {
					break;
				}
				
				String samplerName = sampler.getAttributes().getNamedItem("testname").getNodeValue();
				samplerNamesList.add(samplerName);
				Node stringProp = findNodeWithAttribure(sampler, "stringProp", "name", "Argument.value");
				
				if (stringProp != null) {
					httpSamplers.put(samplerName, stringProp.getTextContent());
				}
			}
			
			String[] samplersNamesArray = new String[samplerNamesList.size()];
			samplerNamesList.toArray(samplersNamesArray);

			window.setHttpSamplers(samplersNamesArray);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Node findNodeWithAttribure(Node rootNode, String tag, String attr, String attrValue) {
		
		Element rootElement = (Element) rootNode;
		NodeList stringProps = rootElement.getElementsByTagName(tag);

		Node node = stringProps.item(0);
		
		int i = 0;
		while (true) {
			if (node == null) break;
			String nameAttr = node.getAttributes().getNamedItem(attr).getNodeValue();
			if (nameAttr.equals(attrValue)) {
				break;
			}
			node = stringProps.item(i);
			i++;
		}
		return node;
	}

	public static void main(String[] args) {
		new TestPlanParser(args[0]);
	}

}
