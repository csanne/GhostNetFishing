import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

// Datenzugriffsobjekt (DAO) verwaltet alle Datenbank-Operationen für Geisternetz-Entitäten
@ApplicationScoped
public class GeisternetzDAO {
    // EntityManagerFactory (Datenbank-Verbindung)
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("GhostNetFishingPU");

    // --Neuen Eintrag speichern--

    // Speichert ein neues Geisternetz in der Datenbank.
    public void speichern(Geisternetz geisternetz) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(geisternetz);
        entityManager.getTransaction().commit();
        entityManager.close(); // Wichtig: EntityManager schließen!
    }

    // --Daten abfragen--

    // Gibt eine Liste aller Geisternetze zurück.
    public List<Geisternetz> alleGeisternetze() {
        EntityManager entityManager = emf.createEntityManager();
        List<Geisternetz> liste = entityManager.createQuery("SELECT g FROM Geisternetz g", Geisternetz.class)
                .setHint("jakarta.persistence.cache.storeMode", "REFRESH")
                .getResultList();
        entityManager.close();
        return liste;
    }

    // Findet ein Geisternetz anhand seiner ID
    public Geisternetz findeGeisternetz(int id) {
        EntityManager entityManager = emf.createEntityManager();
        Geisternetz netz = entityManager.createQuery(
                        "SELECT g FROM Geisternetz g LEFT JOIN FETCH g.meldendePerson LEFT JOIN FETCH g.bergendePerson WHERE g.id = :id",
                        Geisternetz.class)
                .setParameter("id", id)
                .setHint("jakarta.persistence.cache.storeMode", "REFRESH")
                .getSingleResult();
        entityManager.close();
        return netz;
    }

    // --Bestehenden Eintrag aktualisieren--

    // Aktualisiert die Daten eines bestehenden Geisternetzes.
    public void aktualisieren(Geisternetz geisternetz) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(geisternetz);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    // --Eintrag löschen--

    // Löscht ein Geisternetz aus der Datenbank.
    public void loeschen(Geisternetz geisternetz) {
        if (geisternetz == null) {
            return;
        }
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Geisternetz zuLoeschen = entityManager.find(Geisternetz.class, geisternetz.getId());
        if (zuLoeschen != null) {
            entityManager.remove(zuLoeschen);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
