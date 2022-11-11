<?php
//https://book.cakephp.org/4/en/orm/table-objects.html
namespace App\Model\Table;

use Cake\ORM\Table;
//https://book.cakephp.org/4/en/orm/validation.html
//https://api.cakephp.org/4.1/class-Cake.Validation.Validator.html
use Cake\Validation\Validator;

class occupationTable extends Table
{
    public function initialize(array $config): void{

        $this->setDisplayField("Occupation_Name");      
    }
}

