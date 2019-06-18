import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xmlfier {
	
	public static void userToXML(Set<User> users, String filename) throws SAXException, IOException, ParserConfigurationException, TransformerException
    {
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			        .newInstance();
			    DocumentBuilder documentBuilder = documentBuilderFactory
			        .newDocumentBuilder();
			    Document document = documentBuilder.newDocument();
			    Element root = document.createElement("Users");
			    document.appendChild(root);
			    for (User i : users) {
			      Element user = document.createElement("user");
			      Element name = document.createElement("name");
			      name.appendChild(document.createTextNode(i.getName()));
			      user.appendChild(name);
			      Element pass = document.createElement("password");
			      pass.appendChild(document.createTextNode(i.getPassword()));
			      user.appendChild(pass);
			      Element maxsize = document.createElement("maxsize");
			      maxsize.appendChild(document.createTextNode(Long.toString(i.getMaxSize())));
			      user.appendChild(maxsize);
			      Element drive = document.createElement("drive");
			      drive.appendChild(document.createTextNode(i.getDrive().getPath()));
			      user.appendChild(drive);
			      Element sharedDrive= document.createElement("sharedDirectories");
			      for (Directory j : i.getSharedDirectories()) {
			    	  sharedDrive.appendChild(document.createTextNode(j.getPath()));
			      }
			      user.appendChild(sharedDrive);
			      Element sharedFile= document.createElement("sharedFiles");
			      for (File j : i.getSharedFiles()) {
			    	  sharedFile.appendChild(document.createTextNode(j.getPath()));
			      }
			      user.appendChild(sharedFile);
			      root.appendChild(user);
			    }

			    DOMSource source = new DOMSource(document);
			    TransformerFactory transformerFactory = TransformerFactory.newInstance();
			    Transformer transformer = transformerFactory.newTransformer();
			    StreamResult result = new StreamResult(new File(filename));
			    transformer.transform(source, result);
    }
	
	public static Set<User> XMLToUsers(Directory root) throws ParserConfigurationException, SAXException, IOException{
		Set<User> users= new HashSet<>();
		File fXmlFile = new File("users.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("user");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
					
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
					
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				User user= new User();
				Element eElement = (Element) nNode;
				String name=eElement.getElementsByTagName("name").item(0).getTextContent();
				
				String password=eElement.getElementsByTagName("password").item(0).getTextContent();
				long size=Long.parseLong(eElement.getElementsByTagName("maxsize").item(0).getTextContent());
				Directory drive= Directory.findSubFolder(root, eElement.getElementsByTagName("drive").item(0).getTextContent());
				ArrayList<Directory> sharedDirs= new ArrayList<Directory>();
				for(int i=0;i<eElement.getElementsByTagName("sharedDirectories").getLength();i++) {
					Directory aux=Directory.findSubFolder(root,eElement.getElementsByTagName("sharedDirectories").item(i).getTextContent());
					if(aux!=null) {
						sharedDirs.add(aux);
					}
				}
				ArrayList<File> sharedFiles= new ArrayList<File>();
				for(int i=0;i<eElement.getElementsByTagName("sharedFiles").getLength();i++) {
					Directory aux=Directory.findSubFolder(root,eElement.getElementsByTagName("sharedFiles").item(i).getTextContent());
					if(aux!=null) {
						sharedDirs.add(aux);
					}
				}
				user.setName(name);
				System.out.println("user name:" + name);
				user.setMaxSize(size);
				System.out.println("user size:" + size);
				user.setDrive(drive);
				System.out.println("user drive:" + drive.getPath());
				user.setPassword(password);
				user.setSharedDirectories(sharedDirs);
				user.setSharedFiles(sharedFiles);
				users.add(user);
			}
		}
		return users;
		
	}

}
