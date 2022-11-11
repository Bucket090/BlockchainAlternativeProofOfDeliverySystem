<div class="row">   
    <div class="col-6 offset-3 mb-5">
<h3 class="text-center mt-5">Update <?=$mycompany[0]->Company_Name?></h3>
<?php
    echo $this->Form->create(null, ["class" => "m-5"]);
    echo "<div class='row'>
         <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("Company_Name",["class" => "form-control mr-2","placeholder"=>"Company Name","value" => $mycompany[0]->Company_Name]);
    echo $this->Form->input("Company_Spoke_Person",["class" => "form-control","placeholder"=>"Spoke Person","value" => $mycompany[0]->Company_Spoke_Person]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("Company_Email",["class" => "form-control mr-3","placeholder"=>"Email","value" => $mycompany[0]->Company_Email]);
    echo $this->Form->input("Company_Phone_Number",["class" => "form-control","placeholder" =>"Phone Number","value" => $mycompany[0]->Company_Phone_Number]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group col-12'>";
    echo $this->Form->input("Company_street",["class" => "form-control mr-3","placeholder"=>"Street Address","value" => $mycompany[0]->Company_street]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 d-flex col'>";
    echo $this->Form->select("allsectors", $allsectors, ["class" => "form-control mr-3","id" =>"allsectors","value" => $mycompany[0]->Company_Sector_ID]);
    echo $this->Form->select("allcountries", $allcountries, ["class" => "form-control","id" =>"allcountries","value" => $mycompany[0]->Country_ID]);
    echo "</div></div>";
    echo $this->Form->button("Update Company",["class"=>"btn btn-primary btn-lg","type" =>"submit"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
</div>
