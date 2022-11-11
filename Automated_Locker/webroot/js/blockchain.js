var contract;
var abi;
function initalize(){
    web3 = new Web3(web3.currentProvider);
    contract_address = "0x69771d73FF2643f32472aC45e326ddc78FB8e3cF";
    address = window.ethereum.selectedAddress;
    abi = [
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "_deliveryPersonAddress",
                    "type": "address"
                },
                {
                    "internalType": "string",
                    "name": "_employeer",
                    "type": "string"
                },
                {
                    "internalType": "string",
                    "name": "_systemId",
                    "type": "string"
                }
            ],
            "name": "AddDeliveryPerson",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "string",
                    "name": "_password",
                    "type": "string"
                }
            ],
            "name": "CollectParcel",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "_ownerAddress",
                    "type": "address"
                },
                {
                    "internalType": "string",
                    "name": "_systemId",
                    "type": "string"
                },
                {
                    "internalType": "uint256",
                    "name": "_onetimeCode",
                    "type": "uint256"
                },
                {
                    "internalType": "int256",
                    "name": "TotalPackages",
                    "type": "int256"
                }
            ],
            "name": "DeliveryPersonAccessLocker",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "_ownerAddress",
                    "type": "address"
                },
                {
                    "internalType": "uint256",
                    "name": "_onetimeCode",
                    "type": "uint256"
                }
            ],
            "name": "GetOneTimeCode",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "string",
                    "name": "_LockerRegistrationID",
                    "type": "string"
                },
                {
                    "internalType": "string",
                    "name": "_password",
                    "type": "string"
                }
            ],
            "name": "RegisterLocker",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                }
            ],
            "name": "deliveryPersons",
            "outputs": [
                {
                    "internalType": "address",
                    "name": "deliveryPersonAddress",
                    "type": "address"
                },
                {
                    "internalType": "string",
                    "name": "Employeer",
                    "type": "string"
                },
                {
                    "internalType": "string",
                    "name": "systemId",
                    "type": "string"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                }
            ],
            "name": "owners",
            "outputs": [
                {
                    "internalType": "address",
                    "name": "ownerAddress",
                    "type": "address"
                },
                {
                    "internalType": "string",
                    "name": "LockerID",
                    "type": "string"
                },
                {
                    "internalType": "bool",
                    "name": "isLocked",
                    "type": "bool"
                },
                {
                    "internalType": "bytes32",
                    "name": "password",
                    "type": "bytes32"
                },
                {
                    "internalType": "uint256",
                    "name": "oneTimeCode",
                    "type": "uint256"
                },
                {
                    "internalType": "bool",
                    "name": "isCodeValid",
                    "type": "bool"
                },
                {
                    "internalType": "int256",
                    "name": "totalParcelsReceived",
                    "type": "int256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "string",
                    "name": "_password",
                    "type": "string"
                }
            ],
            "name": "totalParcelsReceived",
            "outputs": [
                {
                    "internalType": "int256",
                    "name": "",
                    "type": "int256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        }
    ];
    contract = new web3.eth.Contract(abi,contract_address); 
}
$(document).ready(function(){
    initalize();
});
$(document).on("load",function(){
    initalize();
});

/* OWNER FUNCTIONS */
$("#registerBTN").on("click", function(){
    initalize();
    var input_registrationID = $("#Register_Name").val();
    var input_lockerCode = $("#Locker_Code").val();
        if(input_lockerCode.length == 4){
            $("#Locker_Code").val("");
            $("#Register_Name").val("");
            contract.methods.RegisterLocker(input_registrationID,input_lockerCode).send({from: address,gas:210000});
            window.location.reload();
        }
        else
            alert("locker Code must be 4 digits");
});

$("#getTransactions").on("click", function(){
    $(".spinner-border").css("display","block");
    $("#error_msg").css("display","none");
    $("#blockchain_transction_container").css("display","none");
    initalize();
    contract.methods.owners(address).call()
    .then(function(result){
        console.log(result);
        $(".spinner-border").css("display","none");
        $("#blockchain_transction_container").css("display","block");
        $("#locker_reg_ID").html(result.LockerID);
        $("#owner").html(result.ownerAddress);
        $("#islocked").html(result.isLocked);
    }).catch(function(err){
        $(".spinner-border").css("display","none");
        $("#error_msg").css("display","block");
        $("#error_msg").html(err.message);
    });
});
$("#getParcel").on("click", function(){
    initalize();
    contract.methods.owners(address).call().then(function(result){
        console.log(result);
        $("#total_parcels_received").html(result.totalParcelsReceived);
    }).catch(function(error){
        $("#total_parcels_received").html(error.message);
    })
});

$("#collectParcel").on("click",async function(){
    initalize();
    var code = String(prompt("Enter Locker Password:"));
        await contract.methods.CollectParcel(code).send({from: address,gas:210000}).then(function(result){
            log(result);
            $("#total_parcels_received").html(result.totalParcelsReceived);
        }).then(function(tx){
            console.log(tx);
        }).catch(function(error){
            console.log(error);
        });
});


/*WORKER FUNCTIONS */
$("#addEmployeeBTN").on("click", async function(){
    initalize();
    var input_company = $("#allcompanies").find(":selected").text();
    var input_system = $("#input_system_Id").val();
    await contract.methods.AddDeliveryPerson(address,input_company,input_system).send({from: address,gas:210000});
});


$("#getTransactionsWorker").on("click", function(){
    initalize();
    $(".spinner-border").css("display","block");
    $("#error_msg").css("display","none");
    $("#blockchain_transction_container").css("display","none");

    contract.methods.deliveryPersons(address).call()
    .then(function(result){
        console.log(result);
        $(".spinner-border").css("display","none");
        $("#blockchain_transction_container").css("display","block");
        $("#worker_wallet").html(result.deliveryPersonAddress);
        $("#system_id").html(result.systemId);
        $("#worker_employeer").html(result.Employeer);
    }).catch(function(err){
        $(".spinner-border").css("display","none");
        $("#error_msg").css("display","block");
        $("#error_msg").html(err.message);
    });
});
   
$("#getOneTimeCode").on("click",function(){
    initalize();
    var owner_address = prompt("Enter Owner Address:");
    var gen_code = Math.floor(Math.random() * 1000); 
    if(owner_address != address)
        contract.methods.GetOneTimeCode(owner_address,gen_code).send({from: address,gas:210000}).then(function(){
            contract.methods.owners(owner_address).call().then(function(result){
                $("#gen_code").html(result.oneTimeCode);
                $("#valid_code").html(result.isCodeValid);
            }).catch(function(error){
                console.log(error);
            });
        }).catch(function(error){
            console.log(error);
        })
    else
        alert("You have the same address as the owner");
});
$("#lastValidCode").on("click",function(){
    initalize();
    var owner_address = prompt("Enter Owner Address:");
    if(owner_address != address)
            contract.methods.owners(owner_address).call().then(function(result){
                console.log(result);
                $("#gen_code").html(result.oneTimeCode);
                $("#valid_code").html(result.isCodeValid);
            }).catch(function(error){
                console.log(error);
            });
    else
        alert("You have the same address as the owner");
});

$("#showaddressBTN").on("click",function(){
    initalize();
    $("#owner_address_container").toggle();
});

$("#openLockerBTN").on("click", async function(){
    initalize();
    var input_owner_address = $("#Owner_Address").val();
    var input_system_id = $("#System_ID").val();
    var input_one_time_code = parseInt($("#One_Time_Code").val());
    var input_total_parcel = parseInt($("#total_parcel").val());
    $("#Owner_Address").val("");
    $("#System_ID").val("");
    $("#One_Time_Code").val("");
    $("#total_parcel").val("");
    await contract.methods.DeliveryPersonAccessLocker(input_owner_address,input_system_id,input_one_time_code,input_total_parcel).send({from: window.ethereum.selectedAddress,gas:210000});
    window.location.reload();
});