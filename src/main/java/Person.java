import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private String middleName;
    private String lastName;
    private String sex;
    private int age;
    private Person mother;
    private Person father;
    private Person partner;
    private List<Person> siblings;
    private List<Person> children;
    private List<Pet> pets;

    public Person(String name, String lastName, int age, String sex) {
        this(name, "", lastName, age, sex);
    }

    public Person(String name, String middleName, String lastName, int age, String sex) {
        this.name = name;
        this.middleName = middleName;
        this.lastName = lastName;
        this.sex = sex;
        this.age = age;
        this.siblings = new ArrayList<>();
        this.children = new ArrayList<>();
        this.pets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getPartner() {
        return partner;
    }

    /** Zet de partner en houdt de relatie symmetrisch: de partner wijst terug en een oude partner wordt losgekoppeld. */
    public void setPartner(Person partner) {
        if (this.partner == partner) {
            return;
        }
        Person oldPartner = this.partner;
        this.partner = partner;

        if (oldPartner != null && oldPartner.partner == this) {
            oldPartner.partner = null;
        }
        if (partner != null) {
            if (partner.partner != null && partner.partner != this) {
                partner.partner.partner = null;
            }
            partner.partner = this;
        }
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
        this.children = children;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public void addParents(Person mother, Person father) {
        this.mother = mother;
        this.father = father;

        if (mother != null && !mother.getChildren().contains(this)) {
            mother.getChildren().add(this);
        }

        if (father != null && !father.getChildren().contains(this)) {
            father.getChildren().add(this);
        }
    }

    public void addChild(Person child) {
        if (!children.contains(child)) {
            children.add(child);
        }

        if (child != null) {
            if ("F".equalsIgnoreCase(this.sex) && child.getMother() == null) {
                child.setMother(this);
            }

            if ("M".equalsIgnoreCase(this.sex) && child.getFather() == null) {
                child.setFather(this);
            }

            if (child.getMother() == null && this.mother == null && "F".equalsIgnoreCase(this.sex)) {
                child.setMother(this);
            }

            if (child.getFather() == null && this.father == null && "M".equalsIgnoreCase(this.sex)) {
                child.setFather(this);
            }
        }
    }

    public void addPet(Pet pet) {
        if (pet != null && !pets.contains(pet)) {
            pets.add(pet);
            pet.setOwner(this);
        }
    }

    public void addSibling(Person sibling) {
        if (sibling != null && !siblings.contains(sibling)) {
            siblings.add(sibling);
        }

        if (sibling != null && !sibling.getSiblings().contains(this)) {
            sibling.getSiblings().add(this);
        }
    }

    public List<Person> getGrandChildren() {
        List<Person> grandChildren = new ArrayList<>();

        for (Person child : children) {
            if (child != null) {
                grandChildren.addAll(child.getChildren());
            }
        }

        return grandChildren;
    }

    public List<Pet> getGrandChildrenPets() {
        List<Pet> grandChildrenPets = new ArrayList<>();

        for (Person grandChild : getGrandChildren()) {
            grandChildrenPets.addAll(grandChild.getPets());
        }

        return grandChildrenPets;
    }

    /** Nichtjes: de dochters van de broers en zussen. */
    public List<Person> getNieces() {
        List<Person> nieces = new ArrayList<>();

        for (Person sibling : siblings) {
            for (Person child : sibling.getChildren()) {
                if ("F".equalsIgnoreCase(child.getSex())) {
                    nieces.add(child);
                }
            }
        }

        return nieces;
    }
}
