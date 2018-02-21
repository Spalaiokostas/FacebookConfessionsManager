/* 
 * Copyright (C) 2018 Spyros Palaiokostas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package confessionsManager.dao;

import confessionsManager.HibernateUtil;
import confessionsManager.model.Confession;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class ConfessionDAO {

    private final SessionFactory sessionFactory;

    public ConfessionDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Long saveConfession(Confession confession) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Long confessionID = null;
        if (!confession.isProcessed() && confession.isPublished()) {
            //report error 
            return confessionID;
        }
        try {
            tx = session.beginTransaction();
            confessionID = (Long) session.save(confession);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
        return confessionID;
    }

    //overload method for optional parameters
    public List<Confession> getUnprocessedConfessions(Integer start, Integer limit) {
        return fetchConfessions(false, false, start, limit);
    }

    public List<Confession> getPublishedConfessions() {
        return fetchConfessions(true, true, null, null);
    }

    public List<Confession> geRejectedConfessions() {
        return fetchConfessions(true, false, null, null);
    }

    public List<Confession> getAllConfessions() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        List<Confession> confession = null;
        try {
            tx = session.beginTransaction();
            confession = session.createQuery("from Response").list();
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
        return confession;
    }

    private List<Confession> fetchConfessions(boolean processedStatus, boolean publishedStatus, Integer first, Integer count) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        List<Confession> confessions = null;
        if (!processedStatus && publishedStatus) {
            // report error
            return confessions;
        }
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery("from Response where processed =:processstatus and published =:publishedstatus");
            query.setParameter("processstatus", processedStatus);
            query.setParameter("publishedstatus", publishedStatus);
            
            /*if (first != null) {
                query.setFirstResult(first);
            }
            if (count != null) {
                query.setMaxResults(count);
            }*/
            
            confessions = query.list();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
        return confessions;
    }

    public Confession getConfessionById(long responseId) {
        Session session = sessionFactory.openSession();
        Confession confession = null;
        try {
            confession = (Confession) session.get(Confession.class, responseId);
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
        return confession;
    }

    public void updateToPosted(long confessionId) {
        processConfessionState(confessionId, true);
    }

    public void updateToRejected(long confessionId) {
        processConfessionState(confessionId, false);
    }

    private void processConfessionState(long confessionId, boolean state) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Confession confession = (Confession) session.get(Confession.class, confessionId);
            confession.setProcessed(true);
            confession.setPublished(state);
            session.update(confession);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
    }

    public void deleteResponse(Long responseID) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Confession response = (Confession) session.get(Confession.class, responseID);
            session.delete(response);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close();
            }
        }
    }
}
