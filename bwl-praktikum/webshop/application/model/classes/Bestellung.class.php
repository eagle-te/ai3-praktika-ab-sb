<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Bestellung
 *
 * @author abg667
 */
class Bestellung {
    private $bestellNr;
    private $kundenNr;
    private $bestellDatum;
    private $bestellteArtikel = array();
    private $derKunde;
    
    public function __construct($bestellNr, $kundenNr, $bestellDatum) {
        $this->bestellNr = $bestellNr;
        $this->kundenNr = $kundenNr;
        $this->bestellDatum = $bestellDatum;
    }
    
    public static function neu($bestellNr,$kundenNr,$bestellDatum){
        
        return new Bestellung($bestellNr, $kundenNr, $bestellDatum);
    }
    
    //Getter & Setter
    public function getBestellNr() {
        return $this->bestellNr;
    }

    public function setBestellNr($bestellNr) {
        $this->bestellNr = $bestellNr;
    }

    public function getKundenNr() {
        return $this->kundenNr;
    }

    public function setKundenNr($kundenNr) {
        $this->kundenNr = $kundenNr;
    }

    public function getBestellDatum() {
        return $this->bestellDatum;
    }

    public function setBestellDatum($bestellDatum) {
        $this->bestellDatum = $bestellDatum;
    }

    public function getBestellteArtikel(){
        return $this->bestellteArtikel;
    }
    public function addArtikel($artikel,$menge){
        if(!($artikel instanceof Teile)) die("Bestellung::addArtikel::illegalArgumentException");

        $this->bestellteArtikel[$menge] = $artikel;
    }
    public function addDerKunde($kunde){
        if(!($kunde instanceof Kunde)) die("Bestellung::addDerKunde::illegalArgumentException");
        $this->derKunde = $kunde;
    }

}

?>
