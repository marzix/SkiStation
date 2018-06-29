/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Employees;
import DBClasses.Users;
import Tools.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import Model.ViewSwitcher;
/**
 * Klasa obsługująca logowanie użytkownika do systemu
 * @author Mateusz
 */
public class LoginController {
    
    /**
     * Weryfikuje logowanie oraz loguje uzytkowników do systemu
     * @param login - login użytkownika
     * @param password - hasło użytkownika
     * @param viewName - nazwa panelu, który załaduję się po zalogowaniu
     * @param loginStatusMessage - wiadomość o powodzeniu operacji
     * @return - czy operacja się powiodła
     */
    public static boolean logIn(String login, String password, StringBuilder viewName, StringBuilder loginStatusMessage){
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        String hashPass = HashingHelper.sha256(password);
        Query query = s.createQuery(String.format("FROM Users U WHERE U.login = '%s' AND U.password = '%s'", login, hashPass ));
        if (query.list().isEmpty() || login.equals("") || password.equals("")){
            System.out.println("Failed login");
            loginStatusMessage.append("Failed login");
            return false;
        } else {
            
            Users user;
            Employees employee= null;
            
            user = (Users)query.list().get(0);
        
            Query query2 = s.createQuery(String.format("FROM Employees E WHERE E.users = '%d'",user.getUserid()));
            
            if(query2.list().size() > 0)
            {
                employee =  (Employees)query2.list().get(0);
            }

            if (employee == null) {
                viewName.append("UserMainPanel");
                SessionController.setLoggedUserType(RegistrationController.UserTypes.Client);
            }else if (employee.getRole().equals(RegistrationController.UserTypes.Admin.toString()) ) {
                viewName.append("adminPanelMain");
                SessionController.setLoggedUserType(RegistrationController.UserTypes.Admin);
            }else if (employee.getRole().equals(RegistrationController.UserTypes.Cashier.toString())) {
                viewName.append("cashierPanel");
                SessionController.setLoggedUserType(RegistrationController.UserTypes.Cashier);
            }
            SessionController.SetUserLogged(login);
            System.out.println("Successful login"); 
            loginStatusMessage.append("Successful login");
            return true;
        }
    }
    
}
