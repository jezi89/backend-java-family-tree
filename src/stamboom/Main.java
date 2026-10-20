// Main.java
package stamboom;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class Main {

	private static boolean validateYesNoInput(Scanner scanner) {
		String input = scanner.nextLine().trim().toLowerCase();
		while (!input.equals("ja") && !input.equals("nee")) {
			System.out.println("Ongeldige invoer. Voer 'ja' of 'nee' in.");
			input = scanner.nextLine().trim().toLowerCase();
		}
		return input.equals("ja");
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		List<Person> persons = new ArrayList<>();
		Person p1 = new Person("Jerôme", "van der", "Zijde", 35, RealGender.Male);
		persons.add(p1);

		Pet bobbie = new Pet("Dog", 5, "Dog");
		p1.addPet(bobbie);

		// Vraag om ontbrekende details voor alle personen
		for (Person person : persons) {
			askForDetails(scanner, person);
		}

		// Vraag of een nieuwe persoon moet worden aangemaakt
		System.out.println("Wil je een nieuwe persoon aanmaken? (ja/nee)");
		if (validateYesNoInput(scanner)) {
			Person p2 = createNewPerson(scanner);
			persons.add(p2);
			System.out.println("Nieuwe persoon (p2) aangemaakt: " + p2.getName());
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

		System.out.println("Heeft " + person.getName() + " ouders? (ja/nee)");
		if (validateYesNoInput(scanner)) {
			System.out.println("Wat is de naam van de moeder?");
			Person mother = createNewPerson(scanner);
			System.out.println("Wat is de naam van de vader?");
			Person father = createNewPerson(scanner);
			person.addParents(mother, father);
		}

		System.out.println("Heeft " + person.getName() + " broers/zussen? (ja/nee)");
		if (validateYesNoInput(scanner)) {
			System.out.println("Hoeveel broers/zussen?");
			try {
				int numberOfSiblings = Integer.parseInt(scanner.nextLine());
				for (int i = 0; i < numberOfSiblings; i++) {
					System.out.println("Wat is de naam van broer/zus " + (i + 1) + "?");
					Person sibling = createNewPerson(scanner);
					person.addSibling(sibling, scanner);
				}
			} catch (NumberFormatException e) {
				System.out.println("Ongeldige invoer voor het aantal broers/zussen.");
			}
		}
	}

	private static Person createNewPerson(Scanner scanner) {
		System.out.println("Vul de details in voor de nieuwe persoon");

		System.out.println("Wat is de voornaam?");
		String name = scanner.nextLine();

		System.out.println("Wat is de volledige achternaam (inclusief prefix)?");
		String fullSurName = scanner.nextLine();

		System.out.println("Wat is de leeftijd?");
		int age = Integer.parseInt(scanner.nextLine());

		System.out.println("Wat is het geslacht? (M/F)");
		RealGender sex = scanner.nextLine().equalsIgnoreCase("M") ? RealGender.Male : RealGender.Female;

		return createPersonFromFullName(name + " " + fullSurName, age, sex);
	}

	private static Person createPersonFromFullName(String fullSurName, int age, RealGender sex) {
		String[] nameParts = fullSurName.split(" ");
		String preFix = "";
		String familyName = fullSurName;

		if (nameParts.length > 1) {
			preFix = String.join(" ", Arrays.copyOf(nameParts, nameParts.length - 1));
			familyName = nameParts[nameParts.length - 1];
		}

		return new Person("", preFix, familyName, age, sex);
	}
}