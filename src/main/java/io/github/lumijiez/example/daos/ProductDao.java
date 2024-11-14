package io.github.lumijiez.example.daos;

import io.github.lumijiez.example.models.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class ProductDao {

    public void saveProduct(Product product) {
        Transaction transaction = null;
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(product);
            transaction.commit();
            System.out.println("Product saved successfully!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Product getProductById(int id) {
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            return session.get(Product.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Product> getAllProducts() {
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            return session.createQuery("from Product").list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateProduct(Product product) {
        Transaction transaction = null;
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(product);
            transaction.commit();
            System.out.println("Product updated successfully!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        Transaction transaction = null;
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
                System.out.println("Product deleted successfully!");
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Product> getProductsByPage(int pageNumber, int pageSize) {
        try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
            Query<Product> query = session.createQuery("from Product", Product.class);

            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
