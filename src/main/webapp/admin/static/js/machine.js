var page = 1;
var count;
var xmlhttp;
const uri = "../../machine";
var data;
var machineId;


function bodyOnLoad() {
    addressField = document.getElementById("addressField");
    longitudeField = document.getElementById("longitudeField");
    latitudeField = document.getElementById("latitudeField");
    newMachineRadio = document.getElementById("newMachineRadio");
    postButton = document.getElementById("postButton");
    putButton = document.getElementById("putButton");
    deleteButton = document.getElementById("deleteButton");
    var url = uri + "/list?pageNo=1";
    request(url, null, showMachine, "get")
}

function prevPage() {
    --page;
    query();
}

function nextPage() {
    ++page;
    query();
}

function query() {
    var status;
    var s = document.getElementsByName("status");
    for (var i = 0; i < s.length; i++) {
        if (s[i].checked) {
            status = s[i].value;
            break;
        }
    }

    var url = uri + "/list?pageNo=" + page;
    if (status != "") {
        url += "&status=" + status;
    }
    request(url, null, showMachine, "get")
}

function pageUpdate() {
    var prevPageButton = document.getElementById("prevPageButton");
    var nextPageButton = document.getElementById("nextPageButton");
    prevPageButton.hidden = (page === 1);
    nextPageButton.hidden = (page * 10 >= count);
    document.getElementById("page").innerText = "页码" + page;
}

function showMachine() {
    if (xmlhttp.status === 200) {
        document.getElementById("noneLabel").hidden = true;
        var response = JSON.parse(xmlhttp.responseText);
        data = response.data;
        var div = document.getElementById("machines");
        var i = 0;
        for (; i < response.data.length; i++) {
            if (div.rows[i + 1] == null) {
                div.insertRow(i + 1)
                div.rows[i + 1].insertCell(0);
                div.rows[i + 1].insertCell(1);
                div.rows[i + 1].insertCell(2);
                div.rows[i + 1].insertCell(3);
                div.rows[i + 1].insertCell(4);
                div.rows[i + 1].insertCell(5);
            }
            div.rows[i + 1].cells[0].innerText = data[i].id;
            div.rows[i + 1].cells[1].innerText = data[i].address;


            switch (data[i].status) {

                case -1 : {
                    div.rows[i + 1].cells[2].innerText = "关机";
                }
                    break;
                case 0 : {
                    div.rows[i + 1].cells[2].innerText = "等待";
                }
                    break;
                case 1 : {
                    div.rows[i + 1].cells[2].innerText = "运行中";
                }
                default: {
                    div.rows[i + 1].cells[2].innerText = "状态未知";
                }
            }


            div.rows[i + 1].cells[3].innerText = data[i].longitude;
            div.rows[i + 1].cells[4].innerText = data[i].latitude;
            if (div.rows[i + 1].cells[5].childElementCount == 0) {
                var radio = document.createElement("input");
                radio.value = i;
                radio.setAttribute("type", "radio");
                radio.name = "id";
                radio.onclick = function (e) {
                    edit(e.currentTarget.value);
                };
                div.rows[i + 1].cells[5].insertAdjacentElement('beforeend', radio);
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
        var div = document.getElementById("machines");
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
    document.getElementById("nextPageButton").hidden = true;
    document.getElementById("prevPageButton").hidden = true;

}

function edit(i) {
    addressField.value = data[i].address;
    longitudeField.value = data[i].longitude;
    latitudeField.value = data[i].latitude;
    machineId = data[i].id;
    putButton.hidden = false;
    deleteButton.hidden = false;
    postButton.hidden = true;
}

function cleanField() {
    addressField.value = "";
    longitudeField.value = "";
    latitudeField.value = "";
}

function newMachine() {
    cleanField();
    postButton.hidden = false;
    putButton.hidden = true;
    deleteButton.hidden = true;
}

function post() {
    var url = uri + "?";
    url += "address=" + addressField.value;
    url += "&longitude=" + longitudeField.value;
    url += "&latitude=" + latitudeField.value;
    request(url, null, afterPOST, "post");
}

function put() {
    var url = uri + "/" + machineId + "?";
    if (addressField.value != "") {
        url += "address=" + addressField.value + "&";
    }
    if (longitudeField.value != "") {
        url += "longitude=" + longitudeField.value + "&";
    }
    if (latitudeField.value != "") {
        url += "latitude=" + latitudeField.value + "&";
    }
    url.slice(0, url.length - 1);
    var func = function () {
        afterURD("更新");
    }
    request(url, null, func, "put");
}

function del() {
    var func = function () {
        afterURD("删除");
    }
    request(uri + "/" + machineId, null, func, "delete");
}

function afterURD(type) {
    if (xmlhttp.status == 204) {
        alert(type + "成功！")
        query()
    } else if (xmlhttp.status == 500) {
        alert("服务器错误")
    }
}

function afterPOST() {
    if (xmlhttp.status == 200) {
        var machine = JSON.parse(xmlhttp.responseText);
        alert("添加成功！新设备id为：" + machine.id);
        query()
    } else if (xmlhttp.status == 500) {
        alert("服务器错误")
    }
}