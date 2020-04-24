var page = 1;
var count;
const SELECT_ARGS = 106;
const SELECT_RESULT = 107;
const MESSAGE = 100;
var xmlhttp;
var uri = "../../order";


function bodyOnLoad() {
    var url = uri + "?pageNo=1";
    request(url, null, showData, "get")
}

function prevPage() {
    --page;
    query();
}

function nextPage() {
    ++page;
    query();
}


function clearMachineId() {
    document.getElementById("machineIdField").value = null;
}

function clearTime() {
    document.getElementById("fromTimeField").value = null;
    document.getElementById("toTimeField").value = null
}

function clearAll() {
    clearMachineId();
    clearTime()
}

function query() {
    var mid = document.getElementById("machineIdField").value;
    var ft = document.getElementById("fromTimeField").value;
    var tt = document.getElementById("toTimeField").value;
    var url = uri + "?pageNo=" + page;
    if ("" != mid) {
        url += "&machineId=" + mid;
    }
    if ("" != ft) {
        url += "&fromTime=" + ft;
    }
    if ("" != tt) {
        url += "&toTime=" + tt;
    }

    request(url, null, showData, "get")
}


function pageUpdate() {
    var prevPageButton = document.getElementById("prevPageButton");
    var nextPageButton = document.getElementById("nextPageButton");
    prevPageButton.hidden = (page === 1);
    nextPageButton.hidden = (page * 10 >= count);
    document.getElementById("page").innerText = page;
}

function setPageOne() {
    page = 1;
    document.getElementById("pageNo").value = 1;
    document.getElementById("nextPageButton").hidden = true;
    document.getElementById("prevPageButton").hidden = true;

}

function showData() {
    if (xmlhttp.status === 200) {
        document.getElementById("noneLabel").hidden = true;
        var response = JSON.parse(xmlhttp.responseText);

        var div = document.getElementById("orders");
        var i = 0;
        for (; i < response.data.length; i++) {
            if (div.rows[i + 1] == null) {
                div.insertRow(i + 1)
                div.rows[i + 1].insertCell(0);
                div.rows[i + 1].insertCell(1);
                div.rows[i + 1].insertCell(2);
            }
            div.rows[i + 1].cells[0].innerText = response.data[i].id;
            div.rows[i + 1].cells[1].innerText = response.data[i].machineId;
            div.rows[i + 1].cells[2].innerText = formatDate(response.data[i].time);
        }
        if (i != 10 && div.rows.length > i + 1) {
            var end = i;
            for (i = div.rows.length - 1; i > end; i--) {
                div.deleteRow(i);
            }
        }
        if (response.totalCount != undefined) {
            count = response.totalCount;
        }

        pageUpdate();

    } else if (xmlhttp.status == 404) {
        alert("找不到记录");
        var div = document.getElementById("orders");
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

