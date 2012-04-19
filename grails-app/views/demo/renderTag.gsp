<html>
<body>

First invocation: <cache:render template="counterTemplate" model="[counter: 1]" key="1"/><br/>
Second invocation: <cache:render template="counterTemplate" model="[counter: 2]" key="1"/><br/>

Third invocation: <cache:render template="counterTemplate" model="[counter: 3]" key="2"/><br/>
Fourth invocation: <cache:render template="counterTemplate" model="[counter: 4]" key="2"/><br/>

Fifth invocation: <cache:render template="counterTemplate" model="[counter: 5]" key="1"/><br/>

</body>
</html>