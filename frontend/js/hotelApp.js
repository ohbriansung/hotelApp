function setType(type) {
	document.getElementById("mySmallModalLabel").innerHTML = type;
	document.getElementById("modalButton").value = type;
	document.getElementById("username").value = "";
	document.getElementById("password").value = "";
	document.getElementById("type").value = type;
	document.getElementById("passwordSnippet").style.display = "none";
	document.getElementById("usernameBad").style.display = "none";
	document.getElementById("usernameGood").style.display = "none";
	document.getElementById("passwordBad").style.display = "none";
	document.getElementById("passwordGood").style.display = "none";
}

function passwordCheck() {
	var pw = document.getElementById("password").value;
	if (pw != "") {
		document.getElementById("passwordSnippet").style.display = "block";
	}
	else {
		document.getElementById("passwordSnippet").style.display = "none";
	}

	if (pw == "") {
		document.getElementById("passwordGood").style.display = "none";
		document.getElementById("passwordBad").style.display = "none";
	}
	else if (pw.length < 8 || !characterCheck("password", pw)) {
		document.getElementById("passwordGood").style.display = "none";
		document.getElementById("passwordBad").style.display = "block";
	}
	else if (pw.length >= 8) {
		document.getElementById("passwordGood").style.display = "block";
		document.getElementById("passwordBad").style.display = "none";
	}
}

function usernameCheck() {
	var un = document.getElementById("username").value;

	if (un == "") {
		document.getElementById("usernameGood").style.display = "none";
		document.getElementById("usernameBad").style.display = "none";
	}
	else if (!characterCheck("username", un)) {
		document.getElementById("usernameGood").style.display = "none";
		document.getElementById("usernameBad").style.display = "block";
	}
	else {
		document.getElementById("usernameGood").style.display = "block";
		document.getElementById("usernameBad").style.display = "none";
	}
}

function characterCheck(type, text) {
	var result = true;

	if (type == "password") {
		var pLower = /[a-z]/g; 
    	if (!text.match(pLower)) {
    		result = false;
    	}

    	var pUpper = /[A-Z]/g; 
    	if (!text.match(pUpper)) {
    		result = false;
    	}

    	var pDigit = /[\d]/g; 
    	if (!text.match(pDigit)) {
    		result = false;
    	}

    	var pSpecial = /[!@#$%&*\-_]/g; 
    	if (!text.match(pSpecial)) {
    		result = false;
    	}

    	var pNo = /[^\w!@#$%&*\-]/g; 
    	if (text.match(pNo)) {
    		result = false;
    	}
	}
	else if (type = "username") {
		var pNo = /[^\w@\-]/g; 
    	if (text.match(pNo)) {
    		result = false;
    	}
	}

	return result;
}

function snippet(type) {
	document.getElementById("password").type = type;
}

function tooltipFouce(type) {
	document.getElementById(type).tooltip({
		placement: "top", trigger: "focus"
	});
}

function errorAlert(message) {
	swal("Something is wrong.", message, "error");
}

function successAlert(message) {
	swal("Success!", message, "success");
}

function greeting(name) {
	swal("Welcome back, " + name + "!");
}

function hideAndSee(page) {
	document.getElementById("navlogin1").style.display = "none";
	document.getElementById("navlogin2").style.display = "none";
	document.getElementById("navlogout1").style.display = "list-item";
	document.getElementById("navlogout2").style.display = "list-item";
	document.getElementById("navlogout3").style.display = "list-item";
	if (page == "index") {
		document.getElementById("login").style.display = "none";
		document.getElementById("start").style.display = "block";
	}
}

function tableTitle(title) {
	loading("start");
	document.getElementById("tableName").innerHTML = "<i class=\"fa fa-table\"></i> " + title;
	category(title);

	if (title == "Hotels") {
		var style = document.getElementById("hotelSearchBar").getAttribute("style");
		document.getElementById("hotelSearchBar").setAttribute("style", style.replace("display: none;", ""));
		document.getElementById("reviewSearchBar").setAttribute("style", "display: none;");
		document.getElementById("attractionSearchBar").setAttribute("style", "display: none;");
		document.getElementById("googleMap").setAttribute("style", "display: block;");
		if (document.getElementById("searchCity").innerHTML == "") {
			getCitys();
		}
		showHotels();
	}
	else if (title == "Reviews") {
		var style = document.getElementById("reviewSearchBar").getAttribute("style");
		document.getElementById("reviewSearchBar").setAttribute("style", style.replace("display: none;", ""));
		document.getElementById("hotelSearchBar").setAttribute("style", "display: none;");
		document.getElementById("attractionSearchBar").setAttribute("style", "display: none;");
		document.getElementById("googleMap").setAttribute("style", "display: none;");
		showReviews();
	}
	else if (title == "Attractions") {
		var style = document.getElementById("attractionSearchBar").getAttribute("style");
		document.getElementById("attractionSearchBar").setAttribute("style", style.replace("display: none;", ""));
		document.getElementById("hotelSearchBar").setAttribute("style", "display: none;");
		document.getElementById("reviewSearchBar").setAttribute("style", "display: none;");
		document.getElementById("googleMap").setAttribute("style", "display: none;");
		showAttractions();
	}
	loading("end");
}

function category(type) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("category").innerHTML = this.responseText;
		}
	};
	xhttp.open("POST", "category?type=" + type, true);
	xhttp.send();
}

function getCitys() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("searchCity").innerHTML = this.responseText;
		}
	};
	xhttp.open("POST", "city", false);
	xhttp.send();
}

function getPages(type) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("hotelPageBar").innerHTML = this.responseText;
		}
	};
	xhttp.open("POST", "page?pageof=" + type, true);
	xhttp.send();
}

function showHotels() {
	var hotelShowEntries = document.getElementById("hotelShowEntries").value;
	var city = document.getElementById("city").value;
	var hotelname = document.getElementById("hotelname").value;
	var sortColumn = document.getElementById("hotelSortColumn").value;
	var sortType = document.getElementById("hotelSortType").value;
	var hotelPage = document.getElementById("hotelPage").value;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		document.getElementById("dataTable").innerHTML = this.responseText;
    	}
    };
    xhttp.open("POST", "hotel?hotelShowEntries=" + hotelShowEntries + "&city=" + city + "&hotelname=" + hotelname + "&sortColumn=" + sortColumn + "&sortType=" + sortType + "&hotelPage=" + hotelPage, false);
    xhttp.send();
    getPages('hotel');
    googleMapDisplay();
}

function sort(type, column, sorttype) {
	document.getElementById(type + "SortColumn").value = column;
	document.getElementById(type + "SortType").value = sorttype;
	document.getElementById(type + "Page").value = 1;
	show(type);
}

function showOnChange(type) {
	document.getElementById(type + "Page").value = 1;
	show(type);
}

function gotoPage(type, page) {
	document.getElementById(type + "Page").value = page;
	show(type);
}

function googleMapDisplay() {
	var path = "//maps.googleapis.com/maps/api/staticmap?center=";
	var center = "San+Francisco";
	var query = "&zoom=13&size=420x600&maptype=roadmap";
	var select = document.getElementById("city").value;
	var key = "&key=AIzaSyCRdm7u2jxWgUKSEwJgPbQZLS0S6t-AIM0"

	if (select != "") {
		center = select.replace(" ", "+");
	}

	var markers = "";
	var hotels = document.getElementsByName("hotels");

	if (hotels.length > 0) {
		for (var i = 0; i < hotels.length; i++) {
			markers += "&markers=label:" + hotels[i].getAttribute("no");
			markers += "%7C" + hotels[i].getAttribute("lat") + "," + hotels[i].getAttribute("lon");
		}

		document.getElementById("googleMap").setAttribute("src", path + center + query + markers + key);
	}
}

function thisGoogleMap() {
	var path = "//maps.googleapis.com/maps/api/staticmap?center=";
	var lat = document.getElementById("gmdata").getAttribute("lat");
	var lon = document.getElementById("gmdata").getAttribute("lon");
	var center = lat + "," + lon;
	var query = "&zoom=14&size=640x420&maptype=roadmap";
	var markers = "&markers=label:H%7C" + lat + "," + lon;
	var key = "&key=AIzaSyCRdm7u2jxWgUKSEwJgPbQZLS0S6t-AIM0"

	document.getElementById("googleMap2").setAttribute("src", path + center + query + markers + key);
}

function show(type) {
	if (type == "hotel") {
		showHotels();
	}
	else if (type == "review") {
		showReviews();
	}
	else if (type == "attraction") {
		showAttractions();
	}
}

function showReviews() {
	var hotelId = document.getElementById("hotelId").value;
	var sortColumn = document.getElementById("reviewSortColumn").value;
	var sortType = document.getElementById("reviewSortType").value;
	var reviewShowEntries = document.getElementById("reviewShowEntries").value;
	var reviewPage = document.getElementById("reviewPage").value;
	var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		document.getElementById("dataTable").innerHTML = this.responseText;
    	}
    };
    xhttp.open("POST", "review?hotelId=" + hotelId + "&sortColumn=" + sortColumn + "&sortType=" + sortType + "&reviewShowEntries=" + reviewShowEntries + "&reviewPage=" + reviewPage, false);
    xhttp.send();
    getPages('review');
    tableTitleAppend('of');
}

function detail(type, id) {
	var token = document.getElementById("pageToken").value;
	var background = true;

	if (token == "mypage") {
		background = false;
	}

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("detailDisplay").innerHTML = this.responseText;
		}
	};
	xhttp.open("POST", "hotelDetail?id=" + id + "&type=" + type, background);
	xhttp.send();

	document.getElementById(type + "Id").value = id;

	if (type == "hotel") {
		if (token == "hotel") {
			document.getElementById("thisHotel").value = document.getElementById("h" + id).innerText;
			document.getElementById("addReview").setAttribute("style", "");
			document.getElementById("reviewSortColumn").value = "column4";
			document.getElementById("reviewSortType").value = "DESC";
			document.getElementById("reviewPage").value = "1";
			document.getElementById("attractionPage").value = "1";
		}
		else if (token == "mypage") {
			document.getElementById("addReview2").setAttribute("style", "");
			document.getElementById("btnReviews").setAttribute("style", "display: none;");
			document.getElementById("btnAttractions").setAttribute("style", "display: none;");
		}
	}
}

function scrollDownAnimation() {
	var scroll = function() {
		var speed = 1;
		var i = document.documentElement.scrollTop;
		if (i <= 0) i = 1;
		var x = setInterval(function() {
			var position = i + speed;
			window.scrollTo(0, position);
			if (speed <= 200) speed *= 1.4;
			else speed += 125;
			if (position >= document.body.scrollHeight) clearInterval(x);
		}, 20);
	};

	setTimeout(scroll, 500);
}

function like(type, id) {
	var result;
	var token = document.getElementById("pageToken").value;
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			result = this.responseText;
		}
	};
	xhttp.open("POST", "like?type=" + type + "&id=" + id, false);
	xhttp.send();

	if (result == 1) {
		if (token == "mypage") {
			if (type == "hotel") {
				reloadCard("favoriteHotels");
				document.getElementById("detailDisplay").innerHTML = "";
				document.getElementById("hotelId").value = "";
				id = "";
			}
			else if (type == "review") {
				reloadCard("likedReviews");

				if (document.getElementById("btnEdit") == null) {
					document.getElementById("detailDisplay").innerHTML = "";
					document.getElementById("reviewId").value = "";
					id = "";
				}
			}
			else if (type == "expedia") {
				reloadCard("expediaLinks");
			}
		}

		if (type == "expedia") {
			type = "hotel";
		}
		detail(type, id);
	}
	else {
		errorAlert("Cannot proceed this operation.");
	}
}

function reviewmodal(type) {
	document.getElementById("reviewModalLabel").innerText = type + " Review";
	var id;

	if (type == "Add") {
		id = document.getElementById("hotelId").value;
	}
	else if (type == "Edit") {
		id = document.getElementById("reviewId").value;
	}

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("reviewModalContent").innerHTML = this.responseText;
		}
	};
	xhttp.open("GET", "reviewmodal?type=" + type + "&id=" + id, false);
	xhttp.send();

	if (type == "Edit") {
		contentChange("Title");
		contentChange("Content");
	}
}

function contentChange(type) {
	var count;
	var current = document.getElementById("review" + type).value.length;

	if (type == "Title") {
		count = 50;
	}
	else if (type == "Content") {
		count = 250;
	}

	if (current > count) {
		errorAlert("Exceeding maximum length.");
		document.getElementById("review" + type).value = "";
		document.getElementById("count" + type).innerText = count;
		return;
	}

	document.getElementById("count" + type).innerText = count - current;
}

function deleteConfirm() {
	swal({
		title: "Are you sure?",
		text: "Your data will be cleared.",
		type: "warning",
		showCancelButton: true,
		confirmButtonText: "Yes, please!",
		closeOnConfirm: false
	},
	function(){
		putreview('delete');
	});
}

function putreview(type) {
	var id;
	var title = document.getElementById("reviewTitle").value;
	var rating = document.getElementById("reviewRating").value;
	var recommend = 1;
	var review = document.getElementById("reviewContent").value;
	var result = 0;

	if (type == "add") {
		id = document.getElementById("hotelId").value;
	}
	else if (type == "edit" || type == "delete") {
		id = document.getElementById("reviewId").value;
	}

	if (document.getElementById("recommend0").checked) {
        recommend = 0;
    }

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			result = this.responseText;
		}
	};
	xhttp.open("POST", "reviewmodal?type=" + type + "&id=" + id + "&title=" + title + "&rating=" + rating + "&recommend=" + recommend + "&review=" + review, false);
	xhttp.send();

	if (result == 1) {
		successAlert("Changed successfully!");
		var token = document.getElementById("pageToken").value;

		if (token == "hotel") {
			document.getElementById("reviewSortColumn").value = "column4";
			document.getElementById("reviewSortType").value = "DESC";
			document.getElementById("reviewPage").value = "1";
			tableTitle('Reviews');
			window.scrollTo(0, 0);
		}
		else if (token == "mypage") {
			reloadCard('myReviews');
			reloadCard('likedReviews');
		}

		if (type == "edit") {
			detail("review", id);
		}
		else if (type == "add") {
			detail("hotel", document.getElementById("hotelId").value);
		}
		else if (type == "delete") {
			document.getElementById("detailDisplay").innerHTML = "";
		}
	}
	else {
		errorAlert("Changed unsuccessfully.");
	}
}

function showlike(type) {
	var id;

	if (type == "review") {
		id = document.getElementById("reviewId").value;
	}

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("likeModalBody").innerHTML = this.responseText;
		}
	};
	xhttp.open("POST", "showlike?type=" + type + "&id=" + id, true);
	xhttp.send();
}

function tableTitleAppend(str) {
	var hotelName = document.getElementById("thisHotel").value;
    var title = document.getElementById("tableName").innerText;
    if (hotelName != "" && !title.includes(hotelName)) {
	    document.getElementById("tableName").innerHTML += " " + str + " " + hotelName;
	}
}

function showAttractions() {
	var hotelId = document.getElementById("hotelId").value;
	var attractionShowEntries = document.getElementById("attractionShowEntries").value;
	var attractionPage = document.getElementById("attractionPage").value;
	var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		document.getElementById("dataTable").innerHTML = this.responseText;
    	}
    };
    xhttp.open("GET", "attraction?hotelId=" + hotelId + "&attractionShowEntries=" + attractionShowEntries + "&attractionPage=" + attractionPage, false);
    xhttp.send();
    getPages('attraction');
    tableTitleAppend('near');
}

function loading(status) {
	if (status == "start") {
		document.getElementById("loadingCircle").setAttribute("style", "display:block;position:fixed;top:45%;left:25%;width:50%;height:auto;text-align:center;color:#3385FF;");
	}
	else if (status == "end") {
		document.getElementById("loadingCircle").setAttribute("style", "display: none;");
	}
}

function addResult(type) {
	var data = document.getElementsByName(type + "Data").length;
	var finish = document.getElementsByName(type + "Finish").length;

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		document.getElementById(type).innerHTML += this.responseText;
    		hideShowButton(type);
    	}
    };
    xhttp.open("POST", "mypage?type=" + type + "&data=" + data + "&finish=" + finish, true);
    xhttp.send();
}

function hideShowButton(type) {
	if (document.getElementsByName(type + "Finish").length > 0) {
		document.getElementById(type + "Show").setAttribute("style", "display: none;");
	}
}

function getLoginInformation() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		document.getElementById("loginInformation").innerHTML = this.responseText;
    	}
    };
    xhttp.open("GET", "page", true);
    xhttp.send();
}

function clearAll(type) {
	swal({
		title: "Are you sure?",
		text: "Your history will be cleared.",
		type: "warning",
		showCancelButton: true,
		confirmButtonText: "Yes, please!",
		closeOnConfirm: false
	},
	function(){
		clearOnConfirm(type);
	});
}

function clearOnConfirm(type) {
	var result = -1;
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
    	if (this.readyState == 4 && this.status == 200) {
    		result = this.responseText;
    	}
    };
    xhttp.open("POST", "clear?type=" + type, false);
    xhttp.send();

    if (result > 0) {
    	successAlert("Your history have been cleared.");
	    reloadCard(type);

	    if (type == "myReviews") {
	    	reloadCard('likedReviews');
	    }
	    else if (type == "expediaLinks" && document.getElementById("btnExpedia") != null) {
	    	detail('hotel', document.getElementById("hotelId").value);
	    }
	}
	else if (result == 0) {
		errorAlert("There is nothing to be cleared.");
	}
	else {
		errorAlert("Changed unsuccessfully.");
	}
}

function reloadCard(type) {
	document.getElementById(type).innerHTML = "";
	addResult(type);
}

function test() {
	swal("test success.");
}