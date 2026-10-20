package stamboom;
import java.util.ArrayList;
import java.util.List;

public class Pet extends Familymember {
private String species;
private List<Person> owners = new ArrayList<>();



	public Pet(String name, int age, String species) {
		setName(name);
		setAge(age);
		this.species = species;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public void addOwner(Person owner) {
		owners.add(owner);
	}

	public List<Person> getOwners() {
		return owners;
	}
	@Override
	public String toString() {
		return "Pet Name: " + getName() + "\n" +
				"Species: " + getSpecies() + "\n" +
				"Owners: " + getOwners();
	}
}