<?php
/**
 * Created by IntelliJ IDEA.
 * User: Sven
 * Date: 24.10.12
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
class Topbar_MainPage  implements Topbar
{
    private $dataMap;
    public function show()
    {
     echo('
    <div id="topbar" >
        <div id="logo" style="float:left;width: 30%;height: 100%"> LOGO</div>
        <div id="buttonbar" style="float: left;margin-left: 20%;width: 50%;height: 100%">
            <div id="btn1" style="float:left;">'.$this->dataMap["btn1"].'</div>
            <div id="btn2" style="float:left;">'.$this->dataMap["btn2"].'</div>
            <div id="btn3" style="float:left;">'.$this->dataMap["btn3"].'</div>
        </div>
    </div>
    ');
    }

    public static function create()
    {
        return new Topbar_MainPage();
    }
    public function applyData(array $dataMap)
    {
        $this->dataMap = $dataMap;
    }
}

?>