/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Employees;
import DBClasses.Users;
import DBClasses.Addresses;
import Tools.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import Model.AccountInfo;

/**
 * Klasa obsługująca aktualizację danych osobowych użytkowników
 * @author Marzena
 */
public class MyAccountController {
    
    /**
     * Zwraca dane osobowe konta o danym loginie
     * @param login - login użytkownika
     * @return obiekt klasy Users zawierający podstawowe dane użytkownika
     */
    public static Users GetAccountDetails( String login ) {
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        if( login.isEmpty() )
            return null;
        Query query = s.createQuery(String.format("FROM Users U WHERE U.login = '%s'", login) );
        if( query.list().isEmpty() || login.equals("") )
            return new Users();
        
        Users user;            
        user = (Users)query.list().get(0);

        s.close();
        return user;
    }
    
    /**
     * Aktualizuje dane dotyczące adresu danego użytkownika
     * @param newAddress - nowe danr adresowe
     * @param login - login uzytkownika
     * @return - obiekt klasy Addresses ze zaktualizowanymi danymi
     */
    public static Addresses UpdateUserAddress( Addresses newAddress, String login ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Addresses address = new Addresses();
        if( login.isEmpty() )
            login = SessionController.GetUserLogged();
        
        Transaction tr = s.beginTransaction();
        Query query = s.createQuery(String.format("FROM Users U WHERE U.login = '%s'", login) );
        if( query.list().isEmpty() || login.equals("") )
            return address;
        
        Users user;            
        user = (Users)query.list().get(0);
        
        query = s.createQuery(String.format("FROM Addresses U WHERE U.addressid = '%s'", user.getAddresses().getAddressid()) );
        if( query.list().isEmpty() )
            return address;
                  
        address = (Addresses)query.list().get(0);
        address.setStreet(newAddress.getStreet());
        address.setCountry(newAddress.getCountry());
        address.setCity(newAddress.getCity());
        
        s.saveOrUpdate(address);
        tr.commit();
        s.close();
        return address;
    }
    
    /**
     * Zwraca dane konta użytkownika w formie tekstowej gotowej do wyśietlenia
     * @param login - login uzytkownika
     * @return - obiekt przechowujący wszystkie dane w formie tekstowej
     */
    public static AccountInfo GetAccountInfoString( String login ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        AccountInfo info = new AccountInfo();
        if( login == null || login.isEmpty() )
            return info;
        
        Query query = s.createQuery(String.format("FROM Users U WHERE U.login = '%s'", login) );
        if( query.list().isEmpty() || login.equals("") )
            return info;
        
        Users user = (Users)query.list().get(0);
        
        info.name = user.getName();
        info.surname = user.getSurname();
        info.login = user.getLogin();
        info.documentNumber = user.getDocumentnumber();
        info.userID = Integer.toString(user.getUserid());
        info.active = user.getActive();
        Addresses address = user.getAddresses();
        if( address != null ) {
            info.street = address.getStreet();
            info.city = address.getCity();
            info.country = address.getCountry();
        }
        s.close();
        return info;
    }
    
    /**
     * Zwraca login użytkownika na podstawie jego id
     * @param userID - id użytkownika
     * @return - login użytkownika
     */
    public static String GetLoginFromID( int userID ) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query query = s.createQuery(String.format("FROM Users U WHERE U.userid = '%s'", userID) );
        if( query.list().isEmpty() )
            return null;
        Users user = (Users)query.list().get(0);
        String login = user.getLogin();
        s.close();
        return login;
    }      
    
    /**
     * Aktualizuje dane użytkownika
     * @param newUser - obiekt zawierający nowe dane
     * @param userID - id użytkownika
     * @return - wiadomość o powodzeniu operacji
     */
    public static String UpdateAccountDetails( Users newUser, int userID ){
        Session s = HibernateUtil.getSessionFactory().openSession();
        String login = GetLoginFromID( userID );
        Users oldUser = GetAccountDetails( login );
        if( oldUser == null )
            return "Błąd aktualizacji";
        UpdateUserAddress( newUser.getAddresses(), login );
        Transaction tr = s.beginTransaction();
        if( newUser.getLogin().equals("") )
            return "Nieprawidłowy login";
        if( !( oldUser.getLogin().equals(newUser.getLogin()) ) )
        {
            if (s.createCriteria(Users.class).add(Restrictions.like("login", newUser.getLogin())).list().size()>0) {
            // incorrect login
            return "Login zajęty";          
          }
        }
        oldUser.setName(newUser.getName());
        oldUser.setSurname(newUser.getSurname());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setDocumentnumber(newUser.getDocumentnumber());
        oldUser.setActive(newUser.getActive());
        s.saveOrUpdate(oldUser);
        tr.commit();
        s.close();
        return "Dane zaktualizowane";
    }
}
