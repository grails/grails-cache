<html>
<body>

First invocation: <cache:render template="counterTemplate" model="[counter: counter]" key="1" ttl="$ttl"/><br/>
Second invocation: <cache:render template="counterTemplate" model="[counter: counter + 1]" key="1" ttl="$ttl"/><br/>

Third invocation: <cache:render template="counterTemplate" model="[counter: counter + 2]" key="2" ttl="$ttl"/><br/>
Fourth invocation: <cache:render template="counterTemplate" model="[counter: counter + 3]" key="2" ttl="$ttl"/><br/>

Fifth invocation: <cache:render template="counterTemplate" model="[counter: counter + 4]" key="1" ttl="$ttl"/><br/>

</body>
</html>