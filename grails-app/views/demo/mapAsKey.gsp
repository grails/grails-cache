<html>
<body>
    <cache:block key="[name: 'one']">
        First block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="[name: 'one']">
        Second block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="[name: 'two']">
        Third block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="[name: 'one']">
        Fourth block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="[name: 'two']">
        Fifth block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="[name: 'three']">
        Sixth block counter ${++counter}
    </cache:block>
</body>
</html>