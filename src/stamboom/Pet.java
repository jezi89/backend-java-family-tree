package stamboom;
import java.util.ArrayList;

public class Pet extends Familymember {
private String species;
private ArrayList<Person> owners;


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

	public ArrayList<Person> getOwners() {
		return owners;
	}

	public void setOwners(ArrayList<Person> owners) {
		this.owners = owners;
	}
}