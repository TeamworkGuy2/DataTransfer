package examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.DataElement;
import base.DataTransferInput;
import base.DataTransferOutput;
import base.DataTransferable;

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
	public boolean equals(Object obj) {
		if(!(obj instanceof Employee)) {
			return false;
		}
		Employee empl = (Employee) obj;
		boolean r1 = id == empl.id;
		boolean r2 = name.equals(empl.name);
		boolean r3 = permanent == empl.permanent;
		boolean r4 = address.equals(empl.address);
		boolean r5 = Arrays.equals(phoneNumbers, empl.phoneNumbers);
		boolean r6 = role.equals(empl.role);
		boolean r7 = Arrays.equals(cities.toArray(), empl.cities.toArray());
		boolean r8 = properties.equals(empl.properties);
		return r1 && r2 && r3 && r4 && r5 && r6 && r7 && r8;
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
