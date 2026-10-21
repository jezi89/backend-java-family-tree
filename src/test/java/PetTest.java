import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {

    @Test
    void shouldCreatePetWithNameAgeAndSpecies() {
        Pet pet = new Pet("Milo", 5, "Dog");

        assertEquals("Milo", pet.getName());
        assertEquals(5, pet.getAge());
        assertEquals("Dog", pet.getSpecies());
    }

    @Test
    void shouldSetOwner() {
        Person person = new Person("Jill", "Miller", 27, "F");
        Pet pet = new Pet("Milo", 5, "Dog");

        pet.setOwner(person);

        assertEquals(person, pet.getOwner());
    }
}
