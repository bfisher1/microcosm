package util;

import world.block.Block;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class DbClient {
    public static final EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("Microcosm");

    public static EntityManager manager = managerFactory.createEntityManager();

    public static void save(Object object) {
        try {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(object);
            transaction.commit();
        }
        catch (Exception e) {
            throw e;
        }

    }

    public static <T> T find(Class<T> type, long id) {
        return manager.find(type, id);
    }

    // TODO, use real database management
    // TODO, Don't use SQL injection!
    public static <T> Collection<T> findAll(Class<T> type) {
        return findAllWhere(type, "");
    }

    public static <T> Collection<T> findAllWhere(Class<T> type, String criteria) {
        Query query = manager.createQuery("SELECT e FROM " + type.getName() + " e " + criteria);
        return (Collection<T>) query.getResultList();
    }

    public static <T> void delete(Class<T> type, long id) {
        T object = manager.find(type, id);

        manager.getTransaction().begin();
        manager.remove(object);
        manager.getTransaction().commit();
    }

    public static <T> void deleteAll(Class<T> type) {
        Collection<T> objects = findAll(type);
        manager.getTransaction().begin();
        objects.forEach(object -> {
            manager.remove(object);
        });
        manager.getTransaction().commit();
    }


// TODO: implement
//    public void update(Object, long id) {
//        //
//    }

    public static void saveList(Collection<Object> objects) {
        try {
            EntityManager manager = managerFactory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            objects.forEach(object -> {
                manager.persist(object);
            });
            transaction.commit();
        }
        catch (Exception e) {
            throw e;
        }
    }


    public static void saveBlocks(List<Block> blocks) {
        try {
            EntityManager manager = managerFactory.createEntityManager();
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            blocks.forEach(block -> {
                while(block != null) {
                    manager.persist(block);
                    block = block.getAbove();
                }
            });
            transaction.commit();
        }
        catch (Exception e) {
            throw e;
        }
    }
}
