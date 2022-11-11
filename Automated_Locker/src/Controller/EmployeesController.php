<?php
namespace App\Controller;

use \Cake\ORM\Locator\LocatorAwareTrait;
use \Cake\ORM\Table;
use Cake\ORM\TableRegistry;

class EmployeesController extends AppController{

    public function view($company_Id){
        $users_companyTable = $this->getTableLocator()->get("users_company");
        $allemployees = $users_companyTable->find()->contain(['users','company'])->where(['users_company.Company_ID' => $company_Id])->ToArray();

        $this->set("allemployees",$allemployees);
    }

    public function removeEmployee($System_ID){
        if(!is_numeric($System_ID)){
            $this->Flash("Employee Id is null.");
        }
        try {
            $usersCompanyTable = $this->getTableLocator()->get("users_company");
            $removeEmployee = $usersCompanyTable->find()->where(['System_ID ' => $System_ID])->first();

            if ($usersCompanyTable->delete($removeEmployee)) {
                $this->Flash->success("Employee has been removed");
            }
        } catch(Exception $e){
            $this->Flash->error("Employee cannot be deleted: ".$e->getMessage());
        }

        return $this->redirect(['controller' => 'Company', 'action' => 'view']);
    }

    public function add(){
        $userTable = $this->getTableLocator()->get("users");
        $allusers = $userTable->find("list",array('order' => array('users.User_ID' => 'ASC')))->toArray();

        $companyTable = $this->getTableLocator()->get("company");
        $allcompanies = $companyTable->find("list",array('order' => array('Company_ID' => 'ASC')))->toArray();
       
        $occupationTable = $this->getTableLocator()->get("occupation");
        $alloccupation = $occupationTable->find("list",array('order' => array('Occupation_ID' => 'ASC')))->toArray();

        $this->set("allusers",$allusers);
        $this->set("allcompanies",$allcompanies);
        $this->set("alloccupation",$alloccupation);
        //Adding Employee
        self::addEmployee();
    }
    public function addEmployee(){
        if ($this->request->is('post')) {
            $usersCompanyTable = $this->getTableLocator()->get("users_company");
            $addEmployee = $usersCompanyTable->newEmptyEntity();
            $addEmployee->User_ID = $_POST['allusers'];
            $addEmployee->Company_ID = $_POST['allcompanies'];
            $addEmployee->System_ID = $_POST['System_ID'];
            $addEmployee->Occupation_ID = $_POST['alloccupation'];
            if ($usersCompanyTable->save($addEmployee)) {
                self::updateRole($_POST['allusers']);
            }
        }
    }

    public function updateRole($user_Id){

        $usersTable = $this->getTableLocator()->get("users");
        $user = $usersTable->get($user_Id);
        $user->Role_ID = 2; // Worker Role
        if ($usersTable->save($user)) {
                // entity saved
            $this->Flash->success("New Employee Added!!");
                return $this->redirect(['controller' => 'Company', 'action' => 'view']);
        }
    }
}
