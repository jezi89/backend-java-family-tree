import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Weet hoe we bij de database komen. Alle andere klassen vragen hier een verbinding aan.
public class Database {
    // De instellingen komen uit omgevingsvariabelen (instellingen buiten de code). Staan ze er niet, dan gebruiken
    // we de lokale waarden. Zo hoeft een wachtwoord nooit in de code te staan als je dit ooit online zet.
    // jdbc:postgresql://localhost:5433/familytree = type database, computer, poort en naam van de database.
    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5433/familytree");
    private static final String USER = env("DB_USER", "familytree_app");
    private static final String PASSWORD = env("DB_PASSWORD", "CHANGE_ME");

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
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value != null ? value : fallback;
    }
}
