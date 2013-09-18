package xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

/** Interface for objects that can be saved as XML data.
 * @author TeamworkGuy2
 * @since 2013-2-1
 */
public interface XMLable {

	/** Initialize this object with XML data read from the specified {@link XMLInput} stream
	 * @param inputStream - the XML data input stream
	 * @throws IOException if there is an error reading data from the input stream
	 * @throws XMLStreamException if there is an error reading XML data from the input stream
	 */
	public void readXML(XMLInput inputStream) throws IOException, XMLStreamException;

	/** Write this XML object to the specified {@link XMLOutput} stream
	 * @param outputStream - the XML data output stream
	 * @throws IOException if there is an error writing data to the output stream
	 * @throws XMLStreamException if there is an error writing XML data to the output stream
	 */
	public void writeXML(XMLOutput outputStream) throws IOException, XMLStreamException;

}
