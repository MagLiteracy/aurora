    <h2>Change User Role</h2>
    <table>
        <tr>
            <td class="name">User Name:</td>
            <td><g:textField id="userName" name="userName" value="${params.userName}"/></td>
        </tr>
        <tr>
            <td class="name">User role:</td>
            <td><g:select id="userRole" name="userRole" from="${roles}" optionKey="id" optionValue="name"/></td>
        </tr>
    </table>
    <div class="buttons">
        <g:submitButton name="userRoleSubmit" class="save" id="userRoleSubmit" value="Change/Add Role" action="usersSave" title="Accept role changing"/>
    </div>

    <h2>Remove User</h2>
    <table>
        <tr>
            <td class="name">User Name:</td>
            <td><g:textField id="userNameRemove" name="userNameRemove" value="${params.userName}"/></td>
        </tr>
    </table>
    <div class="buttons">
        <g:submitButton name="userRoleRemove" class="save" id="userRoleRemove" value="Remove User" action="usersSave" title="Remove user"/>
    </div>
