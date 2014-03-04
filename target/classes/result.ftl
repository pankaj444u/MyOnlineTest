<!DOCTYPE html>

<html>
  <head>
    <title>Welcome</title>
    <style type="text/css">
      .label {text-align: right}
      .error {color: red}
      .result {color: green}
    </style>

  </head>

  <body>
    Welcome ${username}
	
          <#list result as resultP>
          <br />
          ${resultP}
          </#list>
          <br/>
          <br/>
          ${totalscore} / 100  
    	
  </body>

</html>