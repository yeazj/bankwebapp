<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@include file="pageHeader.jsp"%>
    <body>
        <%@include file="header.jsp"%>
        <main id="content" class="mainContent sutd-template" role="main">
            <div class="container">
                <%@include file="errorMessage.jsp"%>
                <div id="createTransaction">
                    <form id="uploadForm" action="upload" method="post" enctype="multipart/form-data">
                        <div id="input-group-toAccount" class="form-group">
                            <label for="uploadFile" class="control-label">
                                First Line of file, type : Code,Account,Amount <br/>
                                Second Line, type data (e.g) : 1111-2222,10,5 <br/>
                                And so on ...
                            </label>
                            <input type="file" id="uploadfile" name="uploadFile">
                        </div>
                        <button id="uploadTransBtn" type="submit" class="btn btn-default">Submit</button>
                    </form>
                </div>
            </div>
        </main>
    </body>
</html>
