/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Tools.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Klasa kontrolera wykonująca operacje dotyczące transakcji na stoku
 * @author Mateusz
 */
public class SlopeController {

   /**
    * Funkcja sprawdzająca obciążenie każdej bramki stoku z ostatniej godziny
    * @param logger - przechowuje informacje o niepowodzeniu operacji/braku stokow
    * @return - lista obiektów przechowujących dane nt. każdej bramki stoku
    */
    public static List<Object[]> getTrafficList(StringBuilder logger) {
        try {
            SimpleDateFormat dateHelper = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateHelper.format(new Date());
            String hourBefore;
            Calendar c1 = GregorianCalendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY) - 1);
            hourBefore = dateHelper.format(c1.getTime());

            Session s = HibernateUtil.getSessionFactory().openSession();
            Query query = s.createQuery(String.format("SELECT t.terminalid, SUM(CASE WHEN c.usedate <= '%s' AND c.usedate >= '%s' THEN 1 ELSE 0 END)"
                    + " FROM Cardusage c, Terminal t, Attraction a " +
                                 "WHERE c.terminal = t.terminalid " +
                                   "AND t.attraction.attractionid = a.attractionid " +
                                   "AND a.type = 'Stok' " +
                                  "GROUP BY t.terminalid", currentDate, hourBefore));
            List< Object[]> queryList = query.list();
            if (queryList.isEmpty()) {
                logger.append("Traffic list select error or no Slopes defined.");
                return null;
            } else {
                for (Object[] row : queryList) {
                    System.out.println("Ilosc " + row[0]);
                    System.out.println("ID " + row[1]);
                }
                return queryList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
