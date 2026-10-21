import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Alles wat met de database te maken heeft staat hier: SQL lezen en schrijven.
// ApiServer vraagt "geef alle personen" en hoeft niet te weten hoe dat in SQL gaat.
public class PersonRepository {

    // Een record is een korte manier om een "pakketje met velden" te maken (zonder getters en setters).
    // Jackson maakt van deze records de JSON die de frontend krijgt. De veldnamen hier (zoals motherId)
    // zijn dus precies de namen die je in de React-code terugziet (person.motherId).
    // Dto = "data transfer object": een pakketje dat alleen bedoeld is om gegevens te versturen.
    public record PetDto(long id, String name, int age, String species) {
    }

    public record PersonDto(long id, String name, String middleName, String lastName, String sex, int age,
                            Long motherId, Long fatherId, Long partnerId, List<Long> childIds, List<PetDto> pets,
                            List<String> grandChildrenPets, List<String> nieces) {
    }

    // Wat de frontend stuurt als er een persoon bijkomt of wordt aangepast (zie addPerson en updatePerson in api.js).
    public record NewPerson(String name, String middleName, String lastName, String sex, int age,
                            Long motherId, Long fatherId, Long partnerId) {
    }

    public record NewPet(String name, int age, String species, long ownerId) {
    }

    // STAP 6 (zie docs/frontend-backend-uitleg.md): alle personen uit de database halen en klaarmaken als JSON-pakketjes.
    // Leest alle rijen en bouwt met de Person-methodes dezelfde objectgraaf als in Main.java.
    public List<PersonDto> findAll() throws SQLException {
        // Eerst een paar lege "dozen" (Maps) om gegevens in te bewaren. Het getal (id) is steeds de sleutel.
        // Mother/father/partner bewaren we als losse id-getallen, omdat we de echte Person-objecten pas kunnen
        // koppelen als álle personen zijn ingelezen.
        Map<Long, Person> persons = new LinkedHashMap<>();
        Map<Long, Long> motherIds = new LinkedHashMap<>();
        Map<Long, Long> fatherIds = new LinkedHashMap<>();
        Map<Long, Long> partnerIds = new LinkedHashMap<>();
        Map<Long, List<PetDto>> petsByOwner = new LinkedHashMap<>();

        // try-with-resources: de verbinding gaat vanzelf weer dicht als het blok klaar is, ook bij een fout.
        try (Connection c = Database.connect(); Statement st = c.createStatement()) {
            // Dit is SQL: "geef mij deze kolommen uit de tabel persons". Het resultaat (ResultSet) lees je rij voor rij met next().
            try (ResultSet rs = st.executeQuery("SELECT id, name, middle_name, last_name, sex, age, "
                    + "mother_id, father_id, partner_id FROM persons ORDER BY id")) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    persons.put(id, new Person(rs.getString("name"), rs.getString("middle_name"),
                            rs.getString("last_name"), rs.getInt("age"), rs.getString("sex")));
                    motherIds.put(id, nullableLong(rs, "mother_id"));
                    fatherIds.put(id, nullableLong(rs, "father_id"));
                    partnerIds.put(id, nullableLong(rs, "partner_id"));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT id, name, age, species, owner_id FROM pets ORDER BY id")) {
                while (rs.next()) {
                    long ownerId = rs.getLong("owner_id");
                    Person owner = persons.get(ownerId);
                    if (owner != null) {
                        owner.addPet(new Pet(rs.getString("name"), rs.getInt("age"), rs.getString("species")));
                        petsByOwner.computeIfAbsent(ownerId, k -> new ArrayList<>())
                                .add(new PetDto(rs.getLong("id"), rs.getString("name"), rs.getInt("age"),
                                        rs.getString("species")));
                    }
                }
            }
        }

        // Nu alle personen bestaan, leggen we de verbanden: wie is wiens ouder en partner? Dit gebruikt de bestaande
        // methodes van jullie Person-klasse, dus dezelfde regels als in Main.java.
        persons.forEach((id, person) -> {
            person.addParents(persons.get(motherIds.get(id)), persons.get(fatherIds.get(id)));
            person.setPartner(persons.get(partnerIds.get(id)));
        });
        // broers en zussen = personen met dezelfde moeder of vader
        for (Person a : persons.values()) {
            for (Person b : persons.values()) {
                if (a != b && (sameParent(a.getMother(), b.getMother()) || sameParent(a.getFather(), b.getFather()))) {
                    a.addSibling(b);
                }
            }
        }

        Map<Person, Long> idByPerson = new IdentityHashMap<>();
        persons.forEach((id, person) -> idByPerson.put(person, id));

        // Elke Person wordt een PersonDto: het pakketje dat Jackson straks als JSON naar de frontend stuurt.
        // childIds, grandChildrenPets en nieces rekent de Person-klasse zelf uit (getChildren, getGrandChildrenPets, getNieces).
        List<PersonDto> result = new ArrayList<>();
        persons.forEach((id, p) -> result.add(new PersonDto(id, p.getName(), p.getMiddleName(), p.getLastName(),
                p.getSex(), p.getAge(), motherIds.get(id), fatherIds.get(id), partnerIds.get(id),
                p.getChildren().stream().map(idByPerson::get).toList(),
                petsByOwner.getOrDefault(id, List.of()),
                p.getGrandChildrenPets().stream().map(Pet::getName).toList(),
                p.getNieces().stream().map(n -> n.getName() + " " + n.getLastName()).toList())));
        return result;
    }

    // STAP 10 (zie docs/frontend-backend-uitleg.md): een nieuwe persoon in de database zetten.
    public long insert(NewPerson p) throws SQLException {
        // De vraagtekens zijn "gaatjes" die we hieronder met bind() vullen. Zo kan niemand SQL in een naamveld
        // stoppen om de database te saboteren (SQL-injectie). RETURNING id geeft het nieuwe id meteen terug.
        String sql = "INSERT INTO persons (name, middle_name, last_name, sex, age, mother_id, father_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = Database.connect()) {
            // Een transactie: alles hieronder lukt samen of er verandert niets. Anders kan een persoon half
            // opgeslagen zijn (wel aangemaakt, maar de partnerkoppeling mislukt).
            c.setAutoCommit(false);
            long id;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, p);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    id = rs.getLong(1);
                }
            }
            setPartner(c, id, p.partnerId());
            c.commit();
            return id;
        }
    }

    public void update(long id, NewPerson p) throws SQLException {
        String sql = "UPDATE persons SET name = ?, middle_name = ?, last_name = ?, sex = ?, age = ?, "
                + "mother_id = ?, father_id = ? WHERE id = " + id;
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, p);
                ps.executeUpdate();
            }
            setPartner(c, id, p.partnerId());
            c.commit();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM persons WHERE id = ?")) {
            ps.setLong(1, id);
            // De database regelt de rest: kinderen en partner verliezen alleen hun verwijzing (ON DELETE SET NULL in het schema)
            // en de huisdieren van deze persoon verdwijnen mee (ON DELETE CASCADE).
            ps.executeUpdate();
        }
    }

    public long insertPet(NewPet p) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO pets (name, age, species, owner_id) VALUES (?, ?, ?, ?) RETURNING id")) {
            ps.setString(1, p.name());
            ps.setInt(2, p.age());
            ps.setString(3, p.species());
            ps.setLong(4, p.ownerId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void deletePet(long id) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM pets WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private static void bind(PreparedStatement ps, NewPerson p) throws SQLException {
        ps.setString(1, p.name());
        ps.setString(2, p.middleName() == null ? "" : p.middleName());
        ps.setString(3, p.lastName());
        ps.setString(4, p.sex());
        ps.setInt(5, p.age());
        ps.setObject(6, p.motherId());
        ps.setObject(7, p.fatherId());
    }

    // Een partnerschap staat in de database bij allebei: Otto wijst naar Judith én Judith naar Otto.
    // Daarom: eerst alle oude koppelingen van beide personen weghalen, dan de nieuwe twee kanten opslaan.
    // Houdt partner_id dus wederzijds.
    private static void setPartner(Connection c, long id, Long partnerId) throws SQLException {
        try (PreparedStatement clear = c.prepareStatement(
                "UPDATE persons SET partner_id = NULL WHERE id = ? OR partner_id = ?")) {
            for (Long person : new Long[]{id, partnerId}) {
                if (person != null) {
                    clear.setLong(1, person);
                    clear.setLong(2, person);
                    clear.executeUpdate();
                }
            }
        }
        if (partnerId != null) {
            try (PreparedStatement link = c.prepareStatement("UPDATE persons SET partner_id = ? WHERE id = ?")) {
                link.setLong(1, partnerId);
                link.setLong(2, id);
                link.executeUpdate();
                link.setLong(1, id);
                link.setLong(2, partnerId);
                link.executeUpdate();
            }
        }
    }

    private static boolean sameParent(Person a, Person b) {
        return a != null && a == b;
    }

    private static Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}
