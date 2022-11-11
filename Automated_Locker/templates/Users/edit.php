<div class="row">   
    <div class="col-6 offset-3 mb-5">
<h3 class="text-center mt-5">Update User</h3>
<?php
    echo $this->Form->create(null, ["class" => "m-5"]);
    echo "<div class='row'>
         <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("First_Name",["class" => "form-control mr-2","placeholder"=>"First Name","value" => $myuser[0]->First_Name]);
    echo $this->Form->input("Last_Name",["class" => "form-control","placeholder"=>"Last Name","value" => $myuser[0]->Last_Name]);
    echo "</div></div>";
    echo "<div class='row'>
    <div class='form-group p-3 d-flex col'>";
    echo $this->Form->input("Wallet_Address",["class" => "form-control mr-3","placeholder"=>"Wallet Address","value" => $myuser[0]->Wallet_Address]);
    echo $this->Form->input("Email",["class" => "form-control mr-3","placeholder"=>"Email","value" => $myuser[0]->Email]);
    echo "</div></div>";
    echo $this->Form->button("Update User",["class"=>"btn btn-primary btn-lg","type" =>"submit"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
</div>
