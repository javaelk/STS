package uw.star.rts.util;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.*;

import javax.xml.bind.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XMLJAXBUtil {
	/**
	 * Helper method to unmarshall a xml doc
	 * @see http://docs.oracle.com/javase/tutorial/jaxb/intro/basic.html
	 * http://jaxb.java.net/nonav/2.2.6/docs/ch03.html#unmarshalling
	 * this uses <T> JAXBElement<T> unmarshal(Source source,
                           Class<T> declaredType)
                         throws JAXBException
	 * 
	 */
/*	public static <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws JAXBException{
		String packageName = docClass.getPackage().getName();
	    JAXBContext jc = JAXBContext.newInstance( packageName );
	    Unmarshaller u = jc.createUnmarshaller();
	    JAXBElement<T> root = u.unmarshal(new StreamSource(inputStream),docClass);
	    return root.getValue();
	}*/
	
	
	public static <T> T unmarshall(Class<T> docClass, InputStream inputStream) throws JAXBException,ParserConfigurationException,SAXException {
			String packageName = docClass.getPackage().getName();
			JAXBContext jc = JAXBContext.newInstance( packageName );
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://apache.org/xml/features/validation/schema", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			InputSource inputSource = new InputSource(inputStream);
			SAXSource source = new SAXSource(xmlReader,inputSource);
			
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<T> root = u.unmarshal(source,docClass);
			return root.getValue();
	}
}
