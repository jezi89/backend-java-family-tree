## Inleiding
In de les hebben we het gehad over Maven en unitTesten. 

Iedere applicatie die we maken, willen we opleveren inclusief testen. Dit is belangrijk, omdat dit een groot aantal bugs uit de applicatie kan halen, voordat de applicatie in productie gaat.


## Opdrachtbeschrijving
We gaan een applicatie maken om een familie stamboom te maken. Iedereen heeft familieleden: van sommige houden we heel veel en van sommige misschien wat minder. Maar hoe dan ook hebben we allemaal familie.
Voor deze applicatie hoeven we niet allerlei moeilijke opties toe te voegen.
In de applicatie die we gaan maken, is het mogelijk om nieuwe personen en huisdieren toe te voegen aan de familie stamboom. Wanneer we bijvoorbeeld kinderen toevoegen aan een persoon in de stamboom, is het natuurlijk wel zo netjes om deze persoon gelijk neer te zetten als vader of moeder. Naast deze methode gebruiken we nog meer familie gerelateerde methodes binnen deze applicatie. 

![Family!](./assets/Family.JPG)


## Randvoorwaarden
De opdracht moet voldoen aan de volgende voorwaarden:

- minimaal 1 `Person`
- minimaal 1 `Pet`
- alle methodes (behalve de getters en setters) worden getest door een unittest

De `Person` heeft volgende attributen:

- name
- middleName
- lastName
- sex
- age
- mother (Person)
- father (Person)
- siblings (lijst)
- children (lijst)
- pets (lijst)

De `Pet` heeft minimaal de volgende attributen:

- name
- age
- species
- owner (Person)

De `Person` bevat naast de attributen het volgende:

- constructor met name, lastname, age en sex
- constructor met name, middleName, lastname, age en sex
- getters & setters
- addParents methode
- addChild methode
- addPet methode
- addSibling methode
- getGrandChildren methode

De `Pet` bevat naast de attributen minimaal:

- een constructor met alle attributen op 'owner' na
- getters & setters


## Stappenplan
Let op: het is uitdagender om jouw eigen stappenplan te maken. Als je niet zo goed weet waar je moet beginnen, kun je onderstaand stappenplan gebruiken.

1. Maak de benodigde klassen aan.

2. Geef de klassen de juiste attributen.

3. Maak de constructors aan binnen de klassen.

4. Maak alle getters & setters.

5. Schrijf de benodigde methodes in de juiste klassen.

 _Tip voor addParents_: Parents zijn twee Persons
 
 _Tip voor addChild, addPet en addSibling_: Voeg toe aan de lijst.
 
 _Tip voor getGrandChildren_: Dit is een uitdagende methode. Grandchildren zijn de children van de children van een persoon. Gebruik loops.

6. Voeg Maven toe aan het project.

7. (optioneel) Schrijf unittesten voor alle getters en setters van zowel Pet als Person. 

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Person p1 = new Person("John", "Doel", 25, "M");
        Person p2 = new Person("Jane", "Doe", 30, "F");

        // Vraag om ontbrekende details voor p1 en p2
        askForDetails(scanner, p1);
        askForDetails(scanner, p2);

        // Vraag of een nieuwe persoon (p3) moet worden aangemaakt
        System.out.println("Wil je een nieuwe persoon (p3) aanmaken? (ja/nee)");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("ja")) {
            Person p3 = createNewPerson(scanner);
            System.out.println("Nieuwe persoon (p3) aangemaakt: " + p3.getName());
        }

        scanner.close();
    }

    private static void askForDetails(Scanner scanner, Person person) {
        System.out.println("Vul de ontbrekende details in voor " + person.getName());

        System.out.println("Heeft " + person.getName() + " een middle name? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Wat is de middle name?");
            person.setMiddleName(scanner.nextLine());
        }

        System.out.println("Heeft " + person.getName() + " ouders? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Wat is de naam van de moeder?");
            String motherName = scanner.nextLine();
            System.out.println("Wat is de naam van de vader?");
            String fatherName = scanner.nextLine();
            Person mother = new Person(motherName, person.getLastName(), 0, "F");
            Person father = new Person(fatherName, person.getLastName(), 0, "M");
            person.addParents(mother, father);
        }

        System.out.println("Heeft " + person.getName() + " kinderen? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Hoeveel kinderen?");
            int numberOfChildren = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < numberOfChildren; i++) {
                System.out.println("Wat is de naam van kind " + (i + 1) + "?");
                String childName = scanner.nextLine();
                Person child = new Person(childName, person.getLastName(), 0, "M/F");
                person.addChild(child);
            }
        }

        System.out.println("Heeft " + person.getName() + " broers/zussen? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Hoeveel broers/zussen?");
            int numberOfSiblings = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < numberOfSiblings; i++) {
                System.out.println("Wat is de naam van broer/zus " + (i + 1) + "?");
                String siblingName = scanner.nextLine();
                Person sibling = new Person(siblingName, person.getLastName(), 0, "M/F");
                person.addSibling(sibling);
            }
        }
    }

    private static Person createNewPerson(Scanner scanner) {
        System.out.println("Vul de details in voor de nieuwe persoon (p3)");

        System.out.println("Wat is de voornaam?");
        String name = scanner.nextLine();

        System.out.println("Wat is de achternaam?");
        String lastName = scanner.nextLine();

        System.out.println("Wat is de leeftijd?");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.println("Wat is het geslacht? (M/F)");
        String sex = scanner.nextLine();

        return new Person(name, lastName, age, sex);
    }
}import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Person> persons = new ArrayList<>();
        Person p1 = new Person("John", "Doel", 25, "M");
        Person p2 = new Person("Jane", "Doe", 30, "F");
        persons.add(p1);
        persons.add(p2);

        Pet bobbie = new Pet("Dog", 5, "Dog");
        p1.addPet(bobbie);

        // Vraag om ontbrekende details voor alle personen
        for (Person person : persons) {
            askForDetails(scanner, person);
        }

        // Vraag of een nieuwe persoon (p3) moet worden aangemaakt
        System.out.println("Wil je een nieuwe persoon (p3) aanmaken? (ja/nee)");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("ja")) {
            Person p3 = createNewPerson(scanner);
            persons.add(p3);
            System.out.println("Nieuwe persoon (p3) aangemaakt: " + p3.getName());
        }

        // Print details van alle personen en hun huisdieren
        for (Person person : persons) {
            person.printPersonDetails();
            person.printPetDetails();
        }

        scanner.close();
    }

    private static void askForDetails(Scanner scanner, Person person) {
        System.out.println("Vul de ontbrekende details in voor " + person.getName());

        System.out.println("Heeft " + person.getName() + " een middle name? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Wat is de middle name?");
            person.setMiddleName(scanner.nextLine());
        }

        System.out.println("Heeft " + person.getName() + " ouders? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Wat is de naam van de moeder?");
            String motherName = scanner.nextLine();
            System.out.println("Wat is de naam van de vader?");
            String fatherName = scanner.nextLine();
            Person mother = new Person(motherName, person.getLastName(), 0, "F");
            Person father = new Person(fatherName, person.getLastName(), 0, "M");
            person.addParents(mother, father);
        }

        System.out.println("Heeft " + person.getName() + " kinderen? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Hoeveel kinderen?");
            int numberOfChildren = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < numberOfChildren; i++) {
                System.out.println("Wat is de naam van kind " + (i + 1) + "?");
                String childName = scanner.nextLine();
                Person child = new Person(childName, person.getLastName(), 0, "M/F");
                person.addChild(child);
            }
        }

        System.out.println("Heeft " + person.getName() + " broers/zussen? (ja/nee)");
        if (scanner.nextLine().equalsIgnoreCase("ja")) {
            System.out.println("Hoeveel broers/zussen?");
            int numberOfSiblings = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < numberOfSiblings; i++) {
                System.out.println("Wat is de naam van broer/zus " + (i + 1) + "?");
                String siblingName = scanner.nextLine();
                Person sibling = new Person(siblingName, person.getLastName(), 0, "M/F");
                person.addSibling(sibling);
            }
        }
    }

    private static Person createNewPerson(Scanner scanner) {
        System.out.println("Vul de details in voor de nieuwe persoon (p3)");

        System.out.println("Wat is de voornaam?");
        String name = scanner.nextLine();

        System.out.println("Wat is de achternaam?");
        String lastName = scanner.nextLine();

        System.out.println("Wat is de leeftijd?");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.println("Wat is het geslacht? (M/F)");
        String sex = scanner.nextLine();

        return new Person(name, lastName, age, sex);
    }
}8. Schrijf unittesten voor de extra methodes van Person (addPArents, addChild, addPet, addSibling, getGrandChildren)



## Bonusopdrachten
Als je de bonusopdrachten maakt, zorg dan dat deze ook getest worden!

1. Schrijf een methode om alle dieren van de kleinkinderen terug te geven van een persoon. 
2. Schrijf een methode om alle nichtjes terug te geven van een persoon.
3. Voeg een partner toe aan de `Person` en implementeer deze partner door de gehele applicatie inclusief getter & setter en benodigde methodes.
4. Kun je nog meer toevoegingen bedenken om de 'stamboom applicatie' compleet te maken?
