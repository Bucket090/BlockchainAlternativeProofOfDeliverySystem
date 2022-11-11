<div class="jumbotron jumbotron-fluid text-center bg-light p-5 m-auto">
    <div class="container">
      <h1 class="display-4 p-4 mt-4">View Employees</h1>
  </div>
  </div>
  <div class="row">
  <?= '<a href="'.$this->Url->Build('/Employees/add').'" class="btn btn-primary ml-auto mr-5 mt-4 p-3"><i class="fas fa-user-plus" style="font-size:20px"></i></a>';?>
  </div>
<div class="row justify-content-start">
<?php
foreach($allemployees as $employee){
  
  if ($this->Identity->get("Role_ID") == 3) {
      echo '<div class="card shadow border border-light ml-5 mt-4" style="width:26rem">
            <div class="bg-light text-center">
            <img src="'.$this->Url->image("default-image.png").'" class=" mt-1 m-auto mb-2" alt="'.$employee->user->Company_Name.'" style="width:350px">
            </div>
            <div class="card-body border-top">
            <span>Employee Name:</span>
            <h4 class="card-title font-weight-bold">'.$employee->user->First_Name . ' '. $employee->user->Last_Name .'</h4>
            <span>Employee Email:</span>
            <h6 class="card-title font-weight-bold">'.$employee->user->Email.'</h6>
            <span>Employer:</span>
            <h6 class="card-title font-weight-bold">'.$employee->company->Company_Name.'</h6>
            
            <a href="'.$this->Url->Build('/Employees/removeEmployee/'.$employee->System_ID).'" class="btn btn-danger btn-lg mr-3"><i class="fas fa-user-times"></i></a>
            </div>  
            </div>  
          </div>';
    }
}
?>
</div>

