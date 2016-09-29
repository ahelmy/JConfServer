package jconfserver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import outsidethebox.java.hibernate.JPA;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.rest.pojo.GsonObject;

public class Main {

	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		Property property = new Property();
//		property.setName("DB222");
//		property.setValue("okoko");
//		property.setPackage("com.test");
//		property.setType(PropertyType.STATIC);
//		PropertyHandler.getInstance().put(property);
//		JPA jpa = new JPA();
//		Package p = new Package();
//		p.setName("uho0ok");
//		p.setDescription("gyggy");
//		jpa.addPackage(p);
		Project p = new Project();
		p.setId(1);
		p.setName("config2");
		Property prop = new  Property();
		prop.setName("1245678");
		prop.setProject(p);
		prop.setId(1);
		JPA jpa = new JPA();
//		p = jpa.getAddProject(p);
		prop.setProject(p);
//		jpa.addProperty(prop);
	//	jpa.addProperty(prop);
		prop = jpa.getProperty("Project1","Prop1");
		System.out.println(prop.toString());
		System.out.println(prop.getValue());
//		JPA jpa = new JPA();
////		Property prop = new Property();
//		Project p = new Project();
//		p.setId(1);
////		prop.setName("key");
////		prop.setUpdateEveryMin(1);
////		prop.setPackage(p);
////		prop.setDB_URL("testURL");
////		prop.setValue("val");
////		jpa.addProperty(prop);
//		
//		List<Property>props = jpa.getPackageProperties(p);
//		props.stream().forEach(pr->System.out.println(pr.toJSON()));
		
		
	}

}
