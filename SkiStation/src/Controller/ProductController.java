/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DBClasses.Itemprice;
import DBClasses.Pricelist;
import DBClasses.Product;
import DBClasses.Productitem;
import Tools.HibernateUtil;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 * Klasa sluzy do zarzadzania produktami w panelu admina
 * @author Rafał
 */
public class ProductController {
    private Session s;
    
    /**
     * domyślny konstruktor ,  otwiera sesje dla  instancji controllera   
     */
    public ProductController()
    {
        s = HibernateUtil.getSessionFactory().openSession();
    }
    
    /**
     * Tworzy produkt o podanej nazwie z podana cena, dodatkowo dodaje go do domyslnego cennika
     * @param newProductName nazwa produktu
     * @param newProductPrice cena produktu
     * @param logger logger do wyswietlania wyniku operacji w widoku
     * @return bool czy operacja sie powiodla
     */
    public boolean CreateProduct(String newProductName,String newProductPrice, StringBuilder logger)
    {
        int price =0;
        try   {
            price = Integer.parseInt(newProductPrice);
        }catch(Exception e)
        {
            logger.append(e.getMessage());
            return false;
        }
        
        if (price <= 0) {
            logger.append("Price is too low");
            return false;               
        }
        
        if (newProductName.isEmpty()) {
            logger.append("Bad product name");
            return false;   
        }
        
        if (FindProduct(newProductName)!=null) {
            logger.append("Product with the same name already exist");
            return false;                 
        }else{
            Transaction tr = s.beginTransaction();
            Product product = new Product();
            product.setName(newProductName);
            s.saveOrUpdate(product);
            tr.commit();
            
            Pricelist defaultPriceList = (Pricelist)GetPriceListsList("1").get(0);
            ProductController.this.AddPriceItem(defaultPriceList, product, price); 
            
            logger.append("Added new product with name : " + newProductName);
            return true;
        }
    }
    
    /**
     * Dodaje produkt do cennika
     * @param priceList cennik do ktorego ma zostac dodany produkt
     * @param product produkt do dodania
     * @param price z jaka cena
     */
    private void AddPriceItem(Pricelist priceList, Product product, int price)
    {
        Transaction tr = s.beginTransaction();
            Itemprice itemprice = new Itemprice();
            itemprice.setPricelist(priceList);
            itemprice.setProduct(product);
            itemprice.setPrice(price);
            s.saveOrUpdate(itemprice);
            tr.commit();
    }
    
    /**
     * Dodaje produkt do cennika
     * @param priceList cennik do ktorego ma zostac dodany produkt
     * @param product produkt do dodania
     * @param price z jaka cena
     * @param sb logger do wystietlania wyniku dzialania operacji na widoku
     * @return informuje o powodzeniu
     */
    public boolean AddPriceItem(Pricelist priceList, Product product, int price, StringBuilder sb)
    {
        if (price <= 0) {
            sb.append("Price is too low");
            return false;               
        }
        
        boolean existInList = false;
        for (Object itempriceObj : priceList.getItemprices()) {
            Itemprice itemPrice = (Itemprice)itempriceObj;
            if (itemPrice.getProduct() == product) {
                existInList = true;
                break;                        
            }
        }
        
        if (existInList) {
            sb.append("This product exist in this list");
            return false;
        }else  {
            ProductController.this.AddPriceItem(priceList, product, price);
            sb.append("Added !");
             return true;
        }     
       
        
    }
    
    /**
     * Zwraca liste wszystkich produktow
     * @return lista produktow
     */
    public List GetProductList()
    {
         List queryResult = s.createCriteria(Product.class).list();
         return queryResult;                
    }
    
    /**
     * Zwraca przefiltrowana liste produktow po nazwie
     * @param text filtr
     * @return lista produktow
     */
    public List GetProductList(String text)
    {
         List queryResult = s.createCriteria(Product.class).add(Restrictions.like("name", ("%"+text+"%"))).list();
         return queryResult;                
    }
        
    /**
     * Zwraca liste wszystkich cennikow
     * @return lista cennikow 
     */
    public List GetPriceListsList()
    {
         List queryResult = s.createCriteria(Pricelist.class).list();
         return queryResult;                
    }  
    
    /**
     * Zwraca przefitrowana po ID liste cennikow
     * @param idString id cennika
     * @return lista cennikow
     */
    public List GetPriceListsList(String idString)
    {
        Integer id;
        try{
        id = Integer.parseInt(idString);
        }catch(Exception e){
            id =null;
        }
             
        if (id == null) {
            return ProductController.this.GetPriceListsList();
        }else{
            List queryResult = s.createCriteria(Pricelist.class).add(Restrictions.eq("pricelistid", id)).list();
            return queryResult;  
        }     
    }
    
    /**
     * Zwraca ceny produktow w cenniku
     * @param id id cennika
     * @return lista cen jako ItemPrice'y
     */
    public List GetPriceItems(String id)
    {
        Pricelist priceList = (Pricelist)GetPriceListsList(id).get(0);
         List queryResult = s.createCriteria(Itemprice.class).add(Restrictions.eq("pricelist", priceList)).list();
         for (Object object : queryResult) {
            Itemprice ip = (Itemprice)object;
            System.out.print(ip.getProduct().getName());
        }
         return queryResult;                
    }  
                    
    /**
     * Zwraca produkt o podanej nazwie
     * @param name nazwa produktu
     * @return znaleziony produkt lub null
     */
    private Product FindProduct(String name)
    {
         List queryResult = s.createCriteria(Product.class).add(Restrictions.like("name", name)).list();
         Product product;
          if (queryResult.size()>0) {
            product = (Product)queryResult.get(0);
          }else{
            product = null;        
          }
          return product;     
    }
    
    /**
     * Tworzy nowy cennik
     * @param from data od
     * @param to data do
     * @param logger logger do wyswietlania tekstu w widoku
     * @return czy udalo sie utworzyc nowy cennik
     */
    public boolean CreatePriceList(String from,String to, StringBuilder logger)
    {
        
        Date fromDate = GetDateFromString(from);
        Date toDate = GetDateFromString(to);
        if (fromDate == null || toDate == null) {
            logger.append("Date format error");
            return false;
        }else{
            Transaction tr = s.beginTransaction();
            Pricelist priceList = new Pricelist();
            priceList.setStartdate(fromDate);
            priceList.setEnddate(toDate);
            s.saveOrUpdate(priceList);
            tr.commit();
            logger.append("Added new Price List with ID :" + priceList.getPricelistid());
            return true;
        }
    }
        
    /**
     * Parser stringa do daty
     * @param text data jako string dd-mm-rr
     * @return data
     */
        private Date GetDateFromString(String text)
        {
            System.out.print(text);
            String[] dateStrings = text.split("-");
            try
            {
            int day=Integer.parseInt(dateStrings[0]);
            int month = Integer.parseInt(dateStrings[1]);
            int year = Integer.parseInt(dateStrings[2]);
            return new Date(year-1900,month-1,day);
            }catch(Exception e)
            {
                return null;
            }
            
        }

    /**
     *  Dodaje domyslny cennik jezeli nie istnieje
     */
    public void AddDefaultPriceListIfNotExist()
        {
            List list = ProductController.this.GetPriceListsList();
            if (list.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                CreatePriceList("1-1-2000","1-1-2099",sb);
            }
        }
    
    /**
     * Zwraca liste produktow dostepnych w terminalu
     * @param terminalID id etrminala
     * @return lista produktow
     */
    public List GetProductsFromTerminal(int terminalID)
    {         
          List products = s.createQuery(String.format("FROM Productitem C WHERE C.terminal = '%d'", terminalID )).list();
          return products; 
    }
        
    /**
     * Zwraca aktualna cena produktu
     * @param product produkt ktorego cene chcemy dostac
     * @return cena 
     */
    public int GetActualProductPrice(Product product) {

        List queryResult = s.createCriteria(Itemprice.class).add(Restrictions.eq("product", product)).list(); // Retrieving all product prices, don't care about their dates 
        Itemprice mostActualPrice = null;

        if (!queryResult.isEmpty()) {
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
            return (mostActualPrice != null) ? mostActualPrice.getPrice() : -1;
        } else {
            return -1; // Returning -1 if at least one price wasn't found
        }
    }
}
