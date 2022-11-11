$('#GenBTN').on("click",function(){
    var value = Math.floor(Math.random() * 100000); 
    $("#input_system_Id").val(value);
});