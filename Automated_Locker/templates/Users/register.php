<div class="row w-50 m-auto">   
    <div class="col-6 offset-3 mb-5">
<h2 class="text-center mt-5">Register User</h2>
<hr>
<?php
    echo $this->Form->create(null,["class"=>"border border-light shadow p-5"]);
    echo"<div class='form-group'>";
    echo $this->Form->control("First_Name", ["class" => "form-control", "placeholder" => "Name"]);
    echo"</div>
    <div class='form-group'>";
    echo $this->Form->control("Last_Name", ["class" => "form-control", "placeholder" => "Surname"]);
    echo"</div> 
    <div class='form-group'>";
    echo $this->Form->control("Email", ["class" => "form-control", "placeholder" => "Email"]);
    echo"</div>
    <div class='form-group'>";
    echo $this->Form->control("Password", ["type" =>"password","class" => "form-control", "placeholder" => "Password"]);
    echo"</div>";
    echo "<div class='form-group'>";
    echo $this->Form->control("Role_ID", ["type" => "hidden", "value" => 1]);
    echo"</div>";
    echo $this->Form->button("Register",["class"=>"btn btn-lg btn-dark btn-block text-uppercase mt-5","type" =>"submit"]);
    echo "<div class='row justify-content-center'><a class='text-primary mt-3' href='".$this->Url->Build('/Users/login')."' style='text-decoration: none;font-size: 14px;'>Already Registered? Sign In</a></div>";
    echo $this->Form->end();
    echo $this->Flash->render();
    
?>
</div>
</div>
</div>