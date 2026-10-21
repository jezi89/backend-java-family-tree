import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void shouldCreatePersonWithNameAndLastName() {
        Person person = new Person("John", "Doe", 30, "M");

        assertEquals("John", person.getName());
        assertEquals("Doe", person.getLastName());
        assertEquals(30, person.getAge());
        assertEquals("M", person.getSex());
    }

    @Test
    void shouldAddParents() {
        Person mother = new Person("Jane", "Doe", 55, "F");
        Person father = new Person("John", "Doe", 58, "M");
        Person child = new Person("Jack", "Doe", 25, "M");

        child.addParents(mother, father);

        assertEquals(mother, child.getMother());
        assertEquals(father, child.getFather());
    }

    @Test
    void shouldReturnPetsOfGrandChildren() {
        Person grandmother = new Person("Oma", "Doe", 80, "F");
        Person parent = new Person("Pa", "Doe", 50, "M");
        Person grandChild = new Person("Kim", "Doe", 10, "F");
        grandChild.addPet(new Pet("Milo", 2, "Dog"));
        parent.addParents(grandmother, null);
        grandChild.addParents(null, parent);

        assertEquals(1, grandmother.getGrandChildrenPets().size());
        assertEquals("Milo", grandmother.getGrandChildrenPets().get(0).getName());
        assertTrue(parent.getGrandChildrenPets().isEmpty());
    }

    @Test
    void shouldReturnOnlyFemaleChildrenOfSiblingsAsNieces() {
        Person aunt = new Person("Tante", "Doe", 40, "F");
        Person sibling = new Person("Pa", "Doe", 42, "M");
        Person niece = new Person("Nicht", "Doe", 10, "F");
        Person nephew = new Person("Neef", "Doe", 8, "M");
        aunt.addSibling(sibling);
        niece.addParents(null, sibling);
        nephew.addParents(null, sibling);

        assertEquals(1, aunt.getNieces().size());
        assertSame(niece, aunt.getNieces().get(0));
    }

    @Test
    void shouldKeepPartnerSymmetricAndReplaceOldPartner() {
        Person a = new Person("A", "Doe", 30, "M");
        Person b = new Person("B", "Doe", 30, "F");
        Person c = new Person("C", "Doe", 30, "F");

        a.setPartner(b);
        assertSame(a, b.getPartner());

        a.setPartner(c);
        assertSame(c, a.getPartner());
        assertSame(a, c.getPartner());
        assertNull(b.getPartner());

        a.setPartner(null);
        assertNull(c.getPartner());
    }

    @Test
    void shouldAddChild() {
        Person parent = new Person("Anna", "Smith", 40, "F");
        Person child = new Person("Emma", "Smith", 15, "F");

        parent.addChild(child);

        assertTrue(parent.getChildren().contains(child));
        assertEquals(parent, child.getMother());
    }

    @Test
    void shouldAddSibling() {
        Person person1 = new Person("Alice", "Brown", 20, "F");
        Person person2 = new Person("Bob", "Brown", 18, "M");

        person1.addSibling(person2);

        assertTrue(person1.getSiblings().contains(person2));
        assertTrue(person2.getSiblings().contains(person1));
    }

    @Test
    void shouldAddPet() {
        Person person = new Person("Sara", "Taylor", 30, "F");
        Pet pet = new Pet("Max", 4, "Dog");

        person.addPet(pet);

        assertTrue(person.getPets().contains(pet));
        assertEquals(person, pet.getOwner());
    }

    @Test
    void shouldReturnGrandChildren() {
        Person grandParent = new Person("Grandma", "Jones", 70, "F");
        Person parent = new Person("Mom", "Jones", 45, "F");
        Person child = new Person("Kid", "Jones", 20, "M");

        grandParent.addChild(parent);
        parent.addChild(child);

        assertTrue(grandParent.getGrandChildren().contains(child));
    }
}
