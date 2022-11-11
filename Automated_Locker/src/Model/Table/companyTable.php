<?php
//https://book.cakephp.org/4/en/orm/table-objects.html
namespace App\Model\Table;

use Cake\ORM\Table;
//https://book.cakephp.org/4/en/orm/validation.html
//https://api.cakephp.org/4.1/class-Cake.Validation.Validator.html
use Cake\Validation\Validator;

class companyTable extends Table
{
    public function initialize(array $config): void{

        $this->setDisplayField("Company_Name");

        $this->hasOne("country",[
            'foreignKey' => 'Country_ID',
            'bindingKey' => 'Country_ID',
            'Type' => 'LEFT'
        ]);
        $this->hasOne("company_sector",[
            'foreignKey' => 'Company_Sector_ID',
            'bindingKey' => 'Company_Sector_ID',
            'Type' => 'LEFT'
        ]);
    }
}

