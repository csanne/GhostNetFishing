import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

// Datenzugriffsobjekt (DAO) verwaltet alle Datenbank-Operationen für Person-Entitäten
@ApplicationScoped
public class PersonDAO {
    //EntityManagerFactory (Datenbank-Verbindung)
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("GhostNetFishingPU");

    // --Neuen Eintrag speichern--

    // Speichert eine neue Person in der Datenbank.
    public void speichern(Person person) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(person);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    // --Daten abfragen--

    // Findet eine Person anhand ihrer ID.
    public Person findePerson(int id) {
        EntityManager entityManager = emf.createEntityManager();
        Person person = entityManager.find(Person.class, id);
        entityManager.close();
        return person;
    }

    // Findet eine Person anhand von Name und Telefonnummer oder erstellt eine neue, falls keine existiert.
    public Person findeOderErstellePerson(String name, String telefonnummer) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            Person person = entityManager.createQuery(
                            "SELECT p FROM Person p WHERE p.name = :name AND p.telefonnummer = :telefonnummer", Person.class)
                    .setParameter("name", name)
                    .setParameter("telefonnummer", telefonnummer)
                    .getSingleResult();
            entityManager.close();
            return person;
        } catch (NoResultException e) {
            entityManager.getTransaction().begin();
            Person neuePerson = new Person(name, telefonnummer);
            entityManager.persist(neuePerson);
            entityManager.getTransaction().commit();
            entityManager.close();
            return neuePerson;
        }
    }
}
