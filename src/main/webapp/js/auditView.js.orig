$(document).ready(function() {
    var base_url = window.location.origin;
    var hostname = window.location.hostname;
    var port = ':8080';
    var scheme = 'http://';
    $.getJSON(scheme + hostname + port + '/api/audit/search/findAllByTimestamp', function (data) {
        console.log(data);
        var audit = data._embedded.audit;
        var txt = "";
        console.log(audit);
        for (x in audit) {
            txt += "<tr><td>"+audit[x].type+"</td><td>"+audit[x].name+"</td><td>"+(new Date(audit[x].timestamp)).toUTCString()+"</td><td>"+audit[x].action+"</td></tr>";
        }

        if(txt != ""){
            $("table tbody").append(txt);
            $('#auditTable').DataTable();
        }
    });
});


//Still working on this method
function getDate(timestamp) {
// Create a new JavaScript Date object based on the timestamp
// multiplied by 1000 so that the argument is in milliseconds, not seconds.
    var date = new Date(timestamp * 1000);
// Hours part from the timestamp
    var hours = date.getHours();
// Minutes part from the timestamp
    var minutes = "0" + date.getMinutes();
// Seconds part from the timestamp
    var seconds = "0" + date.getSeconds();

// Will display time in 10:30:23 format
    var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);

    return date +" "+ formattedTime;
}