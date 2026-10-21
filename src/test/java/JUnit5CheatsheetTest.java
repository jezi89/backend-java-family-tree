import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Cheatsheet - Alle veelgebruikte annotaties en assertions
 *
 * Dit bestand bevat voorbeelden van alle JUnit 5 testen die je nodig kunt hebben
 * voor het testen van de Person en Pet klassen in het familieboom-project.
 */
class JUnit5CheatsheetTest {

    // ==========================================
    // 1. ANNOTATIES - LifeCycle en Test
    // ==========================================

    /**
     * @BeforeAll - Wordt 1x uitgevoerd VOORDAT alle testen starten.
     * Alleen voor static methods. Nuttig voor gedeelde setup.
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("=== VOOR ALLE TESTEN ===");
    }

    /**
     * @AfterAll - Wordt 1x uitgevoerd NA alle testen.
     * Alleen voor static methods. Nuttig voor cleanup.
     */
    @AfterAll
    static void afterAll() {
        System.out.println("=== NA ALLE TESTEN ===");
    }

    /**
     * @BeforeEach - Wordt VOOR ELKE test uitgevoerd.
     * Gebruik dit om objects te creeren voor elke test.
     */
    private Person persoon;
    private Pet huisdier;

    @BeforeEach
    void beforeEach() {
        persoon = new Person("Jan", "Van Testen", 30, "M");
        huisdier = new Pet("Buddy", 5, "Hond");
        System.out.println("Voordat test start...");
    }

    /**
     * @AfterEach - Wordt NA ELKE test uitgevoerd.
     * Gebruik dit om resources op te ruimen.
     */
    @AfterEach
    void afterEach() {
        System.out.println("Na test...");
    }

    /**
     * @Test - Markeert een methode als test.
     */
    @Test
    void voorbeeldTest() {
        assertTrue(true, "Dit is een simpele test");
    }

    /**
     * @Disabled - Zet een test uit (skip).
     * Voeg een reden toe als commentaar.
     */
    @Disabled("Deze test is tijdelijk uitgeschakeld")
    @Test
    void uitgeschakeldeTest() {
        fail("Deze test wordt niet uitgevoerd");
    }

    /**
     * @DisplayName - Geeft een leesbare naam aan een test.
     */
    @Test
    @DisplayName("Person constructor moet correct initialiseren")
    void testMetLeesbareNaam() {
        Person p = new Person("Test", "Naam", 25, "F");
        assertEquals("Test", p.getName());
    }

    /**
     * @Nested - Groepeer gerelateerde testen in een binnenliggende klasse.
     */
    @Nested
    class ConstructorTesten {
        @Test
        void testPersonConstructor() {
            Person p = new Person("Anna", "Jansen", 28, "F");
            assertEquals("Anna", p.getName());
            assertEquals("Jansen", p.getLastName());
            assertEquals(28, p.getAge());
            assertEquals("F", p.getSex());
        }

        @Test
        void testPersonConstructorMetTussennaam() {
            Person p = new Person("Bert", "Willem", "De Vries", 40, "M");
            assertEquals("Bert", p.getName());
            assertEquals("Willem", p.getMiddleName());
            assertEquals("De Vries", p.getLastName());
        }
    }

    /**
     * @ParameterizedTest - Voer een test meerdere keren uit met verschillende waarden.
     */
    @ParameterizedTest
    @ValueSource(strings = {"M", "F", "m", "f"})
    void testGeslachten(String geslacht) {
        Person p = new Person("Test", "Naam", 20, geslacht);
        assertEquals(geslacht, p.getSex());
    }

    // ==========================================
    // 2. ASSERTIONS - Waarden vergelijken
    // ==========================================

    /**
     * assertEquals(expected, actual) - Controleer of twee waarden gelijk zijn.
     */
    @Test
    void testAssertEquals() {
        assertEquals("Jan", persoon.getName());
        assertEquals(30, persoon.getAge());
        assertEquals("Van Testen", persoon.getLastName());
    }

    /**
     * assertNotEquals(unexpected, actual) - Controleer dat waarden NIET gelijk zijn.
     */
    @Test
    void testAssertNotEquals() {
        assertNotEquals("Piet", persoon.getName());
        assertNotEquals(50, persoon.getAge());
    }

    /**
     * assertEquals met delta - Voor float/double vergelijkingen.
     */
    @Test
    void testAssertEqualsMetDelta() {
        assertEquals(3.14, 3.14159, 0.1, "Pi binnen delta van 0.1");
    }

    /**
     * assertEquals met message - Voeg een bericht toe bij falen.
     */
    @Test
    void testAssertEqualsMetBericht() {
        assertEquals("Jan", persoon.getName(), "Voornaam moet Jan zijn");
    }

    // ==========================================
    // 3. ASSERTIONS - Boolean en Null checks
    // ==========================================

    /**
     * assertTrue(condition) - Controleer dat een conditie waar is.
     */
    @Test
    void testAssertTrue() {
        assertTrue(persoon.getAge() > 0);
        assertTrue(persoon.getName().length() > 0);
        assertTrue(persoon.getChildren().isEmpty(), "Kindjes moeten leeg zijn bij aanmaken");
    }

    /**
     * assertFalse(condition) - Controleer dat een conditie onwaar is.
     */
    @Test
    void testAssertFalse() {
        assertFalse(persoon.getName().isEmpty());
        assertFalse(persoon.getAge() < 0);
    }

    /**
     * assertNull(actual) - Controleer dat een waarde null is.
     */
    @Test
    void testAssertNull() {
        assertNull(persoon.getMother(), "Moeder moet null zijn bij aanmaken");
        assertNull(persoon.getFather(), "Vader moet null zijn bij aanmaken");
        assertNull(huisdier.getOwner(), "Eigenaar moet null zijn bij aanmaken");
    }

    /**
     * assertNotNull(actual) - Controleer dat een waarde NIET null is.
     */
    @Test
    void testAssertNotNull() {
        assertNotNull(persoon.getName());
        assertNotNull(persoon.getChildren());
        assertNotNull(persoon.getSiblings());
        assertNotNull(persoon.getPets());
    }

    // ==========================================
    // 4. ASSERTIONS - Object identiteit
    // ==========================================

    /**
     * assertSame(expected, actual) - Controleer dat het OMZELFDE object is.
     */
    @Test
    void testAssertSame() {
        List<Person> kinderen = persoon.getChildren();
        assertSame(kinderen, persoon.getChildren(), "Zelfde lijst referentie");
    }

    /**
     * assertNotSame(unexpected, actual) - Controleer dat het VERSCHILLENDE objecten zijn.
     */
    @Test
    void testAssertNotSame() {
        Person p1 = new Person("A", "B", 10, "M");
        Person p2 = new Person("A", "B", 10, "M");
        assertNotSame(p1, p2, "Twee aparte objecten");
    }

    // ==========================================
    // 5. ASSERTIONS - Arrays en Collecties
    // ==========================================

    /**
     * assertArrayEquals(expected, actual) - Vergelijk arrays element per element.
     */
    @Test
    void testAssertArrayEquals() {
        String[] expected = {"A", "B", "C"};
        String[] actual = {"A", "B", "C"};
        assertArrayEquals(expected, actual);
    }

    /**
     * assertIterableEquals - Vergelijk lijsten element per element.
     */
    @Test
    void testAssertIterableEquals() {
        List<String> expected = List.of("A", "B", "C");
        List<String> actual = new ArrayList<>(List.of("A", "B", "C"));
        assertIterableEquals(expected, actual);
    }

    /**
     * Collections testen met contains, size, isEmpty.
     */
    @Test
    void testCollections() {
        // Test lege lijsten bij aanmaken
        assertTrue(persoon.getChildren().isEmpty());
        assertTrue(persoon.getSiblings().isEmpty());
        assertTrue(persoon.getPets().isEmpty());

        // Test toevoegen en controleren
        Person kind = new Person("Kind", "Test", 5, "M");
        persoon.addChild(kind);

        assertEquals(1, persoon.getChildren().size());
        assertTrue(persoon.getChildren().contains(kind));
    }

    // ==========================================
    // 6. ASSERTIONS - Exceptions
    // ==========================================

    /**
     * assertThrows(exceptionType, executable) - Controleer dat een exception wordt gegooid.
     */
    @Test
    void testAssertThrows() {
        // Voorbeeld: test dat een NullPointerException wordt gegooid
        assertThrows(NullPointerException.class, () -> {
            String s = null;
            s.length();
        });
    }

    /**
     * assertThrows met exception inspectie.
     */
    @Test
    void testAssertThrowsMetInspectie() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Foutmelding");
        });
        assertEquals("Foutmelding", exception.getMessage());
    }

    /**
     * assertDoesNotThrow - Controleer dat GEEN exception wordt gegooid.
     */
    @Test
    void testAssertDoesNotThrow() {
        assertDoesNotThrow(() -> {
            persoon.setName("Nieuwe Naam");
            persoon.setAge(25);
        });
    }

    // ==========================================
    // 7. ASSERTIONS - Timeouts
    // ==========================================

    /**
     * assertTimeout(duration, executable) - Controleer dat code binnen tijd uitvoert.
     */
    @Test
    void testAssertTimeout() {
        assertTimeout(Duration.ofSeconds(5), () -> {
            // Code die maximaal 5 seconden mag duren
            persoon.addChild(new Person("Snel", "Test", 1, "M"));
        });
    }

    /**
     * assertTimeoutPreemptively - Stop code als het te lang duurt.
     */
    @Test
    void testAssertTimeoutPreemptively() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            // Wordt direct gestopt als het langer duurt
            return persoon.getName();
        });
    }

    // ==========================================
    // 8. ASSERTIONS - Groeperen
    // ==========================================

    /**
     * assertAll - Voer meerdere assertions uit; rapporteer ALLE fouten.
     */
    @Test
    void testAssertAll() {
        Person p = new Person("Test", "Naam", 25, "F");
        assertAll("Person eigenschappen",
                () -> assertEquals("Test", p.getName()),
                () -> assertEquals("Naam", p.getLastName()),
                () -> assertEquals(25, p.getAge()),
                () -> assertEquals("F", p.getSex()),
                () -> assertNotNull(p.getChildren()),
                () -> assertNotNull(p.getSiblings())
        );
    }

    /**
     * fail() - Forceer een test te falen.
     */
    @Test
    void testFail() {
        // Gebruik dit om onafgemaakte testen te markeren
        // fail("Nog niet geimplementeerd");
        assertTrue(true); // vervang dit met je test
    }

    // ==========================================
    // 9. PRACTISCHE TESTEN - Person klasse
    // ==========================================

    @Test
    void testGettersEnSetters() {
        // Test alle getters en setters
        persoon.setName("Nieuwe");
        assertEquals("Nieuwe", persoon.getName());

        persoon.setMiddleName("Tussennaam");
        assertEquals("Tussennaam", persoon.getMiddleName());

        persoon.setLastName("NieuweNaam");
        assertEquals("NieuweNaam", persoon.getLastName());

        persoon.setAge(35);
        assertEquals(35, persoon.getAge());

        persoon.setSex("F");
        assertEquals("F", persoon.getSex());
    }

    @Test
    void testAddParents() {
        Person moeder = new Person("Moeder", "Familie", 55, "F");
        Person vader = new Person("Vader", "Familie", 60, "M");
        Person kind = new Person("Kind", "Familie", 25, "M");

        kind.addParents(moeder, vader);

        // Test ouder-relaties
        assertSame(moeder, kind.getMother());
        assertSame(vader, kind.getFather());

        // Test dat kind toegevoegd is aan ouders
        assertTrue(moeder.getChildren().contains(kind));
        assertTrue(vader.getChildren().contains(kind));
    }

    @Test
    void testAddChild() {
        Person ouder = new Person("Ouder", "Familie", 40, "F");
        Person kind = new Person("Kind", "Familie", 10, "M");

        ouder.addChild(kind);

        assertTrue(ouder.getChildren().contains(kind));
        assertSame(ouder, kind.getMother());
    }

    @Test
    void testAddSibling() {
        Person p1 = new Person("Broer", "Familie", 25, "M");
        Person p2 = new Person("Zus", "Familie", 22, "F");

        p1.addSibling(p2);

        assertTrue(p1.getSiblings().contains(p2));
        assertTrue(p2.getSiblings().contains(p1));
    }

    @Test
    void testAddPet() {
        Person eigenaar = new Person("Eigenaar", "Familie", 30, "M");
        Pet hond = new Pet("Buddy", 3, "Hond");

        eigenaar.addPet(hond);

        assertTrue(eigenaar.getPets().contains(hond));
        assertSame(eigenaar, hond.getOwner());
    }

    @Test
    void testGetGrandChildren() {
        Person grootouder = new Person("Grootouder", "Familie", 70, "F");
        Person ouder = new Person("Ouder", "Familie", 40, "M");
        Person kind = new Person("Kind", "Familie", 10, "F");

        grootouder.addChild(ouder);
        ouder.addChild(kind);

        List<Person> kleinkinderen = grootouder.getGrandChildren();
        assertEquals(1, kleinkinderen.size());
        assertTrue(kleinkinderen.contains(kind));
    }

    @Test
    void testPetGettersEnSetters() {
        Pet pet = new Pet("Kat", 2, "Kat");

        pet.setName("NieuweKat");
        assertEquals("NieuweKat", pet.getName());

        pet.setAge(3);
        assertEquals(3, pet.getAge());

        pet.setSpecies("Persische Kat");
        assertEquals("Persische Kat", pet.getSpecies());
    }

    @Test
    void testPetConstructorMetEigenaar() {
        Person eigenaar = new Person("Test", "Naam", 30, "M");
        Pet pet = new Pet("Buddy", 5, "Hond", eigenaar);

        assertEquals("Buddy", pet.getName());
        assertEquals(5, pet.getAge());
        assertEquals("Hond", pet.getSpecies());
        assertSame(eigenaar, pet.getOwner());
    }

    // ==========================================
    // 10. DYNAMIC TESTS - Dynamisch gegenereerde testen
    // ==========================================

    /**
     * @TestFactory - Genereer testen dynamisch.
     */
    @TestFactory
    Collection<org.junit.jupiter.api.DynamicTest> dynamicTests() {
        return List.of(
                org.junit.jupiter.api.DynamicTest.dynamicTest(
                        "Person leeftijd moet positief zijn",
                        () -> {
                            Person p = new Person("Test", "Naam", 25, "M");
                            assertTrue(p.getAge() > 0);
                        }
                ),
                org.junit.jupiter.api.DynamicTest.dynamicTest(
                        "Person naam mag niet leeg zijn",
                        () -> {
                            Person p = new Person("Test", "Naam", 25, "M");
                            assertFalse(p.getName().isEmpty());
                        }
                )
        );
    }

    // ==========================================
    // 11. ASSUME - Voorwaarden voor testen
    // ==========================================

    /**
     * assumeTrue - Sla test over als conditie onwaar is.
     */
    @Test
    void testAssumeTrue() {
        org.junit.jupiter.api.Assumptions.assumeTrue(
                true,
                "Deze test wordt overgeslagen als conditie onwaar is"
        );
        assertTrue(true);
    }

    // ==========================================
    // 12. EXTENSIONS - Custom extensies
    // ==========================================

    /**
     * @ExtendWith - Voeg custom extensies toe.
     * Voorbeeld: @ExtendWith(MockitoExtension.class) voor Mockito.
     */
    // @ExtendWith(MockitoExtension.class)
    // @Mock private Person personMock;

}
