import jakarta.persistence.*;

// Definiert die JPA-Entity für Geisternetze
@Entity // Markiert die Klasse als JPA-Entity
@Table(name = "Geisternetz") // Setzt den Tabellennamen in der Datenbank
public class Geisternetz {
    @Id // Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Inkrement-ID
    private int id;

    @Column(nullable = false) // Breitengrad (Latitude)
    private double latitude;

    @Column(nullable = false) // Längengrad (Longitude)
    private double longitude;

    @Column(nullable = false) // Größe des Geisternetzes
    private float size;

    @Enumerated(EnumType.STRING) // Status wird als String gespeichert
    @Column(nullable = false)
    private Status status;

    @ManyToOne // Beziehung zur meldenden Person
    @JoinColumn(name = "meldende_person_id", referencedColumnName = "id")
    private Person meldendePerson;

    @ManyToOne // Beziehung zur bergenden Person
    @JoinColumn(name = "bergende_person_id", referencedColumnName = "id")
    private Person bergendePerson;

    // Status-Enum für das Geisternetz
    public enum Status {
        GEMELDET,             // Netz wurde gemeldet
        BERGUNG_BEVORSTEHEND, // Bergung geplant
        GEBORGEN,             // Netz geborgen
        VERSCHOLLEN           // Netz nicht auffindbar
    }

    // Konstruktoren:
    public Geisternetz() {  // Standardkonstruktor

    }

    public Geisternetz(double latitude, double longitude, float size, Status status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.size = size;
        this.status = status;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Person getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(Person meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public Person getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(Person bergendePerson) {
        this.bergendePerson = bergendePerson;
    }

}
