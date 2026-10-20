package stamboom;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Person extends Familymember {
	private final String preFix;
	private final String familyName;
	private RealGender sex;
	private final List<Person> fathers;
	private final List<Person> mothers;
	private Person partner;
	private RealGender biologicalGender;
	private List<Person> siblings;
	private List<Person> children;
	private List<Pet> pets;

	public Person(String name, String preFix, String familyName, int age, RealGender sex) {
		setName(name);
		this.preFix = preFix;
		this.familyName = familyName;
		setAge(age);
		this.sex = sex;
		this.fathers = new ArrayList<>();
		this.mothers = new ArrayList<>();
		this.siblings = new ArrayList<>();
		this.children = new ArrayList<>();
		this.pets = new ArrayList<>();
	}

	public String getLastName() {
		return preFix + " " + familyName;
	}

	public List<Person> getFathers() {
		return fathers;
	}

	public void addFather(Person father) {
		if (fathers.size() < 2 && !fathers.contains(father)) {
			fathers.add(father);
		} else {
			System.out.println("Cannot add more than two fathers or duplicate father.");
		}
	}

	public List<Person> getMothers() {
		return mothers;
	}

	public void addMother(Person mother) {
		if (mothers.size() < 2 && !mothers.contains(mother)) {
			mothers.add(mother);
		} else {
			System.out.println("Cannot add more than two mothers or duplicate mother.");
		}
	}

	public RealGender getSex() {
		return sex;
	}

	public void setSex(RealGender sex) {
		this.sex = sex;
	}

	public Person getPartner() {
		return partner;
	}

	public void setPartner(Person partner) {
		this.partner = partner;
	}

	public List<Person> getSiblings() {
		return siblings;
	}

	public void setSiblings(List<Person> siblings) {
		this.siblings = siblings;
	}

	public List<Person> getChildren() {
		return children;
	}

	public void setChildren(List<Person> children) {
		this.children = new ArrayList<>();
		if (children == null) {
			return;
		}
		for (Person child : children) {
			addChild(child);
		}
	}

	public RealGender getBiologicalGender() {
		return biologicalGender;
	}

	public void setBiologicalGender(RealGender biologicalGender) {
		this.biologicalGender = biologicalGender;
	}

	public List<Pet> getPets() {
		return pets;
	}

	public void setPets(List<Pet> pets) {
		this.pets = pets;
	}

	public void addChild(Person child) {
		if (!children.contains(child)) {
			children.add(child);
			if (this.sex.name().equals("Male")) {
				child.setFather(this);
			} else if (this.sex.name().equals("Female")) {
				child.setMother(this);
			}
		} else {
			System.out.println("This child is already added.");
		}
	}

	public void setMother(Person mother) {
		if (!mothers.contains(mother)) {
			mothers.add(mother);
		}
	}

	public void setFather(Person father) {
		if (!fathers.contains(father)) {
			fathers.add(father);
		}
	}

	public void addParents(Person mother, Person father) {
		this.addMother(mother);
		this.addFather(father);
	}

	public void addSibling(Person sibling, Scanner scanner) {
		if (!siblings.contains(sibling)) {
			siblings.add(sibling);
			if (sibling.getSex() == null) {
				System.out.println("Is " + sibling.getName() + " een jongen of een meisje? (M/F)");
				String input = scanner.nextLine();
				RealGender gender = input.equalsIgnoreCase("M") ? RealGender.Male : RealGender.Female;
				sibling.setSex(gender);
			}
		} else {
			System.out.println("This sibling is already added.");
		}
	}

	public void addPet(Pet pet) {
		if (!pets.contains(pet)) {
			pets.add(pet);
		} else {
			System.out.println("This pet is already added.");
		}
	}

	public List<Person> getGrandChildren() {
		List<Person> grandChildren = new ArrayList<>();
		for (Person child : getChildren()) {
			grandChildren.addAll(child.getChildren());
		}
		return grandChildren;
	}

	public void printPersonDetails() {
		System.out.println(getName());
		System.out.println(getLastName());
		System.out.println(getAge());
		System.out.println(getSex());
		System.out.println(getFathers());
		System.out.println(getMothers());
		System.out.println(getPartner() != null ? getPartner().getName() : "No partner");
		System.out.println(getSiblings());
		System.out.println(getChildren());
		System.out.println(getPets());
	}

	public void printPetDetails() {
		for (Pet pet : pets) {
			System.out.println(pet.getName());
			System.out.println(pet.getSpecies());
			for (Person owner : pet.getOwners()) {
				System.out.println(owner.getName());
			}
		}
	}

	@Override
	public String toString() {
		return "Name: " + getName() + " " + getLastName() + "\n" +
				"Age: " + getAge() + "\n" +
				"Gender: " + getSex() + "\n" +
				"Fathers: " + getFathers() + "\n" +
				"Mothers: " + getMothers() + "\n" +
				"Partner: " + (getPartner() != null ? getPartner().getName() : "No partner") + "\n" +
				"Siblings: " + getSiblings() + "\n" +
				"Children: " + getChildren() + "\n" +
				"Pets: " + getPets();
	}
}