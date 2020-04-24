<%--
  Created by IntelliJ IDEA.
  User: hayring
  Date: 2019/12/30
  Time: 7:06 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>使用记录</title>
</head>
<script>
    function clearMachineId() {
        document.getElementById("machineIdField").value = null;
        document.getElementById("fromTimeField").value = null;
        document.getElementById("toTimeField").value = null;
    }

    function clearTime() {
        document.getElementById("fromTimeField").value = null;
        document.getElementById("toTimeField").value = null
    }

    function clearAll() {
        clearMachineId();
        clearTime()
    }

    function nextPage() {
        var page = document.getElementById("pageNo").value;
        document.getElementById("pageNo").value = Number(page) + 1;
        document.getElementById("form").submit();
    }

    function prevPage() {
        var page = document.getElementById("pageNo").value;
        document.getElementById("pageNo").value = Number(page) - 1;
        document.getElementById("form").submit();
    }

    function setPageOne() {
        document.getElementById("pageNo").value = 1;
        document.getElementById("nextPageButton").style.display = "none";
        document.getElementById("prevPageButton").style.display = "none";

    }


</script>
<body>
<h1>教学设备维护记录</h1>
<h2>管理员：<c:out value="${sessionScope.ADMIN_CONTEXT.id}"/></h2>
<p><a href="adminMain.jsp">首页</a></p>

<form id="form" action="requestMaintenance.do" method="post">
    检索条件<br/>
    管理员名<input type="text" name="adminId" id="adminIdField" onchange="setPageOne()" <c:if
        test="${!empty adminId}"> value=${adminId}  </c:if>/>
    设备编号<input type="text" name="machineId" id="machineIdField" onchange="setPageOne()" <c:if
        test="${!empty machineId}"> value=${machineId}  </c:if>/>
    时间从：<input type="date" name="fromTime" id="fromTimeField" onchange="setPageOne()" <c:if
        test="${!empty fromTime}"> value=${fromTime} </c:if>/>
    到：<input type="date" name="toTime" id="toTimeField" onchange="setPageOne()" <c:if
        test="${!empty toTime}"> value=${toTime}  </c:if>/>
    <input type="hidden" name="page" id="pageNo"
           <c:if test="${!empty dataPage}">value=${dataPage.currentPageNo} </c:if>/>
    <input type="hidden" name="count" id="countNo"
           <c:if test="${!empty dataPage}">value=${dataPage.totalCount} </c:if>/>
    <input type="submit"/>
</form>
<button onclick="clearMachineId()">清除设备编号</button>
<button onclick="clearMachineId()">清除日期</button>
<button onclick="clearAll()">清除全部</button>
<c:choose>
    <c:when test="${empty dataPage}">
        <div>没有记录</div>
        <c:if test="${!empty errorMsg}">
            <div style="color:red">${errorMsg}</div>
        </c:if>
    </c:when>
    <c:otherwise>
        <c:if test="${dataPage.hasPreviousPage}">
            <button id="prevPageButton" onclick="prevPage()">上一页</button>
        </c:if>
        <c:if test="${dataPage.hasNextPage}">
            <button id="nextPageButton" onclick="nextPage()">下一页</button>
        </c:if>
        页码：${dataPage.currentPageNo}/${dataPage.totalPageCount}
        <table border="2" style="table-layout:fixed">
            <tr>
                <td>管理员</td>
                <td>机器编号</td>
                <td>时间</td>
                <td></td>
            </tr>
            <c:forEach items="${dataPage.data}" var="maintenance">
                <tr>
                    <td>${maintenance.adminId}</td>
                    <td>${maintenance.machineId}</td>
                    <td><fmt:formatDate value="${maintenance.time}" pattern="yyyy年MM月dd日 HH时mm分ss秒"/></td>
                </tr>
            </c:forEach>

        </table>
        <br/>
    </c:otherwise>
</c:choose>


</body>
</html>
