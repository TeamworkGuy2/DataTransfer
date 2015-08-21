package twg2.io.serialize.base;

import twg2.io.serialize.base.reader.ReadableObject;
import twg2.io.serialize.base.writer.WritableObject;

/** Interface for objects that can be read from input streams and written to
 * output streams.
 * @author TeamworkGuy2
 * @since 2013-8-27
 */
public interface DataTransferable extends ReadableObject, WritableObject {

}
