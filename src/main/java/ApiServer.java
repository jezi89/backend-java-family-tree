import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

// De webserver van de backend. Hij luistert op een poort (standaard 8080) en wacht op berichten van de frontend.
// Elk bericht (een "request") bevat een pad zoals /api/persons en een methode zoals GET of POST.
public class ApiServer {
    // Jackson zet Java-objecten om in JSON-tekst en andersom. Onbekende velden in binnenkomende JSON negeren we
    // (de frontend stuurt bijvoorbeeld ook childIds mee, die de database niet nodig heeft).
    private static final ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Alleen de frontend op dit adres mag de API aanroepen. De browser dwingt dat af (CORS, zie handle()).
    private static final String ALLOWED_ORIGIN = System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:5173");
    // Het object dat met de database praat. De server zelf bevat dus geen SQL.
    private static final PersonRepository REPO = new PersonRepository();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // Hier koppelen we paden aan methodes: komt er een bericht voor /api/persons binnen,
        // dan roept de server de methode persons hieronder aan. (ApiServer::persons betekent "de methode persons".)
        server.createContext("/api/health", ex -> handle(ex, () -> Map.of("status", "ok")));
        server.createContext("/api/persons", ApiServer::persons);
        server.createContext("/api/pets", ApiServer::pets);
        server.start();
        System.out.println("API luistert op http://localhost:" + port);
    }

    // STAP 4 (zie docs/frontend-backend-uitleg.md): hier komen alle berichten voor /api/persons binnen.
    // De switch kijkt naar de methode van het bericht en kiest wat er moet gebeuren.
    // "yield" geeft in een switch-blok met accolades de uitkomst terug.
    private static void persons(HttpExchange ex) throws IOException {
        handle(ex, () -> switch (ex.getRequestMethod()) {
            case "GET" -> REPO.findAll(); // alle personen uit de database halen
            case "POST" -> Map.of("id", REPO.insert(read(ex, PersonRepository.NewPerson.class))); // nieuwe persoon, antwoord: het nieuwe id
            case "PUT" -> {
                REPO.update(idFromPath(ex), read(ex, PersonRepository.NewPerson.class)); // bestaande persoon aanpassen
                yield Map.of("ok", true);
            }
            case "DELETE" -> {
                REPO.delete(idFromPath(ex)); // persoon verwijderen
                yield Map.of("ok", true);
            }
            default -> throw new IllegalArgumentException("Methode niet ondersteund");
        });
    }

    // Hetzelfde voor huisdieren op /api/pets.
    private static void pets(HttpExchange ex) throws IOException {
        handle(ex, () -> switch (ex.getRequestMethod()) {
            case "POST" -> Map.of("id", REPO.insertPet(read(ex, PersonRepository.NewPet.class)));
            case "DELETE" -> {
                REPO.deletePet(idFromPath(ex));
                yield Map.of("ok", true);
            }
            default -> throw new IllegalArgumentException("Methode niet ondersteund");
        });
    }

    // Leest de JSON uit het bericht van de frontend en maakt er een Java-object van (record NewPerson of NewPet).
    // De <T> betekent: dit werkt voor elk type dat je opgeeft.
    private static <T> T read(HttpExchange ex, Class<T> type) throws IOException {
        return JSON.readValue(ex.getRequestBody(), type);
    }

    // Het id zit achteraan in het pad: bij /api/persons/42 pakken we alles na de laatste "/" en maken er het getal 42 van.
    private static long idFromPath(HttpExchange ex) {
        String path = ex.getRequestURI().getPath();
        try {
            return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id ontbreekt in het pad");
        }
    }

    // Een "Action" is een stukje code dat een antwoord oplevert. Zo kan handle() elke actie veilig uitvoeren.
    private interface Action {
        Object run() throws Exception;
    }

    // STAP 7 (zie docs/frontend-backend-uitleg.md): voert de actie uit en stuurt het antwoord als JSON terug.
    // Alle endpoints gebruiken deze ene methode, dus CORS en foutafhandeling staan maar op één plek.
    private static void handle(HttpExchange ex, Action action) throws IOException {
        // CORS: de browser blokkeert standaard antwoorden van een ander adres (poort 8080 is "ander" dan 5173).
        // Met deze headers zegt de backend: "dit adres mag mij aanroepen, met deze methodes".
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        // Vóór een POST, PUT of DELETE stuurt de browser eerst stiekem een OPTIONS-vraag: "mag dit eigenlijk?".
        // Wij antwoorden 204 (= goed, maar zonder inhoud) en de browser stuurt daarna het echte bericht.
        if ("OPTIONS".equals(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            ex.close();
            return;
        }
        // Statuscodes zijn getallen waarmee de server zegt hoe het ging: 200 = gelukt, 201 = nieuw aangemaakt,
        // 400 = fout in wat de frontend stuurde, 500 = fout in de server zelf.
        int status = "POST".equals(ex.getRequestMethod()) ? 201 : 200;
        Object body;
        try {
            body = action.run();
        } catch (IllegalArgumentException | SQLException e) {
            // Bijvoorbeeld: sex = 'Q'. De database weigert dat (CHECK-regel) en wij melden dat terug aan de frontend.
            status = 400;
            body = Map.of("error", String.valueOf(e.getMessage()));
        } catch (Exception e) {
            status = 500;
            body = Map.of("error", String.valueOf(e.getMessage()));
        }
        // Het antwoord (lijst, Map of foutmelding) wordt JSON-tekst, daarna bytes, en gaat terug naar de browser.
        byte[] bytes = JSON.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
}
