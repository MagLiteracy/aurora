<g:if test="${occasion.name() == Occasion.APRILFOOLS.name()}">
  <link rel="stylesheet" href="${resource(dir: 'js/shadowbox-3.0.3', file: 'shadowbox.css')}?v=${build}"/>
  <script defer type="text/javascript" src="${resource(dir: 'js/shadowbox-3.0.3', file: 'shadowbox.js')}?v=${build}"></script>
  <script defer type="text/javascript" src="${resource(dir: 'js', file: 'aprilfools.js')}?v=${build}"></script>
  <g:if test="${autoLaunchFullAprilFoolsJoke}">
    <script defer type="text/javascript" src="${resource(dir: 'js', file: 'aprilfoolslaunch.js')}?v=${build}"></script>
  </g:if>
</g:if>
