package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class DbClient {
    public static final EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("Microcosm");

    public static void save(Object object) {
        try {
            EntityManager manager = managerFactory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(object);
            transaction.commit();
            manager.close();
        }
        catch (Exception e) {
            throw e;
        }

    }

    public static void saveList(List<Object> objects) {
        EntityManager manager = managerFactory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        objects.forEach(object -> {
            manager.persist(object);
        });
        transaction.commit();
        //manager.close();
    }




}
