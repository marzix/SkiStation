package Model;

/**
 * Klasa przechowująca dane dotyczące konta użytkownika
 * @author Marzena, documentation Sebastian
 */
public class AccountInfo {
    
    /** 
     * Imię właściciela konta
     */
    public String name;
    
     /** 
     * Nazwisko właściciela konta
     */
    public String surname;
    
     /** 
     * Login właściciela konta
     */
    public String login;
    
     /** 
     * ID właściciela konta
     */
    public String userID;
    
     /** 
     * Numer dokumentu właściciela konta
     */
    public String documentNumber;
    
     /** 
     * Nazwa ulicy właściciela konta
     */
    public String street;
    
     /** 
     * Miasto zamieszkania właściciela konta
     */
    public String city;
    
     /** 
     * Kraj właściciela konta
     */
    public String country;
    
     /** 
     * Hasło właściciela konta
     */
    public String password;
    
     /** 
     * Stan konta - nieaktywne jest z punktu widzenia użytkownika nieistniejące
     */
    public Boolean active;
    
    
     /** 
     * Domyślny konstruktor inicjalizujący zmienne
     */
    public AccountInfo() {
        name = new String();
        surname = new String();
        login = new String();
        userID = new String();
        documentNumber = new String();
        street = new String();
        city = new String();
        country = new String();
        password = new String();
        active = false;
    }
}
