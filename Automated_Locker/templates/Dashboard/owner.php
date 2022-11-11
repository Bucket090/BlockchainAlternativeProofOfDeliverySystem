<div class="jumbotron jumbotron-fluid text-center bg-light p-5 m-auto bg-dark">
    <div class="container">
      <h1 class="display-4 p-5 mt-4 text-light">Owner Dashboard</h1>
  </div>
  </div>
<div class="container-fluid row justify-content-center text-center mr-auto p-5">
<div class="col-3 order-1 m-auto">
<h3>Register Locker</h3>

<?php
echo $this->Form->create(null, ["class" => "m-3","id"=>"register-frm w-100"]);
    echo "<div class='row'>
         <div class='form-group p-3 col-12'>";
         echo $this->Form->input("RegistrationID",["class" => "form-control mr-3","placeholder"=>"Locker Registration ID","id"=>"Register_Name"]);
         echo $this->Form->input("Locker_Code",["class" => "form-control mr-3","placeholder"=>"Locker Code","id"=>"Locker_Code","type" =>"number"]);
    echo "</div></div>";
    echo $this->Form->button("Register",["class"=>"btn btn-dark mr-2 w-100 btn-lg","type" =>"submit","id"=>"registerBTN"]);
    echo $this->Form->end();
    echo $this->Flash->render();
?>
</div>
<div class="col-3 order-3">
<h3>Owner Features</h3>
<button class="btn btn-danger" id="getParcel">Check Parcel Received</button>
<button class="btn btn-dark" id="collectParcel">Collect Parcel Locker</button>
<div class="container d-flex" id="parcels">
  <h6 class="ml-5">Parcels Received:</h6><p class="font-weight-bold ml-4" id="total_parcels_received">0</p>
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
    <h5 class="ml-3">Locker Registration ID:</h5><p class="font-weight-bold ml-4" id="locker_reg_ID"></p>
    </div>
    <div class="row d-flex m-auto justify-content-center">
    <h5 class="ml-3">Owner:</h5><p class="font-weight-bold ml-4" id="owner"></p>
    </div>
    <div class="row d-flex m-auto justify-content-center">
    <h5 class="ml-3">Locked:</h5><p class="font-weight-bold ml-4" id="islocked"></p>
    </div>
</div>

<button class="btn btn-primary mt-5" id="getTransactions">Get Transactions</button>
</div>
</div>