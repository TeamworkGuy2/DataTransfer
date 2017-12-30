DataTransfer
============
version: `0.2.0`

Data serialization/deserialization via interfaces similar to `java.io.Externalizable`.
Currently JSON and XML readers and writers are implemented, but the DataTransferInput and DataTransferOutput interfaces makes it easy to implement other protocol handlers.

Also contains an interesting implementing of an all-in-one primitive (boolean, byte, int, float, etc.) and string wrapper, see `DataProxy`.
This class can hold any primitive, primitive array, String, or String array with array offset and length.
It also provides methods for adding elements to array types allowing it to be used like an ArrayList for any primitive type as well as Strings.

NOTE: this is library is completely unnecessary given the existence of mature serialization/deserialization libraries like [Jackson](https://github.com/FasterXML).
This is just for experimenting and learning.