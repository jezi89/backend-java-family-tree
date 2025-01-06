package stamboom;
import java.util.ArrayList;
import java.util.List;

public class Person extends Familymember {
	private String middleName;
	final String lastName;
	private String father;
	private String mother;
	private String partner;
	final String sex;
	private List<Person> siblings;
	private List<Person> children;
	private List<Pet> pets;

	public Person(String name, String lastName, int age, String sex) {
		setName(name);
		this.lastName = lastName;
		setAge(age);
		this.sex = sex;
	}

	public Person(String name, String middleName, String lastName, int age, String sex) {
		setName(name);
		this.middleName = middleName;
		this.lastName = lastName;
		setAge(age);
		this.sex = sex;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFather() {
		return father;
	}

	public String getMother() {
		return mother;
	}

	public String getPartner() {
		return partner;
	}

	public List<Person> getSiblings() {
	return this.siblings;
	};

	public List<Person> getChildren() {
		return this.children;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setFather(String father) {
		this.father = father;
	}

	public void setMother(String mother) {
		this.mother = mother;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public void setSiblings(List<Person> siblings) {
		this.siblings = siblings;
	}

	public void setChildren(List<Person> children) {
		this.children = children;
	}

	public void setPets(List<Pet> pets) {
		this.pets = pets;
	}

	public static String addParents() {
		if (mother == true && father == true) {
			return "Parents added";
		} else {
			return "Parents not added";

		}
	}

	public void addChild() {
		ArrayList<Person> children = new ArrayList<Person>();

			}

	public void addPet() {
		ArrayList<Pet> pets = new ArrayList<Pet>();
	}

	public void addSibling() {
		ArrayList<Person> siblings = new ArrayList<Person>();
	}
	}