import { useCallback, useEffect, useState } from 'react'
import { addPerson, addPet, deletePerson, deletePet, getPersons, updatePerson } from './api'
import './App.css'

// Maakt van een persoon één mooie tekst: voornaam + tussenvoegsel + achternaam. filter(Boolean) laat lege delen weg.
const fullName = (p) => [p.name, p.middleName, p.lastName].filter(Boolean).join(' ')

// Bij elke knop hoort een "type": wat voegen we toe aan de persoon? Dit zijn de teksten die op het scherm komen.
// motherId, fatherId en partnerId heten bewust hetzelfde als de velden van een persoon, zodat we ze verderop
// direct als veldnaam kunnen gebruiken (zie `[type]: target` in create).
const LABELS = { motherId: 'moeder', fatherId: 'vader', partnerId: 'partner', child: 'kind', pet: 'huisdier', root: 'persoon' }

// Het kleine formulier dat opent als je op "+ kind", "+ partner" enzovoort klikt.
// Het formulier bewaart zelf niets: bij "Opslaan" geeft het de ingevulde waarden door via onSubmit.
function AddForm({ type, person, persons, onSubmit, onCancel }) {
  // Handige beginwaarden: een nieuwe moeder is standaard een vrouw, een partner is standaard het andere geslacht.
  const defaultSex = { motherId: 'F', fatherId: 'M', partnerId: person?.sex === 'F' ? 'M' : 'F' }[type] ?? 'F'
  // Een kind krijgt standaard de achternaam van de vader.
  const lastName = type === 'child' ? (person.sex === 'M' ? person.lastName : '') : ''
  const submit = (e) => {
    e.preventDefault() // voorkomt dat de browser de hele pagina herlaadt bij het versturen van een formulier
    // FormData leest alle ingevulde velden uit; fromEntries maakt er een object van, bijvoorbeeld { name: 'Anna', sex: 'F' }.
    onSubmit(Object.fromEntries(new FormData(e.target)))
  }
  return (
    <form onSubmit={submit}>
      {type === 'pet' ? (
        <>
          <input name="name" placeholder="Naam" required autoFocus />
          <input name="species" placeholder="Soort (hond, kat...)" required />
        </>
      ) : (
        <>
          <input name="name" placeholder="Voornaam" autoFocus />
          <input name="lastName" placeholder="Achternaam" defaultValue={lastName} />
          <select name="sex" defaultValue={defaultSex}>
            <option value="F">Vrouw</option>
            <option value="M">Man</option>
          </select>
          <input name="age" type="number" min="0" placeholder="Leeftijd (optioneel)" />
          {/* Liever iemand koppelen die al bestaat? Kies hem hier. Dan maken we geen nieuwe persoon. */}
          {type !== 'root' && (
            <select name="existing" defaultValue="">
              <option value="">of koppel bestaande...</option>
              {persons.filter((p) => p.id !== person.id).map((p) => <option key={p.id} value={p.id}>{fullName(p)}</option>)}
            </select>
          )}
        </>
      )}
      <button type="submit">Opslaan {LABELS[type]}</button>
      <button type="button" onClick={onCancel}>Annuleer</button>
    </form>
  )
}

// Eén regel in de boom: naam, huisdieren, knoppen en (als je op een knop hebt geklikt) het formulier.
// `ctx` is een doos met alle functies en gegevens die de regel nodig heeft, zodat we ze niet één voor één hoeven door te geven.
function PersonRow({ person, ctx }) {
  const { adding, setAdding, remove, removePet, create, persons, selected, setSelected } = ctx
  const add = (type) => setAdding({ type, personId: person.id }) // onthoud: bij DEZE persoon wil ik iets toevoegen
  const open = adding?.personId === person.id // staat het formulier bij deze persoon open?
  // nieces en grandChildrenPets zijn lijsten die de Java-backend voor ons uitrekent (getNieces en getGrandChildrenPets).
  const extra = [
    person.nieces.length > 0 && `nichtjes: ${person.nieces.join(', ')}`,
    person.grandChildrenPets.length > 0 && `dieren van kleinkinderen: ${person.grandChildrenPets.join(', ')}`,
  ].filter(Boolean).join(' · ')

  return (
    <div className="person">
      {/* Klik op een naam om de knoppen van die persoon te tonen of te verbergen. Zo staat "+ huisdier" niet bij iedereen. */}
      <strong className="name" onClick={() => setSelected(selected === person.id ? null : person.id)}>{fullName(person)}</strong> <span>({person.sex}{person.age > 0 && `, ${person.age}`})</span>
      {person.pets.map((pet) => (
        <span className="pet" key={pet.id}>{pet.name} ({pet.species}) <a onClick={() => removePet(pet)}>×</a></span>
      ))}
      {selected === person.id && <span className="actions">
        {/* Een knop tonen we alleen als het zin heeft: wie al een moeder heeft, krijgt geen "+ moeder" */}
        {!person.motherId && <button onClick={() => add('motherId')}>+ moeder</button>}
        {!person.fatherId && <button onClick={() => add('fatherId')}>+ vader</button>}
        {!person.partnerId && <button onClick={() => add('partnerId')}>+ partner</button>}
        <button onClick={() => add('child')}>+ kind</button>
        <button onClick={() => add('pet')}>+ huisdier</button>
        <button onClick={() => remove(person)}>verwijder</button>
      </span>}
      {selected === person.id && extra && <div className="extra">{extra}</div>}
      {open && (
        <AddForm type={adding.type} person={person} persons={persons}
                 onSubmit={(f) => create(adding.type, person, f)} onCancel={() => setAdding(null)} />
      )}
    </div>
  )
}

// Eén "blok" in de boom: de persoon, de partner eronder en daaronder de kinderen.
// Een component die zichzelf aanroept (voor de kinderen) heet recursie: zo kan de boom zo diep zijn als nodig.
function PersonNode({ person, ctx }) {
  const { byId } = ctx
  const partner = byId.get(person.partnerId)
  const couple = [person, partner].filter(Boolean)
  const ids = new Set(couple.map((p) => p.id))
  // Een kind staat maar één keer in de boom: onder de moeder, of onder de vader als de moeder onbekend is.
  // Anders zou een kind bij allebei de ouders verschijnen.
  const children = [...new Set(couple.flatMap((p) => p.childIds))]
    .map((id) => byId.get(id))
    .filter((c) => ids.has(c.motherId ?? c.fatherId))
  const parents = [person.motherId, person.fatherId].filter(Boolean).map((id) => fullName(byId.get(id)))

  return (
    <li>
      <PersonRow person={person} ctx={ctx} />
      {parents.length > 1 && <div className="extra">kind van {parents.join(' & ')}</div>}
      {partner && <div className="partner">♥ <PersonRow person={partner} ctx={ctx} /></div>}
      {children.length > 0 && (
        <ul>{children.map((c) => <PersonNode key={c.id} person={c} ctx={ctx} />)}</ul>
      )}
    </li>
  )
}

export default function App() {
  // useState = het geheugen van de pagina. Verandert zo'n waarde, dan tekent React het scherm opnieuw.
  const [persons, setPersons] = useState([]) // de lijst personen die uit de backend komt
  const [error, setError] = useState('') // een foutmelding om te tonen
  const [adding, setAdding] = useState(null) // welk formulier staat open, bij welke persoon?
  const [selected, setSelected] = useState(null) // van welke persoon zijn de knoppen zichtbaar?

  // STAP 2 (zie docs/frontend-backend-uitleg.md): personen ophalen bij de backend en in het geheugen zetten.
  // useCallback zorgt dat deze functie niet bij elke render opnieuw wordt gemaakt (anders zou useEffect blijven herstarten).
  const load = useCallback(async () => {
    try {
      setPersons(await getPersons()) // praat met de backend via api.js
      setError('')
    } catch (e) {
      // Backend uit, verkeerde poort of een fout in de database? Dan komen we hier terecht.
      setError(`API niet bereikbaar of fout: ${e.message}`)
    }
  }, [])

  // useEffect met [load] erachter draait één keer nadat de pagina voor het eerst getekend is: precies het moment om data op te halen.
  useEffect(() => { load() }, [load])

  // STAP 8 (zie docs/frontend-backend-uitleg.md): het antwoord staat nu in `persons`; hieronder tekenen we de boom. Een opzoektabel: byId.get(4) geeft meteen persoon 4. Sneller en korter dan telkens de hele lijst doorzoeken.
  const byId = new Map(persons.map((p) => [p.id, p]))
  // De bovenste laag van de boom: mensen zonder ouders. Is iemands partner al elders in de boom te zien
  // (omdat die partner ouders heeft), dan tonen we hem daar bij de partner en niet nog eens bovenaan.
  const roots = persons.filter((p) => {
    if (p.motherId || p.fatherId) return false
    const partner = byId.get(p.partnerId)
    return !partner || !(partner.motherId || partner.fatherId || partner.id < p.id)
  })

  // Voert een wijziging uit en daarna: formulier sluiten en de lijst opnieuw ophalen, zodat het scherm klopt met de database.
  async function run(action) {
    try {
      await action()
      setAdding(null)
    } catch (e) {
      setError(e.message)
      return
    }
    await load()
  }

  // STAP 9 (zie docs/frontend-backend-uitleg.md): iets nieuws opslaan. Wat er gebeurt hangt af van het type knop.
  const create = (type, person, f) => run(async () => {
    if (type === 'pet') return addPet({ name: f.name, species: f.species, age: 0, ownerId: person.id })
    // Is er een bestaande persoon gekozen in het keuzemenu? Dan gebruiken we die in plaats van een nieuwe te maken.
    const existing = byId.get(Number(f.existing))
    const base = { name: f.name, lastName: f.lastName, sex: f.sex, age: Number(f.age || 0) }
    if (type === 'child') {
      // Bij een kind zijn de huidige persoon en zijn partner de ouders. Of hij moeder of vader is, hangt van het geslacht af.
      const parents = person.sex === 'F'
        ? { motherId: person.id, fatherId: person.partnerId }
        : { fatherId: person.id, motherId: person.partnerId }
      return existing ? updatePerson(existing.id, { ...existing, ...parents }) : addPerson({ ...base, ...parents })
    }
    // Moeder, vader of partner: eerst de nieuwe persoon opslaan (als er geen bestaande is gekozen)...
    const target = existing?.id ?? (await addPerson(base)).id
    // ...en daarna bij de huidige persoon het juiste veld invullen. `[type]` is hier motherId, fatherId of partnerId.
    // De drie puntjes ...person betekenen: neem alle bestaande velden over en overschrijf alleen dit ene veld.
    if (type !== 'root') await updatePerson(person.id, { ...person, [type]: target })
  })

  // confirm() toont een ja/nee-vraagvenster. Pas bij "ja" gaat && door naar het verwijderen.
  const remove = (p) => confirm(`${fullName(p)} verwijderen?`) && run(() => deletePerson(p.id))
  const removePet = (pet) => run(() => deletePet(pet.id))

  const ctx = { byId, persons, adding, setAdding, selected, setSelected, create, remove, removePet }

  return (
    <main>
      <h1>Family tree</h1>
      {error && <p className="error">{error}</p>}
      <ul className="tree">
        {roots.map((p) => <PersonNode key={p.id} person={p} ctx={ctx} />)}
      </ul>
      <button onClick={() => setAdding({ type: 'root', personId: null })}>+ nieuwe persoon</button>
      {adding?.personId === null && (
        <AddForm type="root" persons={persons} onSubmit={(f) => create('root', null, f)} onCancel={() => setAdding(null)} />
      )}
    </main>
  )
}
