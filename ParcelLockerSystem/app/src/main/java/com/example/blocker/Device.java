package com.example.blocker;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class Device extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5061134c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c80637f0840d11161005b5780637f0840d1146100f8578063953adce61461010b578063a2a522381461011e578063ccd5fcb91461013157600080fd5b8063022914a71461008d578063041857ff146100bd578063460cfe60146100d25780634763ebbe146100e5575b600080fd5b6100a061009b366004610f06565b610153565b6040516100b4989796959493929190610f75565b60405180910390f35b6100d06100cb366004611072565b610233565b005b6100d06100e03660046110c9565b610340565b6100d06100f3366004611117565b6103fa565b6100d0610106366004611171565b6105c0565b6100d06101193660046111aa565b6108d0565b6100d061012c3660046111d4565b6109d0565b61014461013f366004610f06565b610b84565b6040516100b49392919061122b565b6001602081905260009182526040909120805491810180546001600160a01b03909316926101809061125f565b80601f01602080910402602001604051908101604052809291908181526020018280546101ac9061125f565b80156101f95780601f106101ce576101008083540402835291602001916101f9565b820191906000526020600020905b8154815290600101906020018083116101dc57829003601f168201915b5050506002840154600385015460048601546005870154600690970154959660ff9384169692955090935080831692610100909104169088565b336000818152600160205260409020546001600160a01b0316146102ac5760405162461bcd60e51b815260206004820152602560248201527f4f6e6c7920746865206f776e65722063616e2061636365737320746869732064604482015264657669636560d81b60648201526084015b60405180910390fd5b6102b582610c39565b33600090815260016020526040902060030154146103155760405162461bcd60e51b815260206004820152601760248201527f50617373776f7264206973206e6f7420636f727265637400000000000000000060448201526064016102a3565b33600090815260016020526040902060050180549115156101000261ff001990921691909117905550565b336000908152600160205260409020546001600160a01b038381169116146103b95760405162461bcd60e51b815260206004820152602660248201527f4f6e6c7920746865206f776e65722063616e206368616e6765207468652070616044820152651cdcdddbdc9960d21b60648201526084016102a3565b6103c281610c39565b6001600160a01b03909216600090815260016020819052604090912060038101939093556002909201805460ff191690921790915550565b610402610c69565b156104615760405162461bcd60e51b815260206004820152602960248201527f546865204f776e65722068617320616c726561647920612044657669636520726044820152681959da5cdd195c995960ba1b60648201526084016102a3565b60008251116104c05760405162461bcd60e51b815260206004820152602560248201527f44657669636520526567697374726174696f6e2049442063616e6e6f74206265604482015264081b9d5b1b60da1b60648201526084016102a3565b33600081815260016020818152604090922080546001600160a01b031916909317835584516104f59390910191850190610e51565b50336000818152600160205260409020546001600160a01b03161461057a5760405162461bcd60e51b815260206004820152603560248201527f596f752061726520756e617574686f72697a656420746f2073657420612070616044820152747373776f726420746f20746869732064657669636560581b60648201526084016102a3565b61058381610c39565b336000908152600160208190526040909120600381019290925560028201805460ff19169091179055600501805461ff0019166101001790555050565b336000818152602081905260409020546001600160a01b0316146106395760405162461bcd60e51b815260206004820152602a60248201527f5468697320706572736f6e206973206e6f74207265676973746572656420617360448201526910309031b7bab934b2b960b11b60648201526084016102a3565b3360009081526020819052604090206002015483146106b85760405162461bcd60e51b815260206004820152603560248201527f456d706c6f79656527732073797374656d20696420646f6573206e6f74206d6160448201527474636820776974682074686973206164647265737360581b60648201526084016102a3565b6001600160a01b038416600090815260016020819052604090912060050154610100900460ff161515146106fe5760405162461bcd60e51b81526004016102a39061129a565b6001600160a01b03841660009081526001602052604090206004015482146107745760405162461bcd60e51b815260206004820152602360248201527f436f646520646f6573206e6f74206d617463682074727920616e6f74686572206044820152626f6e6560e81b60648201526084016102a3565b6001600160a01b03848116600090815260016020526040902054166107e55760405162461bcd60e51b815260206004820152602160248201527f506c6561736520656e74657220746865206f776e6572277320416464726573736044820152601760f91b60648201526084016102a3565b6001600160a01b03841660009081526001602081905260409091206005015460ff1615151461088d5760405162461bcd60e51b815260206004820152604860248201527f54686520636f646520796f75206861766520747269656420746f20656e74657260448201527f20657870697265642e20506c65617365206765742061206e6577206f6e652074606482015267696d6520636f646560c01b608482015260a4016102a3565b6001600160a01b038416600090815260016020526040902060028101805460ff199081169091556005909101805490911690556108ca8482610d0f565b50505050565b6001600160a01b038216600090815260016020819052604090912060050154610100900460ff161515146109165760405162461bcd60e51b81526004016102a39061129a565b336000818152602081905260409020546001600160a01b03161461099a5760405162461bcd60e51b815260206004820152603560248201527f4f6e6c79206120636f757269657220697320616c6c6f77656420746f2067656e60448201527465726174652061206f6e652074696d6520636f646560581b60648201526084016102a3565b6001600160a01b03909116600090815260016020819052604090912060048101929092556005909101805460ff19169091179055565b336000908152602081905260409020546001600160a01b0384811691161415610a535760405162461bcd60e51b815260206004820152602f60248201527f54686973206164647265737320697320616c726561647920726567697374657260448201526e32b21030b990309031b7bab934b2b960891b60648201526084016102a3565b6000825111610aca5760405162461bcd60e51b815260206004820152603760248201527f41207265676973746572656420656d706c6f79656572206d757374206265206160448201527f737369676e656420746f2074686520656d706c6f79656500000000000000000060648201526084016102a3565b60008111610b2f5760405162461bcd60e51b815260206004820152602c60248201527f412053797374656d204964206d7573742062652061737369676e656420746f2060448201526b74686520656d706c6f79656560a01b60648201526084016102a3565b3360009081526020818152604090912080546001600160a01b0319166001600160a01b0386161781558351610b6c92600190920191850190610e51565b50336000908152602081905260409020600201555050565b600060208190529081526040902080546001820180546001600160a01b039092169291610bb09061125f565b80601f0160208091040260200160405190810160405280929190818152602001828054610bdc9061125f565b8015610c295780601f10610bfe57610100808354040283529160200191610c29565b820191906000526020600020905b815481529060010190602001808311610c0c57829003601f168201915b5050505050908060020154905083565b600081604051602001610c4c9190611303565b604051602081830303815290604052805190602001209050919050565b3360009081526001602081905260408220018054829190610c899061125f565b80601f0160208091040260200160405190810160405280929190818152602001828054610cb59061125f565b8015610d025780601f10610cd757610100808354040283529160200191610d02565b820191906000526020600020905b815481529060010190602001808311610ce557829003601f168201915b5050925195945050505050565b336000818152602081905260409020546001600160a01b031614610d8b5760405162461bcd60e51b815260206004820152602d60248201527f4f6e6c79206120636f757269657220697320616c6c6f77656420746f2064657060448201526c1bdcda5d0818481c185c98d95b609a1b60648201526084016102a3565b6001600160a01b0380831660008181526001602052604090205490911614610e1b5760405162461bcd60e51b815260206004820152603860248201527f596f752063616e206f6e6c79206465706f73697420746869732070617263656c60448201527f20696e746f20746865206f776e6572277320646576696365000000000000000060648201526084016102a3565b6001600160a01b03909116600090815260016020819052604090912060068101929092556002909101805460ff19169091179055565b828054610e5d9061125f565b90600052602060002090601f016020900481019282610e7f5760008555610ec5565b82601f10610e9857805160ff1916838001178555610ec5565b82800160010185558215610ec5579182015b82811115610ec5578251825591602001919060010190610eaa565b50610ed1929150610ed5565b5090565b5b80821115610ed15760008155600101610ed6565b80356001600160a01b0381168114610f0157600080fd5b919050565b600060208284031215610f1857600080fd5b610f2182610eea565b9392505050565b6000815180845260005b81811015610f4e57602081850181015186830182015201610f32565b81811115610f60576000602083870101525b50601f01601f19169290920160200192915050565b6001600160a01b038916815261010060208201819052600090610f9a8382018b610f28565b981515604084015250506060810195909552608085019390935290151560a0840152151560c083015260e09091015292915050565b634e487b7160e01b600052604160045260246000fd5b600082601f830112610ff657600080fd5b813567ffffffffffffffff8082111561101157611011610fcf565b604051601f8301601f19908116603f0116810190828211818310171561103957611039610fcf565b8160405283815286602085880101111561105257600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806040838503121561108557600080fd5b823567ffffffffffffffff81111561109c57600080fd5b6110a885828601610fe5565b925050602083013580151581146110be57600080fd5b809150509250929050565b600080604083850312156110dc57600080fd5b6110e583610eea565b9150602083013567ffffffffffffffff81111561110157600080fd5b61110d85828601610fe5565b9150509250929050565b6000806040838503121561112a57600080fd5b823567ffffffffffffffff8082111561114257600080fd5b61114e86838701610fe5565b9350602085013591508082111561116457600080fd5b5061110d85828601610fe5565b6000806000806080858703121561118757600080fd5b61119085610eea565b966020860135965060408601359560600135945092505050565b600080604083850312156111bd57600080fd5b6111c683610eea565b946020939093013593505050565b6000806000606084860312156111e957600080fd5b6111f284610eea565b9250602084013567ffffffffffffffff81111561120e57600080fd5b61121a86828701610fe5565b925050604084013590509250925092565b6001600160a01b038416815260606020820181905260009061124f90830185610f28565b9050826040830152949350505050565b600181811c9082168061127357607f821691505b6020821081141561129457634e487b7160e01b600052602260045260246000fd5b50919050565b60208082526043908201527f546865206f776e6572206973206e6f7420616363657074696e6720616e79207060408201527f61636b61676573207269676874206e6f772e20506c656173652054727920616760608201526230b4b760e91b608082015260a00190565b602081526000610f216020830184610f2856fea264697066735822122033ac89bbb7afd92acbdd4705b91c8b49d6ea16535385277f7956acbf3bc6604264736f6c634300080a0033";

    public static final String FUNC_ADDCOURIER = "AddCourier";

    public static final String FUNC_COURIERACCESSDEVICE = "CourierAccessDevice";

    public static final String FUNC_GETONETIMECODE = "GetOneTimeCode";

    public static final String FUNC_REGISTERDEVICE = "RegisterDevice";

    public static final String FUNC_SETDEVICEACCESS = "SetDeviceAccess";

    public static final String FUNC_UPDATEPASSWORD = "UpdatePassword";

    public static final String FUNC_COURIERS = "couriers";

    public static final String FUNC_OWNERS = "owners";

    @Deprecated
    protected Device(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Device(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Device(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Device(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> AddCourier(String _CourierAddress, String _employer, BigInteger _systemId) {
        final Function function = new Function(
                FUNC_ADDCOURIER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _CourierAddress), 
                new org.web3j.abi.datatypes.Utf8String(_employer), 
                new org.web3j.abi.datatypes.generated.Uint256(_systemId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> CourierAccessDevice(String _ownerAddress, BigInteger _systemId, BigInteger _onetimeCode, BigInteger TotalPackages) {
        final Function function = new Function(
                FUNC_COURIERACCESSDEVICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _ownerAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_systemId), 
                new org.web3j.abi.datatypes.generated.Uint256(_onetimeCode), 
                new org.web3j.abi.datatypes.generated.Int256(TotalPackages)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> GetOneTimeCode(String _ownerAddress, BigInteger _onetimeCode) {
        final Function function = new Function(
                FUNC_GETONETIMECODE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _ownerAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(_onetimeCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> RegisterDevice(String _DeviceRegistrationID, String _password) {
        final Function function = new Function(
                FUNC_REGISTERDEVICE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_DeviceRegistrationID), 
                new org.web3j.abi.datatypes.Utf8String(_password)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> SetDeviceAccess(String _password, Boolean _DeviceAccess) {
        final Function function = new Function(
                FUNC_SETDEVICEACCESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_password), 
                new org.web3j.abi.datatypes.Bool(_DeviceAccess)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> UpdatePassword(String _ownerAddress, String _password) {
        final Function function = new Function(
                FUNC_UPDATEPASSWORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _ownerAddress), 
                new org.web3j.abi.datatypes.Utf8String(_password)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple3<String, String, BigInteger>> couriers(String param0) {
        final Function function = new Function(FUNC_COURIERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<String, String, BigInteger>>(function,
                new Callable<Tuple3<String, String, BigInteger>>() {
                    @Override
                    public Tuple3<String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger>> owners(String param0) {
        final Function function = new Function(FUNC_OWNERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Bool>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Int256>() {}));
        return new RemoteFunctionCall<Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger>>(function,
                new Callable<Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger>>() {
                    @Override
                    public Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (Boolean) results.get(2).getValue(), 
                                (byte[]) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (Boolean) results.get(5).getValue(), 
                                (Boolean) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    @Deprecated
    public static Device load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Device(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Device load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Device(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Device load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Device(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Device load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Device(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Device> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Device.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Device> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Device.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Device> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Device.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Device> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Device.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
