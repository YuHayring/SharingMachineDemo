var page = 1;
var count;
var xmlhttp;
var uri = "../../order";
var data;


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
    document.getElementById("machineIdField").value = "";
}

function clearTime() {
    document.getElementById("fromTimeField").value = "";
    document.getElementById("toTimeField").value = "";
}

function clearUserId() {
    document.getElementById("userIdField").value = "";
}

function clearAll() {
    clearMachineId();
    clearTime();
    clearUserId();
}

function query() {
    var mid = document.getElementById("machineIdField").value;
    var ft = document.getElementById("fromTimeField").value;
    var tt = document.getElementById("toTimeField").value;
    var uid = document.getElementById("userIdField").value;
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
    if ("" != uid) {
        url += "&userId=" + uid;
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


function showData() {
    if (xmlhttp.status === 200) {
        document.getElementById("noneLabel").hidden = true;
        var response = JSON.parse(xmlhttp.responseText);
        data = response.data;
        var div = document.getElementById("orders");
        var i = 0;
        for (; i < response.data.length; i++) {
            if (div.rows[i + 1] == null) {
                div.insertRow(i + 1)
                div.rows[i + 1].insertCell(0);
                div.rows[i + 1].insertCell(1);
                div.rows[i + 1].insertCell(2);
                div.rows[i + 1].insertCell(3);
                div.rows[i + 1].insertCell(4);

            }
            div.rows[i + 1].cells[0].innerText = response.data[i].id;
            div.rows[i + 1].cells[1].innerText = response.data[i].userId;
            div.rows[i + 1].cells[2].innerText = response.data[i].machineId;
            div.rows[i + 1].cells[3].innerText = formatDate(response.data[i].time);
            if (div.rows[i + 1].cells[4].childElementCount == 0) {
                var button = document.createElement("button");
                button.value = i;
                button.innerText = "删除";
                button.onclick = function (e) {
                    if (confirm('确定删除?')) {
                        del(e.currentTarget.value);
                    }
                };
                div.rows[i + 1].cells[4].insertAdjacentElement('beforeend', button);
            }
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
        window.location = "/sharing/rootLogin.html?msg=%e6%9c%aa%e7%99%bb%e5%bd%95"
    } else if (xmlhttp == 500) {
        alert("服务器错误")
    }
}

function setPageOne() {
    page = 1;
    document.getElementById("pageNo").value = 1;
    document.getElementById("nextPageButton").hidden = true;
    document.getElementById("prevPageButton").hidden = true;

}


function del(i) {
    request(uri + "/" + data[i].id, null, afterDel, "delete");
}

function afterDel() {
    if (xmlhttp.status == 204) {
        alert("删除成功！")
        query()
    } else if (xmlhttp.status == 500) {
        alert("服务器错误")
    }
}