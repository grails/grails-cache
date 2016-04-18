<html>
<head>
    <meta name="layout" content="grails-cache"/>
</head>
<body>
<cache:block ttl="$ttl">
    First block counter ${++counter}
</cache:block>
<br/>
<cache:block ttl="$ttl">
    Second block counter ${++counter}
</cache:block>
<br/>
<cache:block ttl="$ttl">
    Third block counter ${++counter}
</cache:block>
</body>
</html>