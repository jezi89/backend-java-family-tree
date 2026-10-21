public class Main {
    public static void main(String[] args) {
        Person mother = new Person("Anna", "Jansen", 45, "F");
        Person father = new Person("Pieter", "Jansen", 48, "M");

        Person child = new Person("Lisa", "Jansen", 20, "F");
        child.addParents(mother, father);

        Pet pet = new Pet("Milo", 5, "Dog");
        child.addPet(pet);

        System.out.println("Child: " + child.getName());
        System.out.println("Mother: " + child.getMother().getName());
        System.out.println("Father: " + child.getFather().getName());
        System.out.println("Pet: " + child.getPets().get(0).getName());
    }
}
