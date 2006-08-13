//$Id: QueryImpl.java 9796 2006-04-26 06:46:52Z epbernard $
package org.hibernate.ejb;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIME;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
 * @author Emmanuel Bernard
 */
public class QueryImpl implements Query, HibernateQuery {
    private org.hibernate.Query query;
    private HibernateEntityManagerImplementor em;

    public QueryImpl(org.hibernate.Query query, AbstractEntityManagerImpl em) {
        this.query = query;
        this.em = em;
    }

    public org.hibernate.Query getHibernateQuery() {
        return query;
    }

    public int executeUpdate() {
        try {
            if ( ! em.isTransactionInProgress() ) {
                em.throwPersistenceException( new TransactionRequiredException( "Executing an update/delete query" ) );
                return 0;
            }
            return query.executeUpdate();
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return 0;
        }
    }

    public List getResultList() {
        try {
            return query.list();
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Object getSingleResult() {
        try {
            Object result = query.uniqueResult();

            if ( result == null ) {
                em.throwPersistenceException( new NoResultException( "No entity found for query" ) );
            }

            return result;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setMaxResults(int maxResult) {
        if ( maxResult < 0 ) {
            throw new IllegalArgumentException(
                    "Negative ("
                            + maxResult
                            + ") parameter passed in to setMaxResults"
            );
        }
        query.setMaxResults( maxResult );
        return this;
    }

    public Query setFirstResult(int firstResult) {
        if ( firstResult < 0 ) {
            throw new IllegalArgumentException(
                    "Negative ("
                            + firstResult
                            + ") parameter passed in to setMaxResults"
            );
        }
        query.setFirstResult( firstResult );
        return this;
    }

    public Query setHint(String hintName, Object value) {
        try {
            if ( "org.hibernate.timeout".equals( hintName ) ) {
                query.setTimeout( (Integer) value );
            }
            else if ( "org.hibernate.comment".equals( hintName ) ) {
                query.setComment( (String) value );
            }
            else if ( "org.hibernate.fetchSize".equals( hintName ) ) {
                query.setFetchSize( (Integer) value );
            }
            else if ( "org.hibernate.cacheRegion".equals( hintName ) ) {
                query.setCacheRegion( (String) value );
            }
            else if ( "org.hibernate.cacheable".equals( hintName ) ) {
                query.setCacheable( (Boolean) value );
            }
            else if ( "org.hibernate.readOnly".equals( hintName ) ) {
                query.setReadOnly( (Boolean) value );
            }
            else if ( "org.hibernate.cacheMode".equals( hintName ) ) {
                query.setCacheMode( (CacheMode) value );
            }
            else if ( "org.hibernate.flushMode".equals( hintName ) ) {
                query.setFlushMode( (FlushMode) value );
            }
            //TODO:
            /*else if ( "org.hibernate.lockMode".equals( hintName ) ) {
                query.setLockMode( alias, lockMode );
            }*/
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException( "Value for hint" );
        }
        return this;
    }

    public Query setParameter(String name, Object value) {
        try {
            if ( value instanceof Collection ) {
                query.setParameterList( name, (Collection) value );
            }
            else {
                query.setParameter( name, value );
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setParameter(String name, Date value, TemporalType temporalType) {
        try {
            if ( temporalType == DATE ) {
                query.setDate( name, value );
            }
            else if ( temporalType == TIME ) {
                query.setTime( name, value );
            }
            else if ( temporalType == TIMESTAMP ) {
                query.setTimestamp( name, value );
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        try {
            if ( temporalType == DATE ) {
                query.setCalendarDate( name, value );
            }
            else if ( temporalType == TIME ) {
                throw new IllegalArgumentException( "not yet implemented" );
            }
            else if ( temporalType == TIMESTAMP ) {
                query.setCalendar( name, value );
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setParameter(int position, Object value) {
        try {
            if ( isEJBQLQuery() ) {
                this.setParameter( Integer.toString( position ), value );
            }
            else {
                query.setParameter( position - 1, value );
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    private boolean isEJBQLQuery() {
        return true; //! ( query instanceof SQLQuery );
    }

    public Query setParameter(int position, Date value, TemporalType temporalType) {
        try {
            if ( isEJBQLQuery() ) {
                String name = Integer.toString( position );
                this.setParameter( name, value, temporalType );
            }
            else {
                if ( temporalType == DATE ) {
                    query.setDate( position - 1, value );
                }
                else if ( temporalType == TIME ) {
                    query.setTime( position - 1, value );
                }
                else if ( temporalType == TIMESTAMP ) {
                    query.setTimestamp( position - 1, value );
                }
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        try {
            if ( isEJBQLQuery() ) {
                String name = Integer.toString( position );
                this.setParameter( name, value, temporalType );
            }
            else {
                if ( temporalType == DATE ) {
                    query.setCalendarDate( position - 1, value );
                }
                else if ( temporalType == TIME ) {
                    throw new IllegalArgumentException( "not yet implemented" );
                }
                else if ( temporalType == TIMESTAMP ) {
                    query.setCalendar( position - 1, value );
                }
            }
            return this;
        }
        catch (HibernateException he) {
            em.throwPersistenceException( he );
            return null;
        }
    }

    public Query setFlushMode(FlushModeType flushMode) {
        if ( flushMode == FlushModeType.AUTO ) {
            query.setFlushMode( FlushMode.AUTO );
        }
        else if ( flushMode == FlushModeType.COMMIT ) {
            query.setFlushMode( FlushMode.COMMIT );
        }
        return this;
    }
}
