/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Attraction;
import DBClasses.Cardusage;
import DBClasses.Terminal;
import DBClasses.Users;
import Tools.HibernateUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * Klasa obsługująca funkcje dostępne w panelu kasjera
 * @author Sebastian
 */
public class CashierController {
 
    /**
     * obiekt przechowujący użytkownika wybranego do edycji w panelu kasjera
     */
    private static Users selectedUser;
    
    private Session s;
    
    /**
     * konstruktor otwierający nową sesję
     */
    public CashierController()
    {
        s = HibernateUtil.getSessionFactory().openSession();
    }
    
    /**
     * Zwraca listę użytkowników, których login, imię lub nazwisko zawiera podany ciąg znaków
     * @param text tekst wykorzystywany jako wzorzec do wyszukiwania w bazie
     * @return lista użytkowników
     */
    public List GetUsersList(String text)
    {
         List queryResult = s.createCriteria(Users.class).add(Restrictions.disjunction()
                 .add(Restrictions.like("login", text,MatchMode.ANYWHERE))
                 .add(Restrictions.like("name", text,MatchMode.ANYWHERE))
                 .add(Restrictions.like("surname", text,MatchMode.ANYWHERE))).list();
         
          return queryResult;     
    }
    
    /**
     * Zwraca losowego uzytkownika
     * @return obiekt klasy Users
     */
    public Users GetRandomUser()
    {
        List queryResult = s.createCriteria(Users.class).list();
        
        Random random = new Random();        
        int randomIndex = Math.abs(random.nextInt()) % queryResult.size();
         
        return (Users)queryResult.get(randomIndex);     
    }
    
    /**
     * Zapisuje wybranego do edycji użytkownika
     * @param newUser - wybrany użytkownik
     * @param textPane - pole tekstowe, w którym wyświetlane jest imię, nazwisko i login wybranego użytkownika
     */
    public static void SetSelectedUser(Users newUser, JTextPane textPane)
    {
        selectedUser = newUser;
        SetText(textPane);
    }
    
    /**
     * Zwraca login użytkownika wybranego do edycji
     * @return login użytkownika
     */
    public static String GetSelectedUser() {
        return selectedUser != null ? selectedUser.getLogin() : null;
    }
    
    /**
     * Zapisuje wybranego do edycji użytkownika na podstawie podanego loginu
     * @param newUserString - login użytkownika
     * @param textPane - pole tekstowe, w którym wyświetlane jest imię, nazwisko i login wybranego użytkownika
     */
    public void SetSelectedUserWithString(String newUserString, JTextPane textPane)
    {
        if( newUserString == null )
            return;
        String convertedString = newUserString.subSequence(newUserString.indexOf("(") + 1, newUserString.indexOf(")")).toString();              
        List queryResult = s.createCriteria(Users.class).add(Restrictions.like("login", convertedString,MatchMode.EXACT)).list();
        
        if(queryResult.size() > 0)
        {
            selectedUser = (Users)queryResult.get(0);
        }

        SetText(textPane);
    }
    /**
     * Ustawia tekst w polu tekstowym wyświetlającym wybranego użytkownika
     * @param textPane - pole tekstowe, w którym wyświetlane jest imię, nazwisko i login wybranego użytkownika
     */
    private static void SetText(JTextPane textPane)
    {
        textPane.setText("Użytkownik: " + GetUserConvertedName(selectedUser));
    }
    
    /**
     * formatuje tekst zawierający imię, nazwisko i login użytkownika do wyświetlenia
     * @param user - wybrany użytkownik
     * @return sformatowany tekst
     */
    public static String GetUserConvertedName(Users user)
    {
        return user.getName() + " " + user.getSurname() + " (" + user.getLogin() + ")";
    }
    
    /**
     * Zwarac wszystkie rekordy z tabeli Attraction w bazie danych
     * @return lista obiektów typu Attraction
     */
    public List GetSkiAttractions()
    {
        List queryResult = s.createCriteria(Attraction.class).add(Restrictions.like("type", "stok")).list();
        
        return queryResult;
        
    }
    
    /**
     * Zwraca aktualną datę
     * @return aktualna data
     */
    private Date GetDateNow()
    {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date date = null;
        
        try {
            date = originalFormat.parse("01-21-2013 00:00:00");
        } catch (ParseException ex) {
            Logger.getLogger(CashierController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return date;
    }
    
    /**
     * Zwraca liczbę osób korzystających w danej chwili z atrakcji
     * @param attraction atrakcja z której zostanie odczytane natęzenie
     * @return liczba osób korzystających w danej chwili z atrakcji
     */
    public int GetSkiAttractionTraffic(Attraction attraction)
    {
        int traffic = 0;
        
        for (Object terminalObject : attraction.getTerminals()) {
            Terminal terminal = (Terminal)terminalObject;
            
            
            
            List queryResult = s.createCriteria(Cardusage.class).add(Restrictions.eq("terminal", terminal))
                   .list();
            
            for (Object result : queryResult) {
                Cardusage usage = (Cardusage)result;
                
                System.out.println("Date: " + usage.getUsedate() + "is after: " + GetDateNow() + " - " + usage.getUsedate().after(GetDateNow()));
                
                if(usage.getUsedate().after(GetDateNow()))
                {
                    traffic++;
                }
            }

            //traffic += queryResult.size();
        }

         return traffic;
    }
    
    /**
     * Zwraca uzytkownika wybranego do edycji
     * @return użytkownik zaznaczony w trybie kasjera
     */
    public static Users GetSelectedUserData()
    {
        return selectedUser;
    }
    
}
