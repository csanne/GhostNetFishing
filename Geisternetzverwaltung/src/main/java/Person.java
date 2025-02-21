import jakarta.persistence.*;

// Definiert die JPA-Entity für Personen

@Entity // Markiert die Klasse als JPA-Entity
@Table(name = "Person") // Setzt den Tabellennamen in der Datenbank
public class Person {
    // Attribute
    @Id // Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-Inkrement-ID
    private int id;

    @Column(nullable = false) // Name der Person
    private String name;

    @Column(length = 50) // Telefonnummer (max. 50 Zeichen)
    private String telefonnummer;

    // Konstruktoren
    public Person() { // Standardkonstruktor
    }

    public Person(String name, String telefonnummer) {
        this.name = name;
        this.telefonnummer = telefonnummer;
    }

    // Getter & Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }
}
