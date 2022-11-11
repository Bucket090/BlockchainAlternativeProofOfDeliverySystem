<div class="jumbotron jumbotron-fluid text-center bg-light p-5 m-auto">
    <div class="container">
    <?php 
    if ($this->Identity->get("Role_ID") == 3) // Role as Admin
    {
      echo '<h1 class="display-4 p-4 mt-4">Users List</h1>';
    }else{
      echo '<h1 class="display-4 p-4 mt-4">My Profile</h1>';
    }
    ?>
  </div>
  </div>
<div class="row justify-content-start">
<?php
foreach($allusers as $user){
  
  if ($user->User_ID == $this->Identity->get("User_ID") || $this->Identity->get("Role_ID") == 3) {
      echo '<div class="card shadow border border-light ml-5 mt-4" style="width:26rem">
            <div class="bg-light text-center">
            <img src="'.$this->Url->image("default-image.png").'" class=" mt-1 m-auto mb-2" alt="'.$user->First_Name.'" style="width:350px">
            </div>
            <div class="card-body border-top">
            <span>Full Name:</span>
            <h4 class="card-title font-weight-bold">'.$user->First_Name .' '. $user->Last_Name .'</h4>
            <span>Email:</span>
            <h6 class="card-title font-weight-bold">'.$user->Email.'</h6>
            <span>Role:</span>
            <h6 class="card-title font-weight-bold">'.$user->role->Role_Name.'</h6>
            <span>Crypto Wallet:</span>';
            if($user->Wallet_Address != null){
            echo '<h6 class="card-sub font-weight-bold mb-3">'.$user->Wallet_Address.'</h6>';
            }else{
              echo '<h6 class="card-sub font-weight-bold mb-3">NULL</h6>';
            }
            echo '<div class="d-flex justify-content-end">
            <a href="'.$this->Url->Build('/Users/deleteUser/'.$user->User_ID).'" class="btn btn-danger btn-lg mr-3"><i class="fas fa-user-times"></i></a>
            <a href="'.$this->Url->Build('/Users/edit/'.$user->User_ID).'" class="btn btn-success btn-lg"><i class="fas fa-edit"></i></a>
            </div>  
            </div>  
          </div>';
    }
}
?>
</div>

