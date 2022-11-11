<?php
namespace App\Controller;

use \Cake\ORM\Locator\LocatorAwareTrait;
use \Cake\ORM\Table;
use Cake\ORM\TableRegistry;

class DashboardController extends AppController{

    public function owner(){

    }
    public function worker(){
        $session = $this->request->getSession();
        $userCompanyTable = $this->getTableLocator()->get("users_company");
        $userCompanyQuery = $userCompanyTable->find()->contain(["company"])
        ->where(['users_company.User_ID' => $session->consume("Auth.User_ID")])->toArray();
        
        $this->set("userCompanyQuery", $userCompanyQuery);
    }
}