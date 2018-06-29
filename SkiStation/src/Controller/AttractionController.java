//////////////////////////////////////////////
// AttractionController.java:
//////////////////////////////////////////////
 
package Controller;
 
import DBClasses.Attraction;
import Tools.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
/**
 * Klasa służąca do aktualizacji danych w tabeli Attractions w bazie danych
 * @author Adam
 */
public class AttractionController {
 
    private Session s;
 
    /**
     * Konstruktor otwiera nową sesję
     */
    public AttractionController() {
        s = HibernateUtil.getSessionFactory().openSession();
    }
 
    /**
     * Dodaje nowy rekord do tabeli Attractions w bazie danych
     * @param name - nazwa atrakcji
     * @param type - typ aktrakcji
     * @param message - wiadomość o powodzeniu operacji
     * @return zwraca nowo utworzoną atrakcję
     */
    public Attraction addAttraction(String name, String type, StringBuilder message) {
 
        try {
            Transaction tr = s.beginTransaction();
 
            Attraction attraction = new Attraction();
            attraction.setName(name);
            attraction.setType(type);
 
            Integer id = (Integer) s.save(attraction);
            tr.commit();
                   
            attraction.setAttractionid(id);
           
            message.append("Succesfully added new attraction");
 
            return attraction;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

 
 
    //////////////////////////////////////////////
    // Metoda dla przycisku "Dodaj" w starej zakładce "Nowa Atrakcja"
    //////////////////////////////////////////////
    /*private void AddTerminalButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        StringBuilder message = new StringBuilder();
        String attractionName = TerminalNameTextField.getText();
        String attractionType = AttractionTypeComboBox.getSelectedItem().toString().toLowerCase();
        Integer lockTime = Integer.parseInt(LockTimeComboBox1.getSelectedItem().toString().replace("s", ""));
       
        if ((Arrays.asList(new String[]{"sklep", "stok"})).contains(attractionType) && !attractionName.isEmpty()){
            AttractionController attractionController = new AttractionController();
           
            Attraction attraction = attractionController.addAttraction(attractionName, attractionType, message);
           
            if (attraction != null){
               
                TerminalController terminalController = new TerminalController();
               
                if (terminalController.addTerminal(attraction.getAttractionid(), (attractionType.equals("sklep") ? 0 : lockTime), message))
                {
                    ViewSwitcher view = new ViewSwitcher(getContentPane(), "AdminMainPanel", AttractionManagementAdminPanel, "AttractionListPanel");
                    changeCard(view, true);
                }
            }
        } else
            AttractionEditMessage.setText("Wystąpił błąd. Nazwa nie może być pusta.");
    }*/
}
