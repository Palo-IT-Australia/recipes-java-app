// Clinic Finder Wiring
var _autocomplete = [];
function initAutoComplete() {
    $(".suburb-search").each(function () {
        var autocomplete = new google.maps.places.Autocomplete(this, {types:['(regions)'], componentRestrictions: {country: 'au'}});
        _autocomplete.push(autocomplete);
    });        
}

function goFinder(elm, targeturl) {
    var pdiv = $($(elm).closest(".w-form"));
    var adr = $(pdiv.find("input[type='text']")[0]).val();
    var mod = $(pdiv.find("select")[0]).val();
    var box = $(pdiv.find("input[type='checkbox']")[0]);
    var url = targeturl + "?";  
    if(box && box.prop('checked')) {
        var goto = url + "lat=" + box.attr("data-lat") + "&lon=" + box.attr("data-lon")
        if(mod.length > 0){
            goto = goto + "&mod=" + mod;
        }
        window.location.href = goto;
    }else if(adr.length > 0) {
        var url = url + "adr=" + adr;
        if(mod.length > 0){
            url = url + "&mod=" + mod;
        }
        window.location.href = url;
    }
}

function detectCurrentLocation(elm) {
	if ($(elm).prop("checked") && navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
        if(!position) {
            alert("Please allow this browser to detect your location.");
            return;
        }
        console.log("geo pos", position);
		$(elm).attr("data-lat","" + position.coords.latitude);
		$(elm).attr("data-lon","" + position.coords.longitude);
	  }, function() {
		alert("This browser cannot detect current location.");
	  });
	}
}

//$(document).ready(function(){
//    if (typeof google == 'undefined') $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyDzefQOQ-xmqMjvwQSNj1M8DfXo8hlcjYk&sensor=false&libraries=places&callback=initAutoComplete");
//    else initAutoComplete();
//});