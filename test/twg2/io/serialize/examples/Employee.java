package twg2.io.serialize.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twg2.io.serialize.base.DataElement;
import twg2.io.serialize.base.DataTransferable;
import twg2.io.serialize.base.reader.DataTransferInput;
import twg2.io.serialize.base.writer.DataTransferOutput;

/**
 * @author unknown
 * @since 2014-7-0
 */
public class Employee implements DataTransferable {
	int id;
	String name;
	boolean permanent;
	String address;
	long[] phoneNumbers;
	String role;
	List<String> cities;
	Map<String, String> properties;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((cities == null) ? 0 : cities.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (permanent ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(phoneNumbers);
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Employee)) {
			return false;
		}
		Employee empl = (Employee) obj;
		return id == empl.id &&
				name.equals(empl.name) &&
				permanent == empl.permanent &&
				address.equals(empl.address) &&
				Arrays.equals(phoneNumbers, empl.phoneNumbers) &&
				role.equals(empl.role) &&
				Arrays.equals(cities.toArray(), empl.cities.toArray()) &&
				properties.equals(empl.properties);
	}


	public String dif(Employee empl) {
		String str = id == empl.id ? "" : "id: " + id + "!=" + empl.id + ", ";
		str += name.equals(empl.name) ? "" : "name: " + name + "!=" + empl + ", ";
		str += permanent == empl.permanent ? "" : "permanent: " + permanent + "!=" + empl.permanent + ", ";
		str += address.equals(empl.address) ? "" : "address: " + address + "!=" + empl.address + ", ";
		str += Arrays.equals(phoneNumbers, empl.phoneNumbers) ? "" : "phoneNumbers: " + Arrays.toString(phoneNumbers)
				 + "!=" + Arrays.toString(empl.phoneNumbers) + ", ";
		str += role.equals(empl.role) ? "" : "role: " + role + "!=" + empl.role + ", ";
		str += Arrays.equals(cities.toArray(), empl.cities.toArray()) ? "" : "cities: " + cities + "!=" + empl.cities + ", ";
		str += properties.equals(empl.properties) ? "" : "properties: " + Arrays.toString(properties.entrySet().toArray())
				+ "!=" + Arrays.toString(empl.properties.entrySet().toArray());
		return str;
	}


	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Employee {\n");
		sb.append("id: " + id + ",\n");
		sb.append("name: " + name + ",\n");
		sb.append("permanent: " + permanent + ",\n");
		sb.append("role: " + role + ",\n");
		sb.append("phoneNumbers: " + Arrays.toString(phoneNumbers) + ",\n");
		sb.append("address: " + address + ",\n");
		sb.append("cities: " + Arrays.toString(cities.toArray()) + ",\n");
		sb.append("properties: " + properties + " }\n");
		return sb.toString();
	}


	@Override
	public void readData(DataTransferInput in) throws IOException {
		in.readStartBlock("Employee");
		id = in.readInt("id");
		name = in.readString("name");
		permanent = in.readBoolean("permanent");
		address = in.readString("address");
		phoneNumbers = Arrays.asList(in.readString("phoneNumbers").split(","))
				.stream().mapToLong((s) -> Long.parseLong(s)).toArray();
		role = in.readString("role");
		cities = new ArrayList<>();
		{
			in.readStartBlock("cities");
			DataElement tag = in.readNext();
			while(!tag.isEndBlock()) {
				if(!tag.getName().equals("city")) { throw new IOException("unknown element " + tag); }
				cities.add(tag.getContent());
				tag = in.readNext();
			}
		}
		properties = new HashMap<>();
		{
			in.readStartBlock("properties");
			DataElement tag = in.readNext();
			while(!tag.isEndBlock()) {
				properties.put(tag.getName(), tag.getContent());
				tag = in.readNext();
			}
		}
		in.readEndBlock();
	}


	@Override
	public void writeData(DataTransferOutput out) throws IOException {
		out.writeStartBlock("Employee");
		out.writeInt("id", id);
		out.writeString("name", name);
		out.writeBoolean("permanent", permanent);
		out.writeString("address", address);
		String str = "";
		for(long phone : phoneNumbers) {
			str += phone + ",";
		}
		str = str.substring(0, str.length()-1);
		out.writeString("phoneNumbers", str);
		out.writeString("role", role);
		out.writeStartBlock("cities");
		{
			for(String city : cities) {
				out.writeString("city", city);
			}
		}
		out.writeEndBlock();
		out.writeStartBlock("properties");
		{
			for(Map.Entry<String, String> prop : properties.entrySet()) {
				out.writeString(prop.getKey(), prop.getValue());
			}
		}
		out.writeEndBlock();
		out.writeEndBlock();
	}


	public static final Employee createEmployee() {
		Employee employee = new Employee();
		employee.id = 22;
		employee.name = "No One";
		employee.permanent = true;
		employee.address = "Street-Name, City, 01234";
		employee.phoneNumbers = new long[] {1111111111L, 2222222222L, 9999999999L};
		employee.role = "designer";
		employee.cities = new ArrayList<String>();
		employee.cities.add("City A");
		employee.cities.add("City 2");
		employee.cities.add("City C");
		employee.properties = new HashMap<String, String>();
		employee.properties.put("hasHair", "true");
		employee.properties.put("dob", "1984-6-8");
		employee.properties.put("gender", "male");
		employee.properties.put("overweight", "true");
		return employee;
	}

}
