<html>
<head>
  <meta name="layout" content="main"/>
</head>
<body>
  <div class="homeWrapper">
    <div class="welcome">Welcome to Aurora</div>
    <div class="section homePage">
      <table cellpaddig=0 cellspacing=0>
        <thead>
        <tr>
          <th>Openstack Objects</th>
          <th class="tdSeparated">Aurora Tasks</th>
          <g:if test='${externalLinks}'>
            <th>External Links</th>
          </g:if>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>
            <ul>
              <li class="homeImagesLink">Manage <g:link controller="image" action="list"
                                 title="An 'Amazon Machine Image' is a snapshot if a running machine used to create new instances.">Images</g:link></li>
              <li class="homeSecurityGroupLink">Manage <g:link controller="securityGroup" action="list"
                                 title="A 'Security Group' is the collection of network ingress rules for an Application.">Security Groups</g:link></li>
              <li class="homeInstancesLink">Manage Running <g:link controller="instance" action="list"
                                         title="An 'Instance' is a single running machine instance of an Application.">Instances</g:link></li>
            </ul>
          </td>
          <td class="tdSeparated">
              <ul>
                  <li class="homeTasksLink">Monitor Background <g:link controller="task" action="list"
                      title="Watch the progress of long-running workflow processes.">Tasks</g:link>
                  </li>
              </ul>
          </td>
          <g:if test="${externalLinks}">
            <td class="tdSeparated">
              <ul class="externalLinks">
                <g:each in="${externalLinks}" var="link">
                  <g:set var="image" value="${link.image ?: '/images/tango/16/categories/applications-internet.png'}"/>
                  <li><a style="background-image: url(${image})" href="${link.url.encodeAsHTML()}">${link.text?.encodeAsHTML()}</a></li>
                </g:each>
              </ul>
            </td>
          </g:if>
        </tr>
        <tbody>
      </table>
    </div>
  </div>
</body>
</html>
