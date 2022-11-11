<div class="row">   
    <div class="col-6 offset-3 mb-5">
<h3 class="text-center mt-5">Add Employees</h3>
<?php
    echo $this->Form->create(null, ["class" => "m-5"]);
    echo "<div class='row'>
         <div class='form-group p-3 col-12'>";
         echo $this->Form->select("allusers", $allusers, ["class" => "form-control mr-3","id" =>"allusers"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 col-12'>";
    echo $this->Form->select("allcompanies", $allcompanies, ["class" => "form-control","id" =>"allcompanies"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group col-12'>";
    echo $this->Form->input("System_ID",["class" => "form-control mr-3","id"=>"input_system_Id","placeholder"=>"System ID"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 col-12'>";
    echo $this->Form->select("alloccupation", $alloccupation, ["class" => "form-control","id" =>"alloccupation"]);
    echo "</div></div>";
    echo $this->Form->button("Add Employee",["class"=>"btn btn-primary mr-2 btn-lg","type" =>"submit","id"=>"addEmployeeBTN"]);
    echo $this->Form->button("Generate System ID",["id"=>"GenBTN","class"=>"btn btn-danger btn-lg","type" =>"button"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
</div>
