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
import confessionsManager.model.HashTag;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import static org.hibernate.search.Search.getFullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;


public class HashTagDAO {
    
    private final SessionFactory sessionFactory;
    
    public HashTagDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    public Integer saveHashTag(HashTag hashtag) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Integer hashtagID = null;
        try{
            tx = session.beginTransaction();
            hashtagID = (Integer) session.save(hashtag); 
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        }finally {
            session.close(); 
        }
        return hashtagID;
    }
     
    public HashTag getHashTagByID(int hashtagID) {
        Session session = sessionFactory.openSession();
        HashTag hashtag = null;
        try {
            hashtag = (HashTag)session.get(HashTag.class, hashtagID);
        }catch (HibernateException e) {
            e.printStackTrace(); 
        }finally {
            if ((session != null && session.isOpen())) {
                session.close(); 
            }
        }
        return  hashtag;
    }
    
    public List<HashTag> listHashTags() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        List<HashTag> hashtags = null;
        try{
            tx = session.beginTransaction();
            hashtags = session.createQuery("FROM hashtags").list();
            tx.commit();
        }catch (HibernateException e) {
            e.printStackTrace(); 
        }finally {
             if ((session != null && session.isOpen())) {
                session.close(); 
            }
        }
        return hashtags;
    }
    
    public void updateHashTagUsageCounter(int hashtagID, int counter) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            HashTag hashtag = (HashTag)session.get(HashTag.class, hashtagID);
            hashtag.updateUsageCounter();
            session.update(hashtag);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        }finally {
            if ((session != null && session.isOpen())) {
                session.close(); 
            }
        }
    }
    
    public void updateHashTagName(int hashtagID, String name) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            HashTag hashtag = (HashTag)session.get(HashTag.class, hashtagID);
            hashtag.setHashTagName(name);
            session.update(hashtag);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        }finally {
            if ((session != null && session.isOpen())) {
                session.close(); 
            } 
        }
    }
    
    public List<HashTag> autocompleteHashTag(String token) {
        Session session = sessionFactory.openSession();
        List<HashTag> hashtagList = null;
        try {
            FullTextSession fullTextSession = getFullTextSession(session);
            Transaction tx = fullTextSession.beginTransaction();
            QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(HashTag.class).get();
            org.apache.lucene.search.Query query = qb.keyword().onFields("hashtag_name").matching(token).createQuery();
            org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, HashTag.class);
            hashtagList = hibQuery.list();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return hashtagList;
    }
    
    public void deleteHashTag(int hashTagID) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            HashTag hashtag = (HashTag)session.get(HashTag.class, hashTagID);
            session.delete(hashtag);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            if ((session != null && session.isOpen())) {
                session.close(); 
            }
        }
    }
}
