<html>
<head>
  <title>Aurora is Loading</title>
  <meta name="layout" content="main"/>
  <meta name="hideNav" content="true"/>
</head>
<body>
  <div class="body">
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <h1>Aurora is starting up. This may take a few minutes.</h1>
    <h3>This page will automatically refresh when Aurora is loaded.<img alt="spinner" src="${resource(dir: 'images', file: 'spinner.gif')}"/></h3>
  </div>
  <script defer type="text/javascript" src="${resource(dir: 'js', file: 'loading.js')}?v=${build}"></script>
</body>
</html>
