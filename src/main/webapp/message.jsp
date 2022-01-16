<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String contextPath=request.getContextPath(); %>
<link type="text/css" rel="stylesheet" href="<%=contextPath%>/css/style.css">
<body>
<center>
    <table border="0" width="80%" cellspacing="1" cellpadding="5" bgcolor="#999999" style="word-break:break-all;margin-top:100px">
        <tr class="listhead" height="25">
            <td>文件上传</td>
        </tr>
        <tr bgcolor="#F5F5F5"><td>${requestScope.message}</td></tr>
    </table>
</center>
</body>
