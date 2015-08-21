package twg2.io.serialize.base.reader;

import java.io.IOException;

import twg2.io.serialize.base.DataElement;

/**
 * @author TeamworkGuy2
 * @since 2015-5-21
 */
public interface PeekableReader extends PrimitiveReader {

	/** Peek at the next block of this input stream without reading it.
	 * This call reads the next data block and returns it, however
	 * the next call to {@link #readNext()} or equivalent parameterized
	 * version will return this peek header.<br/>
	 * The purpose of this method is to parse arbitrary objects from an input
	 * stream by peeking at the next element in the stream and call
	 * the correct sub parser and let that parser read its header
	 * without realizing that the header was already peeked at.
	 * @return the next data header from this input stream.
	 * @throws IOException if there is an IO error while reading from the input stream
	 */
	public DataElement peekNext() throws IOException;

}
