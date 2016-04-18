<html>
<head>
    <meta name="layout" content="grails-cache"/>
</head>
<body>

First invocation: <cache:render template="counterTemplate" model="[counter: counter]" key="1"/><br/>
Second invocation: <cache:render template="counterTemplate" model="[counter: counter + 1]" key="1"/><br/>

Third invocation: <cache:render template="counterTemplate" model="[counter: counter + 2]" key="2"/><br/>
Fourth invocation: <cache:render template="counterTemplate" model="[counter: counter + 3]" key="2"/><br/>

Fifth invocation: <cache:render template="counterTemplate" model="[counter: counter + 4]" key="1"/><br/>

</body>
</html>