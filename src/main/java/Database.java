import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Weet hoe we bij de database komen. Alle andere klassen vragen hier een verbinding aan.
public class Database {
    // De instellingen komen uit omgevingsvariabelen (instellingen buiten de code). URL en gebruiker hebben een lokale
    // standaardwaarde; het wachtwoord niet: dat staat nooit in de code en moet je zelf instellen via DB_PASSWORD.
    // jdbc:postgresql://localhost:5433/familytree = type database, computer, poort en naam van de database.
    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5433/familytree");
    private static final String USER = env("DB_USER", "familytree_app");

    // STAP 5 (zie docs/frontend-backend-uitleg.md): een verbinding met PostgreSQL openen.
    // JDBC is de standaardmanier waarop Java met databases praat; het postgresql-pakket uit de pom.xml is de "vertaler".
    public static Connection connect() throws SQLException {
        try {
            // Dit laadt de PostgreSQL-vertaler expliciet. Nodig omdat mvn exec:java een eigen classloader gebruikt
            // en JDBC de vertaler anders niet vindt ("No suitable driver found").
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL-driver ontbreekt", e);
        }
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new SQLException("Omgevingsvariabele DB_PASSWORD is niet gezet");
        }
        return DriverManager.getConnection(URL, USER, password);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value != null ? value : fallback;
    }
}
