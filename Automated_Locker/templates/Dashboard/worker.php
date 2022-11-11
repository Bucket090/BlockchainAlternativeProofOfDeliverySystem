<div class="jumbotron jumbotron-fluid text-center bg-light p-5 m-auto bg-dark">
    <div class="container">
      <h1 class="display-4 p-5 mt-4 text-light">Worker Dashboard</h1>
  </div>
  </div>
<div class="container-fluid row justify-content-center text-center p-5">
<div class="col-3 order-1">
<h3>Access Locker</h3>

<?php
echo $this->Form->create(null, ["class" => "m-3"]);
    echo "<div class='row'>
         <div class='form-group p-3 col-12'>";
         echo $this->Form->input("owner_address",["class" => "form-control mr-3","placeholder"=>"Owner Address","id"=>"Owner_Address"]);
         echo $this->Form->input("System_ID",["class" => "form-control mr-3","placeholder"=>"System ID","id"=>"System_ID","value"=> $userCompanyQuery[0]->System_ID,"readonly"]);
         echo $this->Form->input("one_time_code",["class" => "form-control mr-3","placeholder"=>"One Time Code","id"=>"One_Time_Code"]);
         echo $this->Form->input("total_parcel",["class" => "form-control mr-3","placeholder"=>"Total Parcels","id"=>"total_parcel"]);
         
         
    echo "</div></div>";
    echo $this->Form->button("Open Locker",["class"=>"btn btn-danger mr-2 w-100 btn-lg","type" =>"submit","id"=>"openLockerBTN"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
<div class="col-3 order-3">
<h3>Worker Features</h3>
<button class="btn btn-warning" id="getOneTimeCode">Get One Time Code</button>
<button class="btn btn-primary" id="lastValidCode">Show Last Valid Code</button>
<button class="btn btn-dark" id="showaddressBTN">Show Owner Address</button>
<div class="container d-flex justify-content-center mt-5" id="gen_code_container">
  <h4 class="ml-5 ">Code Generated:</h4><h4 class="font-weight-bold ml-4 mt-0" id="gen_code">0</h4>
  <h4 class="ml-5">Code Valid:</h4><h4 class="font-weight-bold ml-4 mt-0" id="valid_code">False</h4>
</div>
<div class="container justify-content-center mt-5" id="owner_address_container" style="display:none;">
  <h4 class="ml-5">Owner Address:</h4><p class="font-weight-bold ml-4 mt-0 text-danger">0x9DE18Cf037C5950525b7cdF66F3641903Dca5d02</p>
</div>
</div>
<div class="col-5 order-2 border-left border-right">
<h3>Blockchain Transcations</h3>
  <div class="spinner-border m-auto p-4 mb-5" style="display:none;" role="status">
    <span class="sr-only">Loading...</span>
  </div>
  <h4 class="font-weight-bold ml-4 text-danger" id="error_msg" style="display:none;"></h4>
  <h6 class="font-weight-bold ml-4" id="error_blockchain_transaction" style="display:none;"></h6>
  <div id="blockchain_transction_container" class="container" style="display:none;">
    <div class="row d-flex m-auto justify-content-center">
    <h5 class="ml-3">Worker:</h5><p class="font-weight-bold ml-4" id="worker_wallet"></p>
    </div>
    <div class="row d-flex m-auto justify-content-center">
    <h5 class="ml-3">System ID:</h5><p class="font-weight-bold ml-4" id="system_id"></p>
    </div>
    <div class="row d-flex m-auto justify-content-center">
    <h5 class="ml-3">Employeer:</h5><p class="font-weight-bold ml-4" id="worker_employeer"></p>
    </div>
</div>
<button class="btn btn-primary mt-5" id="getTransactionsWorker">Get Transactions</button>
</div>
</div>