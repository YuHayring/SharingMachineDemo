var page = 1;
var count;
var xmlhttp;
var uri = "../../log";

function bodyOnLoad() {
    request(uri + "?pageNo=" + page, null, showData, "get")
}

function prevPage() {
    request(uri + "?pageNo=" + --page, null, showData, "get")
}

function nextPage() {
    request(uri + "?pageNo=" + ++page, null, showData, "get")
}

function showData() {
    if (xmlhttp.status === 200) {
        document.getElementById("noneLabel").hidden = true;
        var response = JSON.parse(xmlhttp.responseText);


        var div = document.getElementById("logs");
        for (var i = 0; i < response.data.length; i++) {
            if (div.rows[i + 1] !== null) {
                div.insertRow(i + 1)

            }
            div.rows[i + 1].innerText = formatDate(response.data[i].time);
        }
        if (response.totalCount !== undefined) {
            count = response.totalCount;
        }

        pageUpdate();


    } else if (xmlhttp.status == 404) {
        alert("找不到记录");
        var div = document.getElementById("logs");
        document.getElementById("noneLabel").hidden = false;
        for (var i = div.rows.length - 1; i > 0; i--) {
            div.deleteRow(i)
        }
        pageUpdate();
    } else if (xmlhttp.status == 401) {
        window.location = "/sharing/login.html?msg=%e6%9c%aa%e7%99%bb%e5%bd%95"
    } else if (xmlhttp == 500) {
        alert("服务器错误")
    }
}

function pageUpdate() {
    var prevPageButton = document.getElementById("prevPageButton");
    var nextPageButton = document.getElementById("nextPageButton");
    prevPageButton.hidden = (page === 1);
    nextPageButton.hidden = (page * 10 >= count);
    document.getElementById("page").innerText = page;
}

