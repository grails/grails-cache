<html>
<body>
    <cache:block key="one">
        First block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="one">
        Second block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="two">
        Third block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="one">
        Fourth block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="two">
        Fifth block counter ${++counter}
    </cache:block>
    <br/>
    <cache:block key="three">
        Sixth block counter ${++counter}
    </cache:block>
</body>
</html>