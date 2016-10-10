package jconfserver;

import java.io.IOException;
import java.sql.SQLException;

import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

public class Main {

	public static void main(String[] args) throws IOException, SQLException {
		// // TODO Auto-generated method stub
		// Property property = new Property();
		// property.setName("DB222");
		// property.setValue("okoko");
		// property.setPackage("com.test");
		// property.setType(PropertyType.STATIC);
		// PropertyHandler.getInstance().put(property);
		// JPA jpa = new JPA();
		// Package p = new Package();
		// p.setName("uho0ok");
		// p.setDescription("gyggy");
		// jpa.addPackage(p);

		Project p = new Project();
//		p.setId("1");
		p.setName("config2");
		Property prop = new Property();
		prop.setName("1245678");
		//prop.setValue("lol");
	//	prop.setProject(p);

		// DBUtils.deleteProperty(prop);
		//DBUtils.persistProperty(prop);
//		p = DBUtils.get(p, "Name");
		System.out.println(p.toString());
		// JPA jpa = new JPA();
		//// p = jpa.getAddProject(p);
		// prop.setProject(p);
		//// jpa.addProperty(prop);
		// // jpa.addProperty(prop);
		// prop = jpa.getProperty("Project1","Prop1");
		// System.out.println(prop.toString());
		// System.out.println(prop.getValue());

		// System.out.println("Your Host addr: " +
		// InetAddress.getLocalHost().getHostAddress()); // often
		// // returns
		// // "127.0.0.1"
		// Enumeration<NetworkInterface> n =
		// NetworkInterface.getNetworkInterfaces();
		// for (; n.hasMoreElements();) {
		// NetworkInterface e = n.nextElement();
		//
		// Enumeration<InetAddress> a = e.getInetAddresses();
		// for (; a.hasMoreElements();) {
		// InetAddress addr = a.nextElement();
		// if (addr.isSiteLocalAddress())
		// System.out.println(" " + addr.getHostAddress());
		// }
		// }
		// JPA jpa = new JPA();
		//// Property prop = new Property();
		// Project p = new Project();
		// p.setId(1);
		//// prop.setName("key");
		//// prop.setUpdateEveryMin(1);
		//// prop.setPackage(p);
		//// prop.setDB_URL("testURL");
		//// prop.setValue("val");
		//// jpa.addProperty(prop);
		//
		// List<Property>props = jpa.getPackageProperties(p);
		// props.stream().forEach(pr->System.out.println(pr.toJSON()));

	}

}
