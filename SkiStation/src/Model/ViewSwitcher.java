/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;
import java.awt.Container;
/**
 * Klasa pomocnicza używana przy przełączaniu widoków w aplikacji
 * przechowuje hierarchię panelów w celu poprawnego przemieszczania się pomiędzy panelami 
 * należącymi do różnych podgrup
 * @author Marzena
 */
public class ViewSwitcher {
    /**
     * panel główny, najczęściej główne okno aplikacji
     */
    public Container mainContainer = null;
    /**
     * Nazwa componentu w danym panelu głównym, najczęściej jest to panel główny widoku klienta,
     * kasjera lub administratora, zawierający pasek głównego menu użytkownika
     */
    public String containerName = "";
    /**
     * Karta, która nakładana jest na panel główny widoku użytkownika w celu wyświetlenia różnej zawartości
     */
    public Container card = null;
    /**
     * Nazwa karty, którą chcemy wyświetlić, np. w administratorze karta zarządzania użytkownikami lub
     * zarządzania bramkami
     */
    public String cardName = "";
    
    /**
     * Domyślny konstruktor
     */
    public ViewSwitcher() {}
    
    /**
     * Konstruktor dwuargumentowy, używany podczas przełączania pomiędzy ekranem logowania, a ekranem rejestracji
     * @param _mainContainer - kontener okna głównego aplikacji
     * @param _containerName - nazwa panelu wyświetlanego w kontenerze głównym
     */
    public ViewSwitcher( Container _mainContainer, String _containerName ) {
        mainContainer = _mainContainer;
        containerName = _containerName;
    }
    
    /**
     * Konstruktor dwuargumentowy, używany podczas przełączania pomiędzy ekranami użytkowników (klienta, kasjera, administratora)
     * @param _mainContainer - kontener okna głównego aplikacji
     * @param _containerName - nazwa panelu głównego użytkownika, wyświetlanego w kontenerze głównym aplikacji
     * @param _card - kontener karty wyświetlanej w panelu głównym użytkownika
     * @param _cardName - nazwa karty, którą chcemy wyświetlić
     */
    public ViewSwitcher( Container _mainContainer, String _containerName, Container _card, String _cardName ) {
        mainContainer = _mainContainer;
        containerName = _containerName;
        card = _card;
        cardName = _cardName;
    }
}
