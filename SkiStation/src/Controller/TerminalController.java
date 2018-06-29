/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Attraction;
import DBClasses.Terminal;
import Tools.HibernateUtil;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Query;

/**
 * controller uzywany do zarzadzania terminalami w panelu admina
 * @author Rafał
 */
public class TerminalController {
    
    private Session s;
    
    /**
     * domyślny konstruktor ,  otwiera sesje dla instancji controllera   
     */
    public TerminalController()
    {
        s = HibernateUtil.getSessionFactory().openSession();
    }
    
    public boolean Exist(int terminalID)
    {
        return findTerminal(terminalID) != null;
    }
    
    public List GetTerminalList(String text)
    {
        int possibleID=0;
        
        try
        {
            possibleID = Integer.parseInt(text);
        }
        catch(Exception e)
        {
            possibleID = -1;
        }
        
        Criteria  c = s.createCriteria(Terminal.class);
        Junction junction =  Restrictions.disjunction().add(Restrictions.eq("terminalid",possibleID ));
        
        List possibleAttractions = GetAttractionList(text);
        
         for (Object result : possibleAttractions) {
            Attraction attraction = (Attraction)result;           
            junction.add(Restrictions.eq("attraction", attraction));
        }
        
        List queryResult = c.add(junction).list();
        
        return queryResult;    
    }
    
    public List GetAttractionList(String text)
    {
        List queryResult = s.createCriteria(Attraction.class).add(Restrictions.like("name", text,MatchMode.ANYWHERE)).list();       
        return queryResult;     
    }
    
    private Terminal findTerminal(int terminalID)
    {
         List queryResult = s.createCriteria(Terminal.class).add(Restrictions.like("terminalid", terminalID)).list();
         Terminal terminal;
          if (queryResult.size()>0) {
            terminal = (Terminal)queryResult.get(0);
          }else{
            terminal = null;        
          }
          return terminal;     
    }
    
    private Attraction findAttraction(int attractionID)
    {
         List queryResult = s.createCriteria(Attraction.class).add(Restrictions.like("attractionid", attractionID)).list();
         Attraction attraction;
          if (queryResult.size()>0) {
            attraction = (Attraction)queryResult.get(0);
          }else{
            attraction = null;        
          }
          return attraction;     
    }
    
    public int GetAttractionID( String name ) {
        Query queryList = s.createQuery( String.format("FROM Attraction a WHERE a.name = '%s'", name) );
        if( queryList == null ) {
            return -1;
        }
        Attraction atr = (Attraction)queryList.list().get(0);
        if( atr != null )
            return atr.getAttractionid();
        return -1;
    }
    
    public boolean addTerminal(int parentAttractionID, int lockTime, StringBuilder message)
    {
        Attraction parentAttraction = null;
        Query queryList = s.createQuery( String.format("FROM Attraction a WHERE a.attractionid = '%d'", parentAttractionID) );
        if( queryList == null ) {
            return false;
        }
        parentAttraction = (Attraction)queryList.list().get(0);
        if (parentAttraction!=null) {
            Transaction tr = s.beginTransaction();
            
            Terminal terminal = new Terminal();
            terminal.setAttraction(parentAttraction);
            terminal.setLocktime(lockTime);
            terminal.setProductitems(null);
            s.saveOrUpdate(terminal);
            tr.commit();
            
            message.append("Succesfully added new terminal");
            return true;  
        }else{
            message.append(String.format("Attraction with %d id not exist",parentAttractionID));
            return false;           
        }
    }
   
}
