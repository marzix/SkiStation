/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Cards;
import DBClasses.Clients;
import Tools.HibernateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Klasa pobierajaca dane i zapisująca je do tabeli Cards w bazie danych
 * @author Mateusz
 */
public class CardController {

    /**
     * Kupuje nową kartę i dodaje ją do puli kart klienta o podanym id
     * @param clientID - id klienta
     * @param points - liczba punktów przydzielonych do nowej karty
     * @param logger - wiadomość dotycząca powodzenia transakcji
     * @return - czy transakcja się powiodła
     */
    public static boolean buyCard(int clientID, int points, StringBuilder logger){
        Cards card = AddNewCard( clientID, points, logger );
        if( card == null )
            return false;
        return true;
    }
    
    /**
     * Dodaje kartę do puli kart klienta o podanym id
     * @param userID - id użytkownika
     * @param points - liczba punktów przydzielonych do karty
     * @param logger - wiadomość dotycząca powodzenia transakcji
     * @return - obiekt nowej karty
     */
    public static Cards AddNewCard( int userID, int points, StringBuilder logger ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        int expYear;
        Query query = s.createQuery(String.format("FROM Clients C WHERE C.users.userid = '%d'", userID ));
        List queryResult = query.list();
                
        if (queryResult.isEmpty()){
            logger.append("Client with this ID does not exist");
            return null;
        }
        
        Clients client = (Clients) queryResult.get(0);
        Cards card = new Cards();
        card.setActdate(new Date());
        Calendar c1 = GregorianCalendar.getInstance();
        expYear = c1.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) > 3)
            expYear += 1;
   
        c1.set(expYear, 3, 30);  //let's assume that 30th April is the end of season
        Date expDate = c1.getTime();
        card.setExpdate(expDate);
        card.setClients(client);
        card.setPoints(points);
        try {
            s.saveOrUpdate(card);
            tr.commit();
        } catch (HibernateException e){
            tr.rollback();
            logger.append("DB error, something went wrong");
            return null;
        } finally {
            s.close();
        }
        logger.append(String.format("Successfully added %d points to user with ID = %d", points, userID));
        return card;
    }
    
    /**
     * Usuwa kartę o podanym id
     * @param cardID - id karty
     * @return - czy operacja sie powiodła
     */
    public static Boolean DeleteCard( int cardID ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        Query query = s.createQuery(String.format("FROM Cards C WHERE C.cardid = '%d'", cardID ));
        if (query == null){
            s.close();
            return false;
        }
        Cards card = (Cards)query.list().get(0);
        query = s.createQuery(String.format("FROM Clients C WHERE C.clientid = '%d'", card.getClients().getClientid() ));
        if (query == null){
            s.close();
            return false;
        }
        Clients c = (Clients)query.list().get(0);
        query = s.createQuery(String.format("DELETE FROM Cards C WHERE C.cardid = '%d'", cardID ));
        if (query == null){
            s.close();
            return false;
        }
        int points = GetCardPoints( cardID ) + ClientController.GetClientPoints(c.getClientid());
        query.executeUpdate();
        ClientController.UpdateClientPoints(c.getClientid(), points);
        tr.commit();
        s.close();
        return true;
    }
    
    /**
     * Dodaje punkty do podanej karty na podstawie id karty
     * @param cardID - id karty
     * @param pointsToAdd - liczba punktów do przydzielenia
     * @param logger - wiadomość o powodzeniu operacji
     * @return - czy operacja się powiodła
     */
    public static boolean chargeCard(int cardID, int pointsToAdd, StringBuilder logger){
        
        int currentPoints = 0;
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        Query query = s.createQuery(String.format("FROM Clients C WHERE C.clientid = '%d'", cardID ));
        if (query == null){
            logger.append("Card with this ID does not exist");
            return false;
        }
        
        Cards card = (Cards)query.list().get(0);
        if (card.getPoints() != null)
            currentPoints += card.getPoints();
            
        card.setPoints(currentPoints + pointsToAdd);
       
        try {
            s.saveOrUpdate(card);
            tr.commit();
        } catch (HibernateException e){
            tr.rollback();
            logger.append("DB error, something went wrong");
            return false;
        } finally {
            s.close();
        }
       
        logger.append(String.format("Successfully added %d points to card with ID = %d", pointsToAdd, cardID));
        return true;
    }
    
    /**
     * Zwraca listę kart danego klienta
     * @param clientID - id klienta
     * @param logger - wiadomość o powodzeniu operacji
     * @return - lista kart klienta
     */
    public static List getUserCards(int clientID, StringBuilder logger){
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query query = s.createQuery(String.format("FROM Clients C WHERE C.clientid = '%d'", clientID ));
        if (query == null){
            logger.append(String.format("Client with ID %d has no cards", clientID));
            return null;
        }
        Clients client = (Clients) query.list().get(0);
        
        if (client.getCardses() == null){
            logger.append(String.format("Client with ID %d has no cards", clientID));
            return null;
        }
        
        List<Cards> cards = new ArrayList<Cards>();
        cards.addAll(client.getCardses());
        Collections.sort(cards, new Comparator<Cards>() {
            @Override
            public int compare(Cards card1, Cards card2) {
                  return Integer.compare(card1.getCardid(), card2.getCardid());
            }
        });
        
        for (Cards card : cards){
            System.out.println(card.getCardid() + " " + card.getPoints());
        }
        logger.append(String.format("Successfully returned %d cards", query.list().size()));
        return cards;
    }
    
    /**
     * Aktualizuje liczbę punktów przydzielonych do karty
     * @param points - nowa liczba punktów
     * @param cardID - id karty
     */
    public static void UpdateCardPoints( int points, int cardID ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        Query query = s.createQuery(String.format("UPDATE Cards SET points = '%d' WHERE cardid = '%d'", points, cardID ));
        if (query == null){
            s.close();
            return;
        }
        query.executeUpdate();
        tr.commit();
        s.close();
    }
    
    /**
     * Zwraca liczbę punktów przydzielonych do karty
     * @param cardID - id karty
     * @return liczba punktów
     */
    public static int GetCardPoints( int cardID ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        Query query = s.createQuery(String.format("FROM Cards C WHERE C.cardid = '%d'", cardID ));
        if (query == null){
            s.close();
            return 0;
        }
        Cards card = (Cards)query.list().get(0);
        tr.commit();
        s.close();
        return card.getPoints();
    }
}
