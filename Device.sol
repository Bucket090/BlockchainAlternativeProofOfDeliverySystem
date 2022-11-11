// SPDX-License-Identifier: MIT
pragma solidity =0.8.10;

contract Device{
    
    struct OwnerDevice{
        address ownerAddress; // this will be stored in a form of QR Code 
        string DeviceID;
        bool isDeviceLocked;
        bytes32 password;
        uint oneTimeCode; // This variable will temporary unlock the device once for deposit however they will not have access to details
        bool isCodeValid;
        bool allowAccess;
        int totalParcelsReceived;
    }
    
    struct CourierDevice{
        address CourierAddress;
        string Employer;
        uint systemId;
    }
    
    mapping(address => CourierDevice) public couriers;
    mapping(address => OwnerDevice) public owners;
    
    function GetLength() private view returns(uint){
        bytes memory sb = bytes(owners[msg.sender].DeviceID);
        return sb.length;
    }
    
    // Using abi.encode instead of abi.encodePacked to avoid hash collision 
    function hash(string memory passcode) private pure returns(bytes32){
        return keccak256(abi.encode(passcode));
    }
    
    // Registering the device with an owner & Password Set up
    function RegisterDevice(string memory _DeviceRegistrationID,string memory _password) public {
        if(GetLength() != 0)
            revert("The Owner has already a Device registered");
        require(bytes(_DeviceRegistrationID).length > 0, "Device Registration ID cannot be null");
        owners[msg.sender].ownerAddress = msg.sender;
        owners[msg.sender].DeviceID = _DeviceRegistrationID;
        require(msg.sender == owners[msg.sender].ownerAddress,"You are unauthorized to set a password to this device"); 
        owners[msg.sender].password = hash(_password);
        owners[msg.sender].isDeviceLocked = true;
        owners[msg.sender].allowAccess = true;
    }
    
    //Adding a new courier
    function AddCourier(address _CourierAddress,string memory _employer, uint _systemId) public{
        require(couriers[msg.sender].CourierAddress != _CourierAddress, "This address is already registered as a courier");
        require(bytes(_employer).length > 0,"A registered employeer must be assigned to the employee");
        require(_systemId > 0,"A System Id must be assigned to the employee");
        couriers[msg.sender].CourierAddress = _CourierAddress;
        couriers[msg.sender].Employer = _employer;
        couriers[msg.sender].systemId = _systemId;
    }
    
    //Generating a one time code
    function GetOneTimeCode(address _ownerAddress, uint _onetimeCode) public {
        require(owners[_ownerAddress].allowAccess == true, "The owner is not accepting any packages right now. Please Try again");
        require(couriers[msg.sender].CourierAddress == msg.sender, "Only a courier is allowed to generate a one time code");
        owners[_ownerAddress].oneTimeCode = _onetimeCode;
        owners[_ownerAddress].isCodeValid = true;
    }

    //Allowing Courier to Access Owner's Device to deposit a Parcel
    function CourierAccessDevice(address _ownerAddress, uint _systemId, uint _onetimeCode, int TotalPackages) public {
        require(couriers[msg.sender].CourierAddress == msg.sender,"This person is not registered as a courier");
        require(couriers[msg.sender].systemId == _systemId,"Employee's system id does not match with this address");
        require(owners[_ownerAddress].allowAccess == true, "The owner is not accepting any packages right now. Please Try again");
        require(owners[_ownerAddress].oneTimeCode == _onetimeCode,"Code does not match try another one");
        require(owners[_ownerAddress].ownerAddress != address(0),"Please enter the owner's Address.");
        require(owners[_ownerAddress].isCodeValid == true,"The code you have tried to enter expired. Please get a new one time code");
        owners[_ownerAddress].isDeviceLocked = false;
        owners[_ownerAddress].isCodeValid = false;
        DepositParcel(_ownerAddress,TotalPackages);
    }
    //Delivery Person Deposits Parcel
    function DepositParcel(address _ownerAddress,int TotalPackages) private {
            require(couriers[msg.sender].CourierAddress == msg.sender, "Only a courier is allowed to deposit a parcel");
            require(owners[_ownerAddress].ownerAddress == _ownerAddress, "You can only deposit this parcel into the owner's device");
            owners[_ownerAddress].totalParcelsReceived = TotalPackages;
            owners[_ownerAddress].isDeviceLocked = true;
    }
    
    // change Password
    function UpdatePassword(address _ownerAddress, string memory _password) public{
        require(owners[msg.sender].ownerAddress == _ownerAddress,"Only the owner can change the password");
        owners[_ownerAddress].password = hash(_password);
        owners[_ownerAddress].isDeviceLocked = true;
    }
    // The Owner locks the device allowing no one except himself to open the device
    function SetDeviceAccess(string memory _password, bool _DeviceAccess) public{
        require(owners[msg.sender].ownerAddress == msg.sender,"Only the owner can access this device");
        require(owners[msg.sender].password == hash(_password),"Password is not correct");
        owners[msg.sender].allowAccess = _DeviceAccess;
    }
}