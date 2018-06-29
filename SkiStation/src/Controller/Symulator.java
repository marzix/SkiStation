/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Boughtitem;
import DBClasses.Cards;
import DBClasses.Cardusage;
import DBClasses.Itemprice;
import DBClasses.Pricelist;
import DBClasses.Terminal;
import Tools.HibernateUtil;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 * Symulator stoku, mozemy w nim symulowac kupowania produktów w bramkach
 * @author Rafał
 */
public class Symulator {
    private Session s;
    
     /**
     * domyślny konstruktor ,  otwiera sesje dla instancji controllera   
     */
    public Symulator()
    {
        s = HibernateUtil.getSessionFactory().openSession();
    }
    
    /**
     * Zwraca liste wszystkich kart w systemie
     * @return lista kart
     */
    public List GetCards()
    {
        return s.createCriteria(Cards.class).list();
    }
    
    /**
     * zwraca ilość pkt posiadanych przez karte o podanym ID
     * @param cardID id karty
     * @return liczba pkt posiadanych przez karte 
     */
    public int GetCardPoints(int cardID )
    {
        List list = s.createCriteria(Cards.class).add(Restrictions.like("cardid", cardID)).list();
        if (!list.isEmpty()) {
            return ((Cards)list.get(0)).getPoints();
        }else{
            return 0;
        }
    }
    
    /**
     * Zwraca liste wszystkich terminali w systemia
     * @return lista terminali
     */
    public List GetTerminals()
    {
        return s.createCriteria(Terminal.class).list();
    }
    
    /**
     * Wykonuje operacje kupienia produków przez karte, tworzy wpisy do bazy danych związane z zakupami
     * @param cardID id kupujacej karty 
     * @param terminalID id terminala w ktorym dokonywane sa zakupy
     * @param sum suma wszyskich kupowanych produktow
     * @param productIDs id produktow do kupienia
     * @param logger logger do wyswietlania wyniku operacji na widoku
     */
    public void BuyProductsFor(int cardID,int terminalID,int sum, List<Integer> productIDs, StringBuilder logger)
    {
        
        List cards =  s.createQuery(String.format("FROM Cards C WHERE C.cardid = '%d'", cardID )).list();
        List terminals =  s.createQuery(String.format("FROM Terminal C WHERE C.terminalid = '%d'", terminalID )).list();
        if (!cards.isEmpty()) {
            if (!terminals.isEmpty()) {  

                Cards card = (Cards)cards.get(0);    
                
                if (card.getPoints()>= sum) {
                    Terminal terminal = (Terminal)terminals.get(0); 
                    Transaction tr = s.beginTransaction();

                    Cardusage cardUsage = new Cardusage();
                    cardUsage.setCards(card);
                    cardUsage.setTerminal(terminal);
                    cardUsage.setUsedate(new Date());
                    s.saveOrUpdate(cardUsage);

                    for (Integer productID : productIDs) {
                        Itemprice itemPrice = GetCurrentProductPrice(productID);
                        if (itemPrice!=null) {     
                            System.out.print(itemPrice.getPrice());
                            Boughtitem boughtItem = new Boughtitem();
                            boughtItem.setCardusage(cardUsage);
                            boughtItem.setItemprice(itemPrice);
                            s.saveOrUpdate(boughtItem);
                        }         
                    }

                    card.setPoints(card.getPoints() - sum);
                    s.saveOrUpdate(card);
                    tr.commit();  
                    logger.append("everything OK");
                    return;
                }else{
                    logger.append("too little amount of points");
                }
                                
                
            }else{
                  logger.append("This terminal is not available!");
                  return ;
            }
        }else{
            logger.append("This card is not available!");
            return ;
        }
    }
     
     /**
     * Zwraca aktualna cena produktu
     * @param productID id produktu ktorego cena zostanie pobrana
     * @return cena produktu jako itemPrice
     */
        private Itemprice GetCurrentProductPrice(int productID) {
            
        List queryResult =  s.createQuery(String.format("FROM Itemprice C WHERE C.product = '%d'", productID )).list();
        Itemprice mostActualPrice = null;

        if (!queryResult.isEmpty()) {
            System.out.print("123");
            for (Object itemPriceObj : queryResult) { // Looking for most actual product price based on pricelists and their start-end dates
                Itemprice itemPrice = (Itemprice) itemPriceObj;
                Pricelist pricelist = itemPrice.getPricelist();
                Date actDate = new Date();
              
                if (actDate.after(pricelist.getStartdate()) && actDate.before(pricelist.getEnddate())) {
                    if (mostActualPrice == null || (itemPrice.getItempriceid() > mostActualPrice.getItempriceid())) {
                        mostActualPrice = itemPrice;
                    }
                }
            }
            return mostActualPrice;
        } else {
            return null;
        }
    }
    
}

