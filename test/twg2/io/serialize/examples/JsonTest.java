package twg2.io.serialize.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.json.JsonReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * @author TeamworkGuy2
 * @since 2014-4-12
 */
public class JsonTest {

	public JsonTest(File jsonFile, Charset charset) throws IOException {
		JsonFactory factory = new JsonFactory();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), charset));
		JsonGenerator output = factory.createGenerator(writer);
		output.writeStartObject();
		output.writeStringField("this", jsonFile.getPath());
		output.writeEndObject();
		output.close();
		main(null);
	}


	public static void main(String[] args) throws IOException {
		File file = new File("rsc/stream_employee.json");
		Charset charset = Charset.forName("UTF-8");
		Employee emp = Employee.createEmployee();

		JsonGenerator jsonGenerator = new JsonFactory().createGenerator(new FileOutputStream(file));
		//for pretty printing
		jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

		jsonGenerator.writeStartObject(); // start root object
		jsonGenerator.writeNumberField("id", emp.id);
		jsonGenerator.writeStringField("name", emp.name);
		jsonGenerator.writeBooleanField("permanent", emp.permanent);

		String[] addressParts = emp.address.split(", ");
		jsonGenerator.writeObjectFieldStart("address"); //start address object
		jsonGenerator.writeStringField("street", addressParts[0]);
		jsonGenerator.writeStringField("city", addressParts[1]);
		jsonGenerator.writeNumberField("zipcode", Integer.parseInt(addressParts[2]));
		jsonGenerator.writeEndObject(); //end address object

		jsonGenerator.writeArrayFieldStart("phoneNumbers");
		for(long num : emp.phoneNumbers) {
			jsonGenerator.writeNumber(num);
		}
		jsonGenerator.writeEndArray();

		jsonGenerator.writeStringField("role", emp.role);

		jsonGenerator.writeArrayFieldStart("cities"); //start cities array
		for(String city : emp.cities)
			jsonGenerator.writeString(city);
		jsonGenerator.writeEndArray(); //closing cities array

		jsonGenerator.writeObjectFieldStart("properties");
		for(String key : emp.properties.keySet()){
			String value = emp.properties.get(key);
			jsonGenerator.writeStringField(key, value);
		}
		jsonGenerator.writeEndObject(); //closing properties
		jsonGenerator.writeEndObject(); //closing root object

		jsonGenerator.flush();
		jsonGenerator.close();

		MainIo.printPlainJsonEvents(file, charset);

		JsonReader in = new JsonReader(file);
		System.out.println("id: " + in.readInt("id"));
		System.out.println("name: " + in.readString("name"));
		System.out.println("permanent: " + in.readBoolean("permanent"));

		in.readStartBlock("address");
		System.out.println("street: " + in.readString("street"));
		System.out.println("city: " + in.readString("city"));
		System.out.println("zipcode: " + in.readInt("zipcode"));
		in.readEndBlock();

		DataElement element = in.readNext();
		while(element.getName() == null) {
			System.out.println("phone number: " + in.readInt(null));
			element = in.readNext();
		}

		System.out.println("role: " + in.readString("role"));

		element = in.readNext();
		while(element.getName() == null) {
			System.out.println("city: " + in.readString(null));
			element = in.readNext();
		}

		element = in.readStartBlock("properties");
		while(!element.isEndBlock()) {
			System.out.println("property: " + in.readString(null));
			element = in.readNext();
		}

		in.readEndBlock();
		in.close();
	}

}
