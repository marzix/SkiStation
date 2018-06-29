/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Addresses;
import DBClasses.Clients;
import DBClasses.Employees;
import Tools.HibernateUtil;
import javax.swing.JPanel;
import org.hibernate.Session;
import DBClasses.Users;
import java.util.Date;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 * Obsługuje rejestrację nowego użytkownika
 * @author Adam, troche MG
 */
public class RegistrationController {
    
    /**
     * Ustawienie loginu master-admina
     */
    private final String ADMIN_LOGIN = "Rav";
    
    /**
     * Wylistowanie możliwych typów kont
     */
    public enum UserTypes{
        /**
         * Administrator systemu. Może zarządzać produktami, cennikami, 
         * uzytkownikami oraz innymi konfigurowalnymi parametrami systemu.
         * Dodatkowo ma wgląd w widoki kasjera i użytkownika.
         */
        Admin,
        /**
         * Kasjer ma uprawnienia do tworzenia nowych użytkowników, sprzedaży punktów,
         * transferów punktowych, wydawania kart i innych zgodnie z założeniami.
         */
        Cashier,
        
        /**
         * Konto zwykłego użytkownika systemu. Użytkownik ma wgląd w swoje karty, 
         * moze zarządzać / transferować punkty, dokupować pakiety punktowe oraz 
         * zmieniać dane konta. 
         */
        Client
    }
    
    /**
     * Rejestracja nowego użytkownika - dodanie do bazy wszelkich niezbędnych 
     * parametrów.
     * 
     * @param name imię
     * @param surname nazwisko    
     * @param city miasto
     * @param country kraj
     * @param street ulica
     * @param login nazwa użytkownika  
     * @param password hasło konta
     * @param documentNumber numer dowodu identyfikującego osobę
     * 
     * @return true jeśli rejestracja przebiegła pomyślnie, false jeśli 
     * wystąpił błąd - np. login zajęty
     */
    public Boolean register(String name, String surname, String city, String country, String street, String login, String password, String documentNumber)
    {
          Session s = HibernateUtil.getSessionFactory().openSession();
          if (s.createCriteria(Users.class).add(Restrictions.like("login", login)).list().size()>0) {
            // not correct login
            return false;
          
          }
          else{
           Transaction tr = s.beginTransaction();
           
           Addresses newAddress = new Addresses();
           newAddress.setCity(city);
           newAddress.setCountry(country);
           newAddress.setStreet(street);
              
           s.saveOrUpdate(newAddress);
           
           Users newUser = new Users();
           newUser.setLogin(login);
           newUser.setPassword(HashingHelper.sha256(password));
           newUser.setName(name);
           newUser.setSurname(surname);
           newUser.setDocumentnumber(documentNumber);
           newUser.setAddresses(newAddress);
           newUser.setActive(true);
           
           s.saveOrUpdate(newUser);
           
           Clients newClient = new Clients();
           newClient.setRegistrationdate(new Date());
           newClient.setUsers(newUser);  
           
           s.saveOrUpdate(newClient);
           
           if (login.equals(ADMIN_LOGIN)) {
             Employees newEmployee = new Employees();
             newEmployee.setRole(UserTypes.Admin.toString());
             newEmployee.setUsers(newUser);
             s.saveOrUpdate(newEmployee);
           }
           
           tr.commit();
      
           return true;
          }

    }
}
