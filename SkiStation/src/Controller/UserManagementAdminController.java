/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Employees;
import DBClasses.Users;
import Tools.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 * UserManagementAdminController controller służący do zarządzania userem w panelu admina
 * @author Rafał
 */
public class UserManagementAdminController {
    
    private Session s;
    
    /**
     * domyślny konstruktor ,  otwiera sesje dla  instancji controllera   
     */
    public UserManagementAdminController()
    {
        s = HibernateUtil.getSessionFactory().openSession();
    }
    
    /**
     * Metoda sprawdza czy użytkownik z podanym loginem istnieje
     * @param login login usera 
     * @return prawda jezeli istnieje, falsz jezeli nie istnieje
     */
    public boolean Exist(String login)
    {
        return FindUser(login) != null;
    }
    
    /** 
     * Szuka usera z podanym loginem
     * @param login login usera
     * @return referencje na obiekt usera, lub null jezeli user z takim loginem nie istnieje
     */
    public Users FindUser(String login)
    {
         List queryResult = s.createCriteria(Users.class).add(Restrictions.like("login", login)).list();
         Users user;
          if (queryResult.size()>0) {
            user = (Users)queryResult.get(0);
          }else{
            user = null;        
          }
          return user;     
    }
      
     /** 
     * Sprawdza jaki status posiada podany User
     * @param user referencja na usera do sprawdzenia
     * @return null jezeli user==null lub nie znaleziono roli dla tego usera, 
     * w normalnych warunkach zwraca referencje na Emplyees podanego usera
     */
    private Employees GetUserRole(Users user)
    {
        if (user == null) {
            return null;
        }
        System.out.println(user.getUserid());
        List queryResult = s.createCriteria(Employees.class).add(Restrictions.eq("users", user)).list();
        Employees role;
        if (queryResult.size()>0) {
            role = ((Employees)queryResult.get(0));
        }else{
            role = null;      
        }

        return role;   
    }
    
          
     /** 
     * Ustawia role dla podane użytkownika
     * @param user referencja na usera
     * @param role rola jaka ma otrzymac user
     * @return informaje o powodzeniu wykonania metody
     */
    private boolean SetUserRole(Users user, String role)
    {
         Transaction tr = s.beginTransaction();
         
         Employees employee = UserManagementAdminController.this.GetUserRole(user);
         if (role == "User") {
             if (employee!=null) {
                s.delete(employee);  
             }
        }else{
             if (employee == null) {
                employee = new Employees();
                employee.setUsers(user);
             }
             employee.setRole(role);
             s.saveOrUpdate(employee);
         }
         tr.commit();

         return true;         
    }
    
    /**
     * pobiera role usera jako string
     * @param login login usera
     * @return role usera jako string
     */
    public String GetUserRole(String login)
    {
         Users user = FindUser(login);
         Employees role =  UserManagementAdminController.this.GetUserRole(user);
                 
         if (role == null) {
            return "User";
        }else{
             return role.getRole();
         }
    }

    /**
     * ustawia role uzytkownikowi 
     * @param login login uzytkownika
     * @param role jaka rola ma zostac nadana
     * @return powodzenie lub nie przypisania 
     */
    public boolean UpdateUser(String login, String role)
    {
        Users user = FindUser(login);
        if (user==null) {
            return false;
        }else{
            SetUserRole(user, role);
            return true;
        }
    }
    
    /**
     * Zwraca liste wszystkich użytkownikow
     * @return lista wszystkich użytkownikow
     */
    public List GetUsers()
    {
        return s.createCriteria(Users.class).list();
    }
}
