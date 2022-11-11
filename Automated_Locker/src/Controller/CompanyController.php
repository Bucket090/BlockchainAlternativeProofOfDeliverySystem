<?php
namespace App\Controller;

use \Cake\ORM\Locator\LocatorAwareTrait;
use \Cake\ORM\Table;
use Cake\ORM\TableRegistry;

class CompanyController extends AppController{


    public function view(){
        $companyTable = $this->getTableLocator()->get("company");
        $allcompanies = $companyTable->find()->contain(['company_sector','country'])->ToArray();

        $this->set("allcompanies",$allcompanies);
    }

    public function deleteCompany($company_Id){
        if(!is_numeric($company_Id)){
            $this->Flash("Company Id is null.");
        }
        try {
            $companyTable = $this->getTableLocator()->get("company");
            $deleteCompany = $companyTable->find()->where(['Company_ID ' => $company_Id])->first();

            if ($companyTable->delete($deleteCompany)) {
                $this->Flash->success("Company has been removed");
            }
        } catch(Exception $e){
            $this->Flash->error("Company cannot be deleted: ".$e->getMessage());
        }

        return $this->redirect(["action" => "view"]);
    }

    public function edit($company_Id){
        $company_sectorTable = $this->getTableLocator()->get("company_sector");
        $allsectors = $company_sectorTable->find("list",array('order' => array('Company_Sector_ID' => 'ASC')))->toArray();

        $countryTable = $this->getTableLocator()->get("country");
        $allcountries = $countryTable->find("list",array('order' => array('Country_ID' => 'ASC')))->toArray();

        $companyTable = $this->getTableLocator()->get("company");
        $mycompany = $companyTable->find()->contain(['company_sector','country'])->where(['Company_ID' => $company_Id])->toArray();
        
        $this->set("mycompany",$mycompany);
        $this->set("allsectors",$allsectors);
        $this->set("allcountries",$allcountries);
        
        //Updating Company
        self::UpdateCompany($company_Id);
    }

    public function add(){
        $company_sectorTable = $this->getTableLocator()->get("company_sector");
        $allsectors = $company_sectorTable->find("list",array('order' => array('Company_Sector_ID' => 'ASC')))->toArray();

        $countryTable = $this->getTableLocator()->get("country");
        $allcountries = $countryTable->find("list",array('order' => array('Country_ID' => 'ASC')))->toArray();
        
        $this->set("allsectors",$allsectors);
        $this->set("allcountries",$allcountries);
        
        // Adding a new Company
        self::AddCompany();        
    }

    protected function AddCompany(){
        if ($this->request->is('post')) {
            $newCompanyTable = $this->getTableLocator()->get("company");
            $addCompany = $newCompanyTable->newEmptyEntity();
            $addCompany->Company_Name = $_POST['Company_Name'];
            $addCompany->Company_Spoke_Person = $_POST['Company_Spoke_Person'];
            $addCompany->Company_Phone_Number = $_POST['Company_Phone_Number'];
            $addCompany->Company_Email = $_POST['Company_Email'];
            $addCompany->Company_Sector_ID = $_POST['allsectors'];
            $addCompany->Company_street = $_POST['Company_street'];
            $addCompany->Country_ID = $_POST['allcountries'];

            if ($newCompanyTable->save($addCompany)) {
                // entity saved
                $this->Flash->success("New Company Added!!");
                return $this->redirect(["action" => "view"]);
            }
        }
    }
    
    protected function UpdateCompany($company_Id){
        if ($this->request->is('post')) {
            $CompanyTable = $this->getTableLocator()->get("company");
            $currentCompany = $CompanyTable->get($company_Id);
            
            $currentCompany->Company_Name = $_POST['Company_Name'];
            $currentCompany->Company_Spoke_Person = $_POST['Company_Spoke_Person'];
            $currentCompany->Company_Phone_Number = $_POST['Company_Phone_Number'];
            $currentCompany->Company_Email = $_POST['Company_Email'];
            $currentCompany->Company_Sector_ID = $_POST['allsectors'];
            $currentCompany->Company_street = $_POST['Company_street'];
            $currentCompany->Country_ID = $_POST['allcountries'];
     
            if ($CompanyTable->save($currentCompany)) {
                // entity saved
                $this->Flash->success("Company Updated !!");
                return $this->redirect(["action" => "view"]);
            }
        }
    }
}
