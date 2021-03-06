package DBClasses;
// Generated 2016-05-01 18:16:39 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Cards generated by hbm2java
 */
public class Cards  implements java.io.Serializable {


     private int cardid;
     private Clients clients;
     private Date actdate;
     private Date expdate;
     private Integer points;
     private Set cardusages = new HashSet(0);
     private Set transactionses = new HashSet(0);

    public Cards() {
    }

	
    public Cards(int cardid, Clients clients) {
        this.cardid = cardid;
        this.clients = clients;
    }
    public Cards(int cardid, Clients clients, Date actdate, Date expdate, Integer points, Set cardusages, Set transactionses) {
       this.cardid = cardid;
       this.clients = clients;
       this.actdate = actdate;
       this.expdate = expdate;
       this.points = points;
       this.cardusages = cardusages;
       this.transactionses = transactionses;
    }
   
    public int getCardid() {
        return this.cardid;
    }
    
    public void setCardid(int cardid) {
        this.cardid = cardid;
    }
    public Clients getClients() {
        return this.clients;
    }
    
    public void setClients(Clients clients) {
        this.clients = clients;
    }
    public Date getActdate() {
        return this.actdate;
    }
    
    public void setActdate(Date actdate) {
        this.actdate = actdate;
    }
    public Date getExpdate() {
        return this.expdate;
    }
    
    public void setExpdate(Date expdate) {
        this.expdate = expdate;
    }
    public Integer getPoints() {
        return this.points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    public Set getCardusages() {
        return this.cardusages;
    }
    
    public void setCardusages(Set cardusages) {
        this.cardusages = cardusages;
    }
    public Set getTransactionses() {
        return this.transactionses;
    }
    
    public void setTransactionses(Set transactionses) {
        this.transactionses = transactionses;
    }




}


