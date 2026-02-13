package utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.function.Supplier;

@ApplicationScoped
public class TransactionHelper {

    @Inject
    private EntityManager em;

    public <T> T executeInTransaction(Supplier<T> operation) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = operation.get();
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void executeInTransaction(Runnable operation) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            operation.run();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
