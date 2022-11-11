<?php
//https://book.cakephp.org/4/en/orm/table-objects.html
namespace App\Model\Table;

use Cake\ORM\Table;
//https://book.cakephp.org/4/en/orm/validation.html
//https://api.cakephp.org/4.1/class-Cake.Validation.Validator.html
use Cake\Validation\Validator;

class users_companyTable extends Table
{
    public function initialize(array $config): void{

        $this->setPrimaryKey("User_ID");

        $this->belongsTo("users",[
            'foreignKey' => "User_ID",
            'bindingKey' => "User_ID",
            'Type' => 'LEFT'
        ]);
        $this->belongsTo("company",[
            'foreignKey' => "Company_ID",
            'bindingKey' => "Company_ID",
            'Type' => 'LEFT'
        ]);
    }
}

