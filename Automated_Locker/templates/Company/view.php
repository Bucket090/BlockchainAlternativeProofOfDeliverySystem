<div class="jumbotron jumbotron-fluid text-center bg-light p-5 m-auto">
    <div class="container">
      <h1 class="display-4 p-4 mt-4">Company List</h1>
  </div>
  </div>
  <div class="row">
  <?= '<a href="'.$this->Url->Build('/Company/add').'" class="btn btn-primary ml-auto mr-5 mt-4 p-3"><i class="fas fa-user-plus" style="font-size:20px"></i></a>';?>
  </div>
<div class="row justify-content-start">
<?php
foreach($allcompanies as $company){
  
  if ($company->Company_ID != $this->Identity->get("user_Id") || $this->Identity->get("user_Id")) {
      echo '<div class="card shadow border border-light ml-5 mt-4" style="width:26rem">
            <div class="bg-light text-center">
            <img src="'.$this->Url->image("default-company-image.png").'" class=" mt-1 m-auto mb-2" alt="'.$company->Company_Name.'" style="width:350px">
            </div>
            <div class="card-body border-top">
            <span>Company:</span>
            <h4 class="card-title font-weight-bold">'.$company->Company_Name .'</h4>
            <span>Spoke Person:</span>
            <h6 class="card-title font-weight-bold">'.$company->Company_Spoke_Person.'</h6>
            <span>Email:</span>
            <h6 class="card-title font-weight-bold">'.$company->Company_Email.'</h6>
            <span>Phone Number:</span>
            <h6 class="card-title font-weight-bold">'.$company->Company_Phone_Number.'</h6>  
            <span>Sector:</span>
            <h6 class="card-title font-weight-bold">'.$company->company_sector->Company_Type.'</h6>  
            <span>Country:</span>
            <h6 class="card-title font-weight-bold">'.$company->country->Country_Name.'</h6>  
            <div class="d-flex justify-content-end">
            
            <a href="'.$this->Url->Build('/Employees/view/'.$company->Company_ID).'" class="btn btn-warning text-light btn-lg mr-auto"><i class="fas fa-users"></i></a>
            <a href="'.$this->Url->Build('/Company/deleteCompany/'.$company->Company_ID).'" class="btn btn-danger btn-lg mr-3"><i class="fas fa-user-times"></i></a>
            <a href="'.$this->Url->Build('/Company/edit/'.$company->Company_ID).'" class="btn btn-success btn-lg"><i class="fas fa-edit"></i></a>
            </div>  
            </div>  
          </div>';
    }
}
?>
</div>

