<div class="row">   
    <div class="col-6 offset-3 mb-5">
<h3 class="text-center mt-5">Add Company</h3>
<?php
    echo $this->Form->create(null, ["class" => "m-5"]);
    echo "<div class='row'>
         <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("Company_Name",["class" => "form-control mr-2","placeholder"=>"Company Name","type" => "text","required"]);
    echo $this->Form->input("Company_Spoke_Person",["class" => "form-control","placeholder"=>"Spoke Person","type" => "text","required"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("Company_Email",["class" => "form-control mr-3","placeholder"=>"Email","type" => "email","required"]);
    echo $this->Form->input("Company_Phone_Number",["class" => "form-control","placeholder" =>"Phone Number","type" => "text","required"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group col-12'>";
    echo $this->Form->input("Company_street",["class" => "form-control mr-3","placeholder"=>"Street Address","type" => "text","required"]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 d-flex col'>";
    echo $this->Form->select("allsectors", $allsectors, ["class" => "form-control mr-3","id" =>"allsectors"]);
    echo $this->Form->select("allcountries", $allcountries, ["class" => "form-control","id" =>"allcountries"]);
    echo "</div></div>";
    echo $this->Form->button("Add Company",["class"=>"btn btn-primary btn-lg","type" =>"submit"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
</div>  

