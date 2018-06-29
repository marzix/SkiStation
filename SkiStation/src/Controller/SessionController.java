/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;
import DBClasses.Users;
import java.util.Vector;
import Model.ViewSwitcher;
import Tools.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Klasa kontrolera służącego do zarządzania sesją logowania użytkownika
 * @author Marzena, documentation Adam
 */
public class SessionController {
    
    /** 
     * Login obecnie zalogowanego użytkownika
     */
    private static String userLogged = "";
    
    /**
     * Czas wykonania ostatniej akcji przez użytkownika
     */
    private static long noActionTime = 0;
    /** 
     * Maksymalny czas podtrzymywania sesji w przypadku braku interakcji użytkownika
     * Domyślny czas podtrzymania sesji to 5 minut (300000 milisekund)
     */
    private static final Integer MAX_TIME = 300000;
    
    /**
     * Typ konta obecnie zalogowanego użytkownika
     */
    private static RegistrationController.UserTypes loggedUserType;
    
    /**
     * Historia przełączania widoków; umożliwia utworzenie możliwości powrotu 
     * do poprzednio wyświetlonego widoku
     */
    private static Vector<ViewSwitcher> previousViews = new Vector<>();

    /**
     * Domyślny konstruktor; ustawia pustą historię widzianych widoków
     */
    public SessionController() {
        previousViews = new Vector<>();
    }
    
    /**
     * Ustanowienie sesji dla konkretnego użytkownika
     * 
     * @param user login zalogowanego użytkownika
     */
    public static void SetUserLogged( String user ){
        userLogged = user;
        noActionTime = System.currentTimeMillis();
    }
    
    /**
     * Wyzerowanie sesji. Używane w przypadku wylogowania użytkownika z systemu
     */
    public static void ResetSession() {
        userLogged = "";
        noActionTime = 0;
        previousViews.clear();
    }
    
    /**
     * Getter dla nazwy obecnie zalogowanego użytkownika
     * 
     * @return login zalogowanego użytkownika
     */
    public static String GetUserLogged() {
        return userLogged;
    }
    
    /**
     * Zaktualizowanie czasu ostatniej akcji użytkownika
     */
    public static void UpdateSession() {
        noActionTime = System.currentTimeMillis();
    }
    
    /**
     * Metoda służąca do sprawdzenia stanu zalogowania użytkownika
     * 
     * @return true jeśli użytkownik, dla którego istnieje bieżąca sesja, 
     * jest nadal zalogowany
     */
    public static Boolean IsUserLogged() {
        long currentTime = System.currentTimeMillis();
        if( currentTime-noActionTime > MAX_TIME )
        {
            noActionTime = 0;
            userLogged = "";
            return false;
        }
        noActionTime = currentTime;
        return true;
    } 
    
    /**
     * Setter dla typu zalogowanego użytkownika
     * 
     * @param userType rodzaj konta
     */
    public static void setLoggedUserType(RegistrationController.UserTypes userType){
        loggedUserType = userType;
    }
    
    /**
     * Getter dla typu konta zalogowanego użytkownika
     * @return typ konta
     */
    public static RegistrationController.UserTypes getLoggedUserType(){
        return loggedUserType;
    }
    
    /**
     * Dodanie obecnego widoku do historii widoków
     * 
     * @param view widok
     */
    public static void AddToPreviousViews( ViewSwitcher view ) {
        previousViews.add(view);
    }
    
    /**
     * Pobranie przedostatniego widoku
     * Ostatni widok jest tym, który widzimy obecnie
     * 
     * @return widok
     */
    public static ViewSwitcher GetPreviousView() {
        // the last position in the vector is the current view
        if( previousViews.size() < 2 )
            return new ViewSwitcher();
        return previousViews.elementAt(previousViews.size() - 2);
    }
    /**
     * Usunięcie z historii najnowszego (ostatniego) widoku
     */
    public static void DeleteLastView() {
        previousViews.remove(previousViews.lastElement());
    }
    
    /**
     * Pobranie ilości widoków dostępnych w historii
     * 
     * @return int ilośc widoków w historii
     */
    public static int GetViewsCount() {
        return previousViews.size();
    }
    
    /**
     * Getter dla szczegółów zalogowanego użytkownika
     * 
     * @return zwraca obecnie zalogowanego użytkownika
     */
    public static Users GetLoggedUserData()
    {
        Session s = HibernateUtil.getSessionFactory().openSession();
        return (Users)s.createCriteria(Users.class).add(Restrictions.like("login", userLogged)).list().get(0);
    }
}
