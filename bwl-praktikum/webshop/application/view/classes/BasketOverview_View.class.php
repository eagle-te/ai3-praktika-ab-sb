<?php
class BasketOverview_View implements View{
    private $dataMap;
    public function applyData(array $dataMap) {
    $this->dataMap = $dataMap;
    }
    public function applyDataToView(array $dataMap) {
    $this->dataMap = $dataMap;
    }
    public function show() {
    ?>
        <div id="basketoverview">
            <div id="basket_table">
             <?php
                $whole_price = 0;
                $basket_entry =
            '<div class="basket_entry">
                <div class="basket_product_id">%s</div>
                <div class="basket_product_name">%s</div>
                <div class="basket_product_price">%s</div>
                <div class="basket_product_count"><input id="product_%s_count" type="text" size="4" value="%s"></div>
                <div class="basket_product_wholeprice">%s</div>
                <div class="basket_product_remove" onclick="javascript:removeItems(%s,%s);">%s</div>

            </div>';
                echo(sprintf($basket_entry,
                    "Artikel Nr.",
                    "Name",
                    "Preis",
                    0,"Menge",
                    "Gesamt Preis",
                    0,"Menge",
                    ""));
                if(count($this->dataMap['products'])>0){
                    foreach ($this->dataMap['products'] as $product){
                        $itemID = $product->getTeileNr();
                        $price = $product->getPreis();
                        $count = $this->dataMap['basket'][$itemID];
                        $productName = $product->getBezeichnung();
                        $whole_price = ($price*$count)+$whole_price;
                        echo(sprintf($basket_entry,
                                $itemID,
                                "<a href=\"products?search=$productName\">$productName</a>",
                                number_format($price,2,',',''),
                                $itemID,$count,
                            number_format(($price*$count), 2, ',', ''),
                                $itemID,$count,
                                "Entfernen"));
                    }
                }
            ?>
            <div id="basket_whole_price">Gesamtpreis:<div id="gesamtpreis"><?php echo(number_format($whole_price, 2, ',', '')) ?></div></div>
            <div id="bestellen" onclick="javascript:bestellen();"> zur Kasse</div>
            <div id="lieferdatum">Lieferdatum:<input id="day" value="tag"><input id="month" value="monat"><input id="year" value="jahr"></div>

            </div>
        </div>
    <?php
    }
    public static function create() {
            return new BasketOverview_View();
        }
}
?>