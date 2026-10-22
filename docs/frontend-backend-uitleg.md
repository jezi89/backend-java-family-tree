# Hoe praten de frontend en de backend met elkaar?

Een uitleg stap voor stap, voor iemand die net begint met programmeren.
Bij elke stap staat een link naar de plek in de code. In de code zelf staat op die plek een comment
met `STAP 1`, `STAP 2`, enzovoort. Zoek op `STAP 4` in je editor en je springt naar de goede plek.

> De regelnummers in de links kloppen op het moment van schrijven. Verschuift de code, zoek dan op het stapnummer.

## Inhoud

- [Het idee in één plaatje](#het-idee-in-één-plaatje)
- [Een paar woorden die je nodig hebt](#een-paar-woorden-die-je-nodig-hebt)
- [Deel 1: de lijst met personen ophalen](#deel-1-de-lijst-met-personen-ophalen)
  - [Stap 1: de pagina start](#stap-1-de-pagina-start)
  - [Stap 2: de pagina vraagt om gegevens](#stap-2-de-pagina-vraagt-om-gegevens)
  - [Stap 3: de postbode verstuurt het bericht](#stap-3-de-postbode-verstuurt-het-bericht)
  - [Stap 4: de server ontvangt het bericht](#stap-4-de-server-ontvangt-het-bericht)
  - [Stap 5: de server opent de database](#stap-5-de-server-opent-de-database)
  - [Stap 6: de server haalt de personen op](#stap-6-de-server-haalt-de-personen-op)
  - [Stap 7: het antwoord gaat terug](#stap-7-het-antwoord-gaat-terug)
  - [Stap 8: de pagina tekent de boom](#stap-8-de-pagina-tekent-de-boom)
- [Deel 2: iemand toevoegen](#deel-2-iemand-toevoegen)
  - [Stap 9: de frontend stuurt een nieuwe persoon](#stap-9-de-frontend-stuurt-een-nieuwe-persoon)
  - [Stap 10: de backend bewaart de persoon](#stap-10-de-backend-bewaart-de-persoon)
- [Waarom werkt het niet? De vier gewone fouten](#waarom-werkt-het-niet-de-vier-gewone-fouten)
- [Zelf kijken hoe het gaat](#zelf-kijken-hoe-het-gaat)

---

## Het idee in één plaatje

Denk aan een restaurant.

| In het restaurant | In onze app | Bestand |
| --- | --- | --- |
| De gast die iets bestelt | De pagina in je browser (React) | [`App.jsx`](../frontend/src/App.jsx) |
| De ober die de bestelling opschrijft en wegbrengt | De postbode-functies | [`api.js`](../frontend/src/api.js) |
| De keuken die het eten maakt | De Java-server | [`ApiServer.java`](../src/main/java/ApiServer.java) |
| De voorraadkast | De database (PostgreSQL) | [`db/01_schema.sql`](../db/01_schema.sql) |

De gast loopt nooit zelf de voorraadkast in. Hij praat alleen met de ober, en de ober praat met de keuken.
Zo is het ook bij ons: **de browser praat nooit direct met de database**. Dat is veiliger, want het wachtwoord van de database blijft in de keuken.

```text
 Browser (React)                Java-server                    Database
 poort 5173                     poort 8080                     poort 5433
┌──────────────┐   fetch       ┌──────────────┐   JDBC/SQL    ┌──────────────┐
│   App.jsx    │ ───────────>  │ ApiServer    │ ───────────>  │  PostgreSQL  │
│   api.js     │  "geef mij    │ Repository   │  "SELECT ..." │  tabel       │
│              │   alle        │              │               │  persons     │
│              │   personen"   │              │               │              │
│              │ <───────────  │              │ <───────────  │              │
└──────────────┘  JSON-lijst   └──────────────┘   rijen       └──────────────┘
```

---

## Een paar woorden die je nodig hebt

| Woord | Wat is het? |
| --- | --- |
| **Frontend** | Wat je ziet in de browser: knoppen, tekst, formulieren. Hier is dat React. |
| **Backend** | Het deel dat op de achtergrond draait en de gegevens beheert. Hier is dat Java. |
| **API** | De lijst met dingen die je aan de backend mag vragen. Bijvoorbeeld "geef alle personen". |
| **HTTP** | De taal waarmee een browser en een server berichten sturen. |
| **GET / POST / PUT / DELETE** | Wat je wilt doen: ophalen, nieuw maken, aanpassen, weghalen. |
| **Statuscode** | Een getal waarmee de server zegt hoe het ging. 200 is gelukt, 400 is jouw fout, 500 is zijn fout. |
| **JSON** | Een manier om gegevens als tekst op te schrijven, zodat twee programma's ze kunnen uitwisselen. |
| **SQL** | De taal waarmee je met een database praat. |
| **JDBC** | De manier waarop Java SQL naar een database stuurt. |
| **CORS** | Een veiligheidsregel van de browser: een pagina mag alleen antwoord krijgen van een andere plek als die plek dat toestaat. |

Zo ziet JSON eruit. Dit is één persoon, zoals de backend hem naar de frontend stuurt:

```json
{
  "id": 4,
  "name": "Jerôme",
  "lastName": "van der Zijde",
  "sex": "M",
  "motherId": 1,
  "fatherId": 2,
  "partnerId": null,
  "pets": []
}
```

Het lijkt op een JavaScript-object. Het is wel tekst, en daarom kan het over het netwerk.

---

# Deel 1: de lijst met personen ophalen

Dit gebeurt elke keer als je de pagina opent. We volgen één bericht van begin tot eind.

## Stap 1: de pagina start

📍 [`frontend/src/main.jsx`, regel 6](../frontend/src/main.jsx#L6)

Als je `http://localhost:5173` opent, laadt de browser `index.html`. Daar staat een lege `<div id="root">` in.
`main.jsx` zoekt die div en laat React er onze app in tekenen.

```jsx
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

**In gewone taal:** "Zoek de lege plek op de pagina en zet daar onze `App` neer."

## Stap 2: de pagina vraagt om gegevens

📍 [`frontend/src/App.jsx`, regel 140](../frontend/src/App.jsx#L140) en [regel 153](../frontend/src/App.jsx#L153)

Direct nadat de pagina getekend is, wil hij weten wie er in de stamboom staan. Daar heeft hij de backend voor nodig.

```jsx
const [persons, setPersons] = useState([])   // het geheugen: eerst een lege lijst

const load = useCallback(async () => {
  try {
    setPersons(await getPersons())           // vraag de backend en bewaar het antwoord
    setError('')
  } catch (e) {
    setError(`API niet bereikbaar of fout: ${e.message}`)
  }
}, [])

useEffect(() => { load() }, [load])          // doe dit één keer, na het eerste tekenen
```

- `useState` is het geheugen van de pagina. Als je `setPersons` aanroept, tekent React het scherm opnieuw met de nieuwe lijst.
- `useEffect` betekent: "doe dit nadat de pagina getekend is". Ideaal om gegevens op te halen.
- `await` betekent: "wacht tot het antwoord er is". Een netwerkverzoek kost tijd, en de pagina bevriest niet terwijl hij wacht.
- `try / catch` vangt een fout op. Staat de backend uit, dan zie je een rode melding in plaats van een kapotte pagina.

**In gewone taal:** "Zodra ik getekend ben: vraag `getPersons()` om de lijst en onthoud die. Gaat er iets mis, laat een foutmelding zien."

## Stap 3: de postbode verstuurt het bericht

📍 [`frontend/src/api.js`, regel 8](../frontend/src/api.js#L8) en [regel 31](../frontend/src/api.js#L31)

`getPersons()` zit in `api.js`. Dit bestand is de enige plek in de frontend die met de backend praat.

```js
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

async function request(path, method = 'GET', body) {
  const response = await fetch(`${API_URL}${path}`, {
    method,
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  })
  const json = await response.json()
  if (!response.ok) throw new Error(json.error ?? `HTTP ${response.status}`)
  return json
}

export const getPersons = () => request('/api/persons')
```

Wat gebeurt hier, regel voor regel?

1. `getPersons()` roept `request('/api/persons')` aan. Er is geen methode opgegeven, dus het wordt `GET`.
2. `fetch(...)` stuurt een HTTP-bericht naar `http://localhost:8080/api/persons`. Dat is het adres van de Java-server.
3. Bij `GET` is er geen `body`, dus er wordt niets meegestuurd.
4. `response.json()` maakt van de tekst die terugkomt weer een JavaScript-lijst.
5. Is `response.ok` niet waar (bijvoorbeeld bij een 400 of 500), dan gooien we een fout met de melding van de server. Die vangt `load()` in stap 2 op.

**In gewone taal:** "Stuur een GET-brief naar `/api/persons`, wacht op het antwoord, pak de inhoud uit en geef die terug."

Het bericht dat de browser verstuurt, lijkt hierop:

```text
GET /api/persons HTTP/1.1
Host: localhost:8080
```

## Stap 4: de server ontvangt het bericht

📍 [`ApiServer.java`, regel 30](../src/main/java/ApiServer.java#L30) en [regel 36](../src/main/java/ApiServer.java#L36)

De Java-server luistert op poort 8080. In `main` staat welk pad bij welke methode hoort:

```java
server.createContext("/api/persons", ApiServer::persons);
```

Komt er een bericht voor `/api/persons` binnen, dan roept de server de methode `persons` aan. Daar kijkt een `switch` naar de HTTP-methode:

```java
private static void persons(HttpExchange ex) throws IOException {
    handle(ex, () -> switch (ex.getRequestMethod()) {
        case "GET" -> REPO.findAll();                                  // alle personen ophalen
        case "POST" -> Map.of("id", REPO.insert(read(ex, PersonRepository.NewPerson.class)));
        case "PUT" -> { /* persoon aanpassen */ }
        case "DELETE" -> { /* persoon verwijderen */ }
        default -> throw new IllegalArgumentException("Methode niet ondersteund");
    });
}
```

Dit bericht was een `GET`, dus de server doet `REPO.findAll()`. `REPO` is de `PersonRepository`: dat is de klas die alle SQL bevat.
De server zelf weet dus niets van de database. Hij vraagt het aan de `REPO`.

**In gewone taal:** "Een GET voor personen? Vraag aan de `REPO` om alle personen."

## Stap 5: de server opent de database

📍 [`Database.java`, regel 14](../src/main/java/Database.java#L14) en [regel 24](../src/main/java/Database.java#L24)

Om iets uit de database te halen, heb je eerst een verbinding nodig. Dat is als een telefoontje met de database.

```java
private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5433/familytree");
private static final String USER = env("DB_USER", "familytree_app");
// Het wachtwoord heeft bewust geen standaardwaarde: het staat nooit in de code.

public static Connection connect() throws SQLException {
    Class.forName("org.postgresql.Driver");
    String password = System.getenv("DB_PASSWORD"); // zelf instellen als omgevingsvariabele
    return DriverManager.getConnection(URL, USER, password);
}
```

- De URL bestaat uit: type database (`postgresql`), computer (`localhost`), poort (`5433`) en naam (`familytree`).
- `env(...)` leest een instelling van buiten de code. Staat die er niet, dan gebruikt hij de lokale waarde. Zo hoeft een wachtwoord nooit in de code te staan als je dit ooit online zet.
- Het `postgresql`-pakket in `pom.xml` is de "vertaler" tussen Java en PostgreSQL. `Class.forName(...)` laadt die vertaler.

**In gewone taal:** "Bel de database op poort 5433 en log in als `familytree_app`."

## Stap 6: de server haalt de personen op

📍 [`PersonRepository.java`, regel 36](../src/main/java/PersonRepository.java#L36), [regel 51](../src/main/java/PersonRepository.java#L51) en [regel 97](../src/main/java/PersonRepository.java#L97)

Nu komt de SQL. Dit is een vraag aan de database in zijn eigen taal:

```java
try (ResultSet rs = st.executeQuery("SELECT id, name, middle_name, last_name, sex, age, "
        + "mother_id, father_id, partner_id FROM persons ORDER BY id")) {
    while (rs.next()) {
        // maak voor elke rij een Person-object
    }
}
```

`SELECT ... FROM persons` betekent: "geef mij deze kolommen uit de tabel `persons`". Het antwoord komt rij voor rij binnen. `rs.next()` gaat naar de volgende rij, en de `while` stopt als er geen rijen meer zijn.

Dan gebeurt er iets slims. Voor elke rij maken we een `Person`-object, dezelfde klas als in `Main.java`. Daarna leggen we de verbanden met jullie eigen methodes:

```java
person.addParents(persons.get(motherIds.get(id)), persons.get(fatherIds.get(id)));
person.setPartner(persons.get(partnerIds.get(id)));
```

Dankzij die objecten kan Java dingen uitrekenen die niet in de database staan, zoals `getNieces()` (nichtjes) en `getGrandChildrenPets()` (dieren van de kleinkinderen).

Tot slot maken we van elke `Person` een klein pakketje, een `PersonDto`. Dat is precies wat naar de frontend gaat:

```java
persons.forEach((id, p) -> result.add(new PersonDto(id, p.getName(), /* ... */ )));
```

De veldnamen in `PersonDto` (zoals `motherId`) zijn dezelfde namen die je in React gebruikt (`person.motherId`). Dat moet kloppen: als je een naam verandert, moet je het aan beide kanten doen.

**In gewone taal:** "Vraag de database om alle rijen, maak er `Person`-objecten van, koppel ouders en partners, en maak voor elke persoon een pakketje om te versturen."

## Stap 7: het antwoord gaat terug

📍 [`ApiServer.java`, regel 88](../src/main/java/ApiServer.java#L88), [regel 93](../src/main/java/ApiServer.java#L93) en [regel 118](../src/main/java/ApiServer.java#L118)

De lijst pakketjes is klaar. De methode `handle` stuurt hem terug naar de browser.
Alle endpoints gebruiken deze ene methode, dus de belangrijke dingen staan op één plek.

**a) De CORS-regels.** De pagina draait op poort 5173 en de server op poort 8080. Voor de browser zijn dat twee verschillende plekken, en dan is hij streng. De server moet dus zeggen dat de pagina mag meeluisteren:

```java
ex.getResponseHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);          // http://localhost:5173
ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
```

**b) De OPTIONS-vraag.** Voordat de browser een POST, PUT of DELETE verstuurt, vraagt hij eerst: "mag dit eigenlijk?". Dat is een `OPTIONS`-bericht. Wij antwoorden 204: "ja hoor, zonder verdere inhoud".

```java
if ("OPTIONS".equals(ex.getRequestMethod())) {
    ex.sendResponseHeaders(204, -1);
    ex.close();
    return;
}
```

**c) Gelukt of mislukt?** De server kiest een statuscode. Gaat het goed, dan is dat 200 (of 201 bij een POST). Weigert de database iets, dan wordt het 400 met een melding.

**d) JSON maken en versturen.** De lijst pakketjes wordt tekst:

```java
byte[] bytes = JSON.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
ex.sendResponseHeaders(status, bytes.length);
ex.getResponseBody().write(bytes);
```

Het antwoord is nu zo'n tekst als in het JSON-voorbeeld hierboven, maar dan een lijst met alle personen.

**In gewone taal:** "Zeg tegen de browser dat de pagina mag meeluisteren, maak van de lijst tekst en stuur die terug met status 200."

## Stap 8: de pagina tekent de boom

📍 [`frontend/src/App.jsx`, regel 155](../frontend/src/App.jsx#L155) en [regel 159](../frontend/src/App.jsx#L159)

Terug in de browser. In stap 3 pakte `response.json()` de tekst uit. In stap 2 zette `setPersons(...)` de lijst in het geheugen.
Omdat het geheugen veranderd is, tekent React de pagina opnieuw, nu met de personen erin.

```jsx
const byId = new Map(persons.map((p) => [p.id, p]))   // opzoektabel: byId.get(4) geeft persoon 4

const roots = persons.filter((p) => {                 // wie staat bovenaan de boom?
  if (p.motherId || p.fatherId) return false          // iemand met ouders staat onder zijn ouders
  /* ... */
})
```

Daarna tekenen `PersonNode` en `PersonRow` de boom. `PersonNode` roept zichzelf aan voor de kinderen, zodat de boom zo diep kan zijn als nodig.
Dat heet **recursie**.

**In gewone taal:** "Nieuwe lijst in het geheugen? Teken de pagina opnieuw."

**Klaar met deel 1.** Het hele pad was:

```text
main.jsx → App.jsx (load) → api.js (fetch) ──HTTP──> ApiServer (persons)
   → PersonRepository (findAll) → Database (connect) → PostgreSQL
   ← JSON ← ApiServer (handle) ← ... ← App.jsx (setPersons) → scherm
```

---

# Deel 2: iemand toevoegen

Nu de andere kant op: gegevens van de pagina naar de database. Je klikt bij Jerôme op `+ partner`, vult een naam in en drukt op Opslaan.

## Stap 9: de frontend stuurt een nieuwe persoon

📍 [`frontend/src/App.jsx`, regel 177](../frontend/src/App.jsx#L177)

Het formulier geeft de ingevulde waarden door aan `create`. Dat doet afhankelijk van de knop iets anders:

```jsx
const create = (type, person, f) => run(async () => {
  if (type === 'pet') return addPet({ name: f.name, species: f.species, age: 0, ownerId: person.id })
  const existing = byId.get(Number(f.existing))        // is er een bestaande persoon gekozen?
  const base = { name: f.name, lastName: f.lastName, sex: f.sex, age: Number(f.age || 0) }
  /* ... kind ... */
  const target = existing?.id ?? (await addPerson(base)).id   // eerst de nieuwe persoon opslaan...
  if (type !== 'root') await updatePerson(person.id, { ...person, [type]: target })  // ...dan koppelen
})
```

Voor een partner gebeuren er twee dingen achter elkaar:

1. `addPerson(base)` stuurt een **POST** met de nieuwe persoon. De backend antwoordt met het nieuwe `id`.
2. `updatePerson(...)` stuurt een **PUT** naar Jerôme met het veld `partnerId` ingevuld. De drie puntjes `...person` betekenen: neem alles over wat er al was, en overschrijf alleen dit ene veld.

Beide gaan door `request` in `api.js`. Dit keer met een `body`:

```text
POST /api/persons HTTP/1.1
Content-Type: application/json

{"name":"Anna","lastName":"Jansen","sex":"F","age":0}
```

Als de aanvraag klaar is, sluit `run` het formulier en roept hij `load()` opnieuw aan. De pagina haalt de lijst dus opnieuw op (stap 2 t/m 8), en je ziet de nieuwe persoon.

## Stap 10: de backend bewaart de persoon

📍 [`ApiServer.java`, regel 42](../src/main/java/ApiServer.java#L42), [`PersonRepository.java`, regel 106](../src/main/java/PersonRepository.java#L106) en [regel 189](../src/main/java/PersonRepository.java#L189)

De server ziet een `POST`. Hij leest de JSON uit het bericht en maakt er een Java-object van (`NewPerson`), en roept `REPO.insert(...)` aan:

```java
String sql = "INSERT INTO persons (name, middle_name, last_name, sex, age, mother_id, father_id) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
```

- De vraagtekens zijn gaatjes die daarna worden ingevuld. Zo kan niemand een stukje SQL in een naamveld typen om de database te saboteren. Dat heet SQL-injectie en dit is de standaardmanier om het te voorkomen.
- `RETURNING id` geeft het nieuwe id terug. Dat stuurt de server terug naar de frontend, die het nodig heeft voor de PUT.
- Het geheel gebeurt in een **transactie**: lukt er iets niet, dan wordt alles teruggedraaid. Je hebt dan nooit een half opgeslagen persoon.

Een partnerschap staat in de database bij **allebei**: Otto wijst naar Judith en Judith wijst naar Otto. De methode `setPartner` maakt eerst oude koppelingen los en bewaart dan beide kanten.

De database bewaakt zelf ook de regels. Probeer je `sex = 'Q'`, dan weigert hij dat door de `CHECK`-regel in [`db/01_schema.sql`](../db/01_schema.sql). De backend stuurt die melding met status 400 terug, en de frontend toont hem in het rood.

---

## Waarom werkt het niet? De vier gewone fouten

| Wat je ziet | Meestal betekent het | Wat je doet |
| --- | --- | --- |
| Rode melding "API niet bereikbaar" | De Java-server draait niet | Start hem: `mvn compile exec:java` |
| In de browserconsole: "blocked by CORS policy" | De pagina draait op een andere poort dan `CORS_ORIGIN` | Zet `CORS_ORIGIN` op het adres van de pagina |
| Melding "No suitable driver found" | De PostgreSQL-vertaler wordt niet geladen | Gebruik `mvn compile exec:java`, niet alleen `exec:java` |
| Melding "password authentication failed" | Verkeerd wachtwoord voor de database | Controleer `DB_PASSWORD` |

---

## Zelf kijken hoe het gaat

De beste manier om dit te snappen, is het bericht zelf zien.

**1. In de browser.** Open de pagina, druk op `F12` en ga naar het tabblad **Network**. Ververs de pagina.
Klik op `persons`. Je ziet de aanvraag (`GET`), de statuscode (`200`) en het antwoord (de JSON-lijst). Dit zijn precies stap 3, 4 en 7.

**2. Zonder frontend.** Je kunt de keuken ook zonder ober aanspreken. Start de server en doe:

```bash
curl http://localhost:8080/api/persons
```

Je krijgt dezelfde JSON als de pagina. Dat bewijst dat de frontend en de backend echt los van elkaar staan.

**3. Kijk in de database.** Bevestig dat de gegevens daar echt staan:

```bash
psql -U familytree_app -p 5433 -d familytree -c "select id, name, mother_id, partner_id from persons"
```

**4. Probeer iets kapot te maken.** Zet in `App.jsx` de `LABELS`-naam `motherId` even op iets anders, of stop de Java-server en ververs. Kijk wat er gebeurt en lees dan de tabel met fouten hierboven.
