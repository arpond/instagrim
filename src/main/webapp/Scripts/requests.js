/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function foo()
{
    alert("blah");
}

//function confirmDelete(picid, owner)
//{   
//    $("#confirmDelete").dialog(
//    {
//       modal: true,
//       resizable: false,
//       height: 200,
//       buttons:{ 
//           "Delete Image": function() {
//                deleteImage(picid, owner);
//                $(this).dialog("close");
//            },
//            Cancel: function() {
//                $(this).dialog("close");
//            }
//        }
//    });
//}

function deleteImage(picid, owner)
{
    $.ajax({
    type: "delete",
    url: "/Instagrim/Image/" + owner  +"/" + picid,
    dataType: "json",
    async: false,
    success: function (data, textStatus, xhr) {
        if (data.success)
        {   
            //$("#successfullDelete").dialog();
            $("#" + picid).hide();
        }
        else
        {
            //$("#errorDelete").dialog();
        }
        alert(data.message);
    },
    error: function (xhr, textStatus, errorThrown) {
                     alert("There was an error processing your request");
                     console.log('Error in Operation');
                 }
    });
}



