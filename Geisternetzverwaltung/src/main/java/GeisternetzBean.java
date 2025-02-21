import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

// Bean für Geisternetz-Funktionen.
@Named
@ViewScoped
public class GeisternetzBean implements Serializable {
    // Attribute
    @Inject private GeisternetzDAO geisternetzDAO;
    @Inject private PersonDAO personDAO;

    private Geisternetz geisternetz = new Geisternetz();    // Aktuelle Meldung
    private List<Geisternetz> geisternetzListe;             // Liste aller Netze
    private Geisternetz selectedNetz;                       // Ausgewähltes Netz
    private int selectedNetzId;                             // ID des gewählten Netzes

    // Daten für Meldende/Bergende Personen
    private String meldenderName;
    private String meldendeTelefonnummer;
    private String bergenderName;
    private String bergendeTelefonnummer;

    // Admin-Daten für Berechtigungsprüfung
    private final String adminName = "AdminGhostNet";
    private final String adminTelefonnummer = "112233445566778899";

    // Getter & Setter
    public List<Geisternetz> getGeisternetze() {
        return geisternetzListe = geisternetzDAO.alleGeisternetze();
    }

    public double getLatitude() { return geisternetz.getLatitude(); }
    public void setLatitude(double latitude) { geisternetz.setLatitude(latitude); }

    public double getLongitude() { return geisternetz.getLongitude(); }
    public void setLongitude(double longitude) { geisternetz.setLongitude(longitude); }

    public float getSize() { return geisternetz.getSize(); }
    public void setSize(float size) { geisternetz.setSize(size); }

    public String getMeldenderName() { return meldenderName; }
    public void setMeldenderName(String meldenderName) { this.meldenderName = meldenderName; }

    public String getMeldendeTelefonnummer() { return meldendeTelefonnummer; }
    public void setMeldendeTelefonnummer(String telefonnummer) { this.meldendeTelefonnummer = telefonnummer; }

    public String getBergenderName() { return bergenderName; }
    public void setBergenderName(String bergenderName) { this.bergenderName = bergenderName; }

    public String getBergendeTelefonnummer() { return bergendeTelefonnummer; }
    public void setBergendeTelefonnummer(String telefonnummer) { this.bergendeTelefonnummer = telefonnummer; }

    public Geisternetz getSelectedNetz() { return selectedNetz; }

    // --Methoden zur Erstellung und Löschung von Geisternetzen--

    // Erstellt eine neue Meldung für ein Geisternetz.
    public void melden() {
        Person meldendePerson = null;

        // Falls kein Name angegeben wurde, speichere als "Anonym"
        if (meldenderName != null && !meldenderName.isEmpty()) {
            meldendePerson = new Person(meldenderName, meldendeTelefonnummer);
            personDAO.speichern(meldendePerson);
        }
        // Netz speichern
        geisternetz.setMeldendePerson(meldendePerson);
        geisternetz.setStatus(Geisternetz.Status.GEMELDET);
        geisternetzDAO.speichern(geisternetz);

        // Felder zurücksetzen
        meldenderName = "";
        meldendeTelefonnummer = "";
        geisternetz = new Geisternetz(); // Neues Geisternetz für die nächste Meldung
    }

    // Löscht ein Geisternetz (nur für Admin).
    public void geisternetzLoeschen() {
        if (!getIstAdmin()) {
            System.out.println("Zugriff verweigert: Kein Admin!");
            return;
        }

        if (selectedNetz == null) {
            System.out.println("Kein Geisternetz ausgewählt!");
            return;
        }

        geisternetzDAO.loeschen(selectedNetz);
        selectedNetz = null;
        System.out.println("Geisternetz wurde von Admin gelöscht!");
    }

    // --Status-Änderungen--

    // Startet die Bergung für ein ausgewähltes Netz.
    public void startBergung() {
        if (selectedNetz == null) {
            selectedNetz = geisternetzDAO.findeGeisternetz(selectedNetzId);
            if (selectedNetz == null) {
                System.out.println("Kein Geisternetz mit dieser ID gefunden!");
                return;
            }
        }

        System.out.println("Geisternetz gefunden: ID " + selectedNetz.getId() + ", Status = " + selectedNetz.getStatus());
        // Prüfen ob Bergende-Daten vorhanden sind
        if (bergenderName == null || bergenderName.isEmpty() || bergendeTelefonnummer == null || bergendeTelefonnummer.isEmpty()) {
            System.out.println("Name & Telefonnummer müssen angegeben werden!");
            return;
        }
        // Bergende Person speichern
        Person bergendePerson = new Person(bergenderName, bergendeTelefonnummer);
        personDAO.speichern(bergendePerson);
        System.out.println("Bergende Person gespeichert: " + bergendePerson.getName());
        // Datenbank-Update
        selectedNetz.setBergendePerson(bergendePerson);
        selectedNetz.setStatus(Geisternetz.Status.BERGUNG_BEVORSTEHEND);
        System.out.println("Status auf BERGUNG_BEVORSTEHEND gesetzt.");

        geisternetzDAO.aktualisieren(selectedNetz); // Speichern in der DB

        // Aktualisiertes Netz erneut laden
        selectedNetz = geisternetzDAO.findeGeisternetz(selectedNetz.getId());
        System.out.println("Geisternetz nach Update geladen: Status = " + selectedNetz.getStatus());
    }

    // Markiert die Bergung als abgeschlossen.
    public void bergungAbschliessen() {
        if (selectedNetz == null) {
            System.out.println("Kein Geisternetz ausgewählt!");
            return;
        }

        selectedNetz.setStatus(Geisternetz.Status.GEBORGEN);
        geisternetzDAO.aktualisieren(selectedNetz);

        // Aktualisiertes Netz erneut laden
        selectedNetz = geisternetzDAO.findeGeisternetz(selectedNetz.getId());

        System.out.println("Bergung für Netz ID " + selectedNetz.getId() + " abgeschlossen!");
    }

    // Meldet ein Netz als verschollen (nur für Berechtigte).
    public void verschollenMelden() {
        if (!getIstBerechtigt()) {
            System.out.println("Nur der Bergende kann das Netz als VERSCHOLLEN melden!");
            return;
        }

        selectedNetz.setStatus(Geisternetz.Status.VERSCHOLLEN);
        geisternetzDAO.aktualisieren(selectedNetz);
        aktualisieren();
    }

    // Bricht eine laufende Bergung ab (nur für Berechtigte).
    public void bergungAbbrechen() {
        if (!getIstBerechtigt()) {
            System.out.println("Nur der Bergende kann die Bergung abbrechen!");
            return;
        }

        selectedNetz.setBergendePerson(null);
        selectedNetz.setStatus(Geisternetz.Status.GEMELDET);
        geisternetzDAO.aktualisieren(selectedNetz);
        aktualisieren();
    }

    // Hilfsmethoden & Navigation

    // Aktualisiert die Liste der Geisternetze.
    public void aktualisieren() {
        System.out.println("Aktualisiere die Geisternetz-Liste.");
        geisternetzListe = null; // Setzt die Liste zurück, damit sie neu geladen wird
    }

    // Lädt das ausgewählte Geisternetz basierend auf der URL-ID
    public void loadNetz() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String idParam = params.get("id");

        if (idParam != null) {
            try {
                selectedNetzId = Integer.parseInt(idParam);
                System.out.println("Lade Geisternetz mit ID: " + selectedNetzId);
                selectedNetz = geisternetzDAO.findeGeisternetz(selectedNetzId);

                if (selectedNetz == null) {
                    System.out.println("Kein Geisternetz mit ID " + selectedNetzId + " gefunden!");
                } else {
                    System.out.println("Geisternetz erfolgreich geladen: " + selectedNetz.getLatitude() + ", " + selectedNetz.getLongitude());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige ID in der URL: " + idParam);
            }
        } else {
            System.out.println("Keine ID in der URL gefunden!");
        }
    }

    // Öffnet die Bergungsseite mit der Netz-ID.
    public void oeffneBergung(int id) {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("bergung.xhtml?faces-redirect=true&id=" + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Berechtigungsprüfungen

    // Prüft, ob aktueller Nutzer berechtigt ist (Admin oder Bergender).
    public boolean getIstBerechtigt() {
        if (selectedNetz == null) {
            return false;
        }
        // Admin hat immer Berechtigung
        if (getIstAdmin()) {
            return true;
        }
        // Normale Berechtigungsprüfung für den Bergenden
        return selectedNetz.getBergendePerson() != null &&
                selectedNetz.getBergendePerson().getName().equals(bergenderName) &&
                selectedNetz.getBergendePerson().getTelefonnummer().equals(bergendeTelefonnummer);
    }

    // Prüft, ob aktueller Nutzer Admin ist.
    public boolean getIstAdmin() {
        return adminName.equals(bergenderName) && adminTelefonnummer.equals(bergendeTelefonnummer);
    }

    // Testet die aktuelle Berechtigung und loggt das Ergebnis.
    public void pruefeBerechtigung() {
        // Debugging-Ausgabe für Admin-Check
        System.out.println("Live-Prüfung Berechtigung: " + (getIstBerechtigt() ? "JA" : "NEIN"));
    }

    // Setzt den Status eines Netzes (nur Admin).
    public void statusManuellSetzen() {
        if (!getIstAdmin()) {
            System.out.println("Zugriff verweigert: Kein Admin!");
            return;
        }

        try {
            geisternetzDAO.aktualisieren(selectedNetz);
            System.out.println("Admin hat den Status auf " + selectedNetz.getStatus() + " geändert.");
        } catch (Exception e) {
            System.out.println("Fehler beim Setzen des Status: " + e.getMessage());
        }
    }

    // Name und Telefonnummer aus bestehenden Daten übernehmen zwecks Usability.
    public void uebernehmePersonDaten(boolean istMeldendePerson) {
        Person person = istMeldendePerson
                ? selectedNetz.getMeldendePerson()
                : selectedNetz.getBergendePerson();

        if (person != null) {
            bergenderName = person.getName();
            bergendeTelefonnummer = person.getTelefonnummer();
            String typ = istMeldendePerson ? "Meldender" : "Bergender";
            System.out.println(typ + " übernommen: " + bergenderName + ", " + bergendeTelefonnummer);
        } else {
            System.out.println("Keine " + (istMeldendePerson ? "Meldende" : "Bergende") + " Person vorhanden.");
        }
    }

    public void datenDesMeldersUebernehmen() {
        uebernehmePersonDaten(true);
    }

    public void datenDesBergendenUebernehmen() {
        uebernehmePersonDaten(false);
    }
}
