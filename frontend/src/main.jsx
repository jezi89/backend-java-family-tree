import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// STAP 1 (zie docs/frontend-backend-uitleg.md): dit is het startpunt van de frontend.
// index.html bevat een lege <div id="root">. React zoekt die div en tekent daarin onze hele app (<App />).
// StrictMode is een hulpmiddel tijdens het ontwikkelen: het draait sommige code twee keer om fouten sneller te vinden.
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
