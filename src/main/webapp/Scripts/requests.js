/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
            $("div." + picid).hide();
        }
        alert(data.message);
    },
    error: function (xhr, textStatus, errorThrown) {
                     alert("There was an error processing your request");
                     console.log('Error in Operation');
                 }
    });
}

function updateProfile(owner)
{
    var fields = $("updateForm").serializeArray();
    var data = {'firstname' : owner};
    alert(data);
    $.ajax({
        type: "post",
        url: "/Instagrim/Profile/" + owner,
        data: data,
        dataType: "json",
        async: false
    });
}

function hideEditTags()
{
    $("div.hide").hide();
    //$("button.hide").hide();
}

function showEditTags(picID)
{
    //alert("show");
    $("div#tag" + picID).show();
    //$("button#" + picID).hide();
}

function updateTags(picID, owner)
{
    var id = "input." + picID;
    var tags = $(id).val();
    $.ajax({
       type: "put",
       url: "/Instagrim/Image/" + owner + "/" + picID + "/" + tags,
       async: false,
    success: function (data, textStatus, xhr) {
        if (data.success)
        {   
            $("h3." + picID).text("Tags: " + tags);
            $("div#tag" + picID).hide();
        }
        alert(data.message);
    },
    error: function (xhr, textStatus, errorThrown) {
                     alert("There was an error processing your request");
                     console.log('Error in Operation');
                 }
    });
}

$( document ).ready( hideEditTags);


