package DBClasses;
// Generated 2016-05-09 22:48:43 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Terminal generated by hbm2java
 */
public class Terminal  implements java.io.Serializable {


     private int terminalid;
     private Attraction attraction;
     private Integer locktime;
     private Set productitems = new HashSet(0);
     private Set cardusages = new HashSet(0);


    public Terminal() {
    }

	
    public Terminal(Attraction attraction) {
        this.attraction = attraction;
    }
    public Terminal(Attraction attraction, Cardusage cardusage, Integer locktime) {
       this.attraction = attraction;
       this.locktime = locktime;
       this.productitems = productitems;
    }
   
    public int getTerminalid() {
        return this.terminalid;
    }
    
    public void setTerminalid(int terminalid) {
        this.terminalid = terminalid;
    }
    public Attraction getAttraction() {
        return this.attraction;
    }
    
    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Integer getLocktime() {
        return this.locktime;
    }
    
    public void setLocktime(Integer locktime) {
        this.locktime = locktime;
    }
    public Set getProductitems() {
        return this.productitems;
    }
    
    public void setProductitems(Set productitems) {
        this.productitems = productitems;
    }
    
    public Set getCardusages(){
        return this.cardusages;
    }
    
    public void setCardusages(Set cardusages){
            this.cardusages = cardusages;
    }
}

