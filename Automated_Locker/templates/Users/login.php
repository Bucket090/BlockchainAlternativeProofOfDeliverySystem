<div class="row w-50 m-auto mb-5">   
    <div class="col-6 offset-3 mb-5">

<div class="d-flex justify-content-center">
<h2 class="text-center mt-5">Login System</h2>
</div>
<hr>
<?php
    echo $this->Form->create(null,["class"=>"border border-light shadow p-5"]);
    echo"<div class='form-group'>";
    echo $this->Form->control("Email", ["class" => "form-control", "placeholder" => "Email"]);
    echo"</div>
    <div class='form-group'>";
    echo $this->Form->control("Password", ["type" => "password","class" => "form-control", "placeholder" => "Password"]);
    echo"</div>";
    echo $this->Form->button("Login",["class"=>"btn btn-lg btn-dark btn-block text-uppercase mt-5","type" =>"submit"]);
    echo "<div class='row justify-content-center'>
    <a class='text-primary mt-4' href='".$this->Url->Build('/users/register')."' style='text-decoration: none;font-size: 14px;'>Not Registered Yet? Sign Up</a>
    </div>";
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>  
</div>
</div>