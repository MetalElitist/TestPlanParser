
package testPlanPackage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import GUI.MainWindow;

import org.w3c.dom.Element;


public class TestPlanParser {

	public ArrayList<httpSampler> httpSamplers = new ArrayList<httpSampler>();
	MainWindow window;
	
	public TestPlanParser() {
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
			for (int i = 0; true; i++) {
				sampler = httpSamplersNodes.item(i);
				if (sampler == null) {
					break;
				}
				
				String samplerName = sampler.getAttributes().getNamedItem("testname").getNodeValue();
				Node stringProp = findNodeWithAttribure(sampler, "stringProp", "name", "Argument.value");

				String textContent = "";
				if (stringProp != null) {
					textContent = stringProp.getTextContent();
				}
				
				httpSampler HttpSampler = new httpSampler(samplerName, i, textContent);
				httpSamplers.add(HttpSampler);
			}
			
			httpSampler[] samplersArray = new httpSampler[httpSamplers.size()];
			httpSamplers.toArray(samplersArray);

			window.setHttpSamplers(samplersArray);
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
		new TestPlanParser();
	}

}
