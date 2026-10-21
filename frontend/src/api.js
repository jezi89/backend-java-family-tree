// Dit bestand is de "postbode" van de frontend: hier staat ALLE code die met de backend praat.
// De rest van de app (App.jsx) roept alleen deze kleine functies aan en hoeft niet te weten hoe HTTP werkt.

// Het adres van de backend. import.meta.env.VITE_API_URL is een instelling die je buiten de code kunt zetten
// (handig als de backend later op een andere plek draait). `??` betekent: "als die er niet is, gebruik dan dit".
const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

// STAP 3 (zie docs/frontend-backend-uitleg.md): de ene functie die ELKE aanvraag naar de backend verstuurt.
// path   = welk deel van de API we willen, bijvoorbeeld '/api/persons'
// method = wat we willen doen: GET (ophalen), POST (nieuw maken), PUT (aanpassen), DELETE (weghalen)
// body   = de gegevens die we meesturen (alleen bij POST en PUT)
async function request(path, method = 'GET', body) {
  // fetch stuurt een HTTP-bericht over het netwerk. `await` wacht tot het antwoord er is, zonder de pagina te bevriezen.
  const response = await fetch(`${API_URL}${path}`, {
    method,
    // De header vertelt de backend dat we JSON sturen. Zonder body is er niets om uit te leggen, dus dan weglaten.
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    // Een JavaScript-object kan niet zomaar over het netwerk: JSON.stringify maakt er tekst van.
    body: body ? JSON.stringify(body) : undefined,
  })
  // De backend stuurt tekst terug die JSON is. response.json() maakt daar weer een JavaScript-object van.
  const json = await response.json()
  // response.ok is false bij een foutcode zoals 400 of 500. Dan gooien we een fout met de melding van de backend,
  // zodat App.jsx die in een try/catch kan opvangen en op het scherm kan tonen.
  if (!response.ok) throw new Error(json.error ?? `HTTP ${response.status}`)
  return json
}

// Hieronder de functies die App.jsx echt gebruikt. Elke functie is één regel: hij kiest alleen pad, methode en gegevens.
// De `export` ervoor maakt ze beschikbaar in andere bestanden.
export const getPersons = () => request('/api/persons') // alle personen ophalen
export const addPerson = (person) => request('/api/persons', 'POST', person) // nieuwe persoon opslaan
export const updatePerson = (id, person) => request(`/api/persons/${id}`, 'PUT', person) // persoon aanpassen
export const deletePerson = (id) => request(`/api/persons/${id}`, 'DELETE') // persoon verwijderen
export const addPet = (pet) => request('/api/pets', 'POST', pet) // huisdier opslaan
export const deletePet = (id) => request(`/api/pets/${id}`, 'DELETE') // huisdier verwijderen
