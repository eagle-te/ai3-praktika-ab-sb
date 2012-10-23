<?php
class Application{
    
    private function __construct(){
        Registry::registerController();
        $this->main();

    }
    public static function start(){
        return new Application();
    }
    public function main(){
        $this->route();
    }
    public function route(){
        $pagename = Registry::$controller[strtolower( $this->getRequestedPage($_SERVER['REQUEST_URI']))];
        if(empty($pagename)){
            $pagename = Registry::$controller["default"];
        }
        $controller = $this->getController($pagename);
        $controller->showPage(Request::val("get","post"));
    }
    public function getRequestedPage($uri){
        $length = strlen("/".Registry::$settings["config"]["path"]."/");
        $page = substr($uri,$length,strlen($uri));
        $getPosition = strpos($page,"?");
        if($getPosition>0){
            $page = substr($page,0,$getPosition);
        }
        return $page;
    }
    public function getController($pageName){
        return new $pageName();
    }
}

?>