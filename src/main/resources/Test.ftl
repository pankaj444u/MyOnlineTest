<!DOCTYPE html>
<html>
	<head>
	<title>My Test...</title>
	</head>

<body>
<form action="/GK" method="POST">
<#list questionList as questionObj>

	<p> ${questionObj.getQdescription()} </p>

	<#list questionObj.getQoptions() as option >
	<p> <input  name="${questionObj_index + 1}" type="radio" value="${option}">${option}</input> </p>
	</#list>
	
	
	<p> <input  name="answer_${questionObj_index + 1}" type="hidden" value="${questionObj.getAanswer()}"></input> </p>
	

</#list>	

<input  type="submit" value="Submit" />
</form>	
</body>
</html>