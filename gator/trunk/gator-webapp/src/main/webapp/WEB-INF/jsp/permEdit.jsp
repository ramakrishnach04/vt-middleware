<%@ include file="includes/top.jsp" %>

<c:choose>
  <c:when test="${perm.new}">
    <c:set var="action" value="Add Permissions" />
  </c:when>
  <c:otherwise>
    <c:set var="action" value="Edit Permissions" />
  </c:otherwise>
</c:choose>

<div class="crumbs">
  <span>
    <a href="<c:url value="/secure/project/list.html" />">Project Listing</a>
  </span>
  <span>&raquo;</span>
  <span>
    <a href="<c:url value="/secure/project/${perm.project.name}/edit.html" />">
    Edit <em>${perm.project.name}</em></a>
  </span>
  <span>&raquo;</span>
  <span>${action}</span>
</div>

<h1>${action}</h1>

<form:form method="post" commandName="perm">
  <fieldset>
  <legend>Security Permissions</legend>

  <spring:hasBindErrors name="perm">
    <div id="validation-summary"><spring:message code="error.validationSummary" /></div>
  </spring:hasBindErrors>

  <div class="field">
    <div><label for="name">Security Identifier</label></div>
    <form:errors cssClass="field-error" path="name" element="div" />
    <div class="note">Examples: bob, ROLE_IT-STAFF</div>
    <div><form:input id="name" path="name" size="50" /></div>
  </div>
  <div class="field">
    <div><label for="permissions">Permissions</label></div>
    <div class="note">
	    <ul>
	      <li>r - Read permission</li>
	      <li>w - Write/Edit permission</li>
	      <li>d - Delete permission</li>
	    </ul>
	    <div>Examples: rwd, r, rw</div>
	    <div>Note that permissions only apply to whole projects.</div>
    </div>
    <form:errors cssClass="field-error" path="permissions" element="div" />
    <div><form:input id="permissions" path="permissions" size="10" /></div>
  </div>
  <div class="field">
		<div><input type="submit" name="action" value="Update" /></div>
  </div>
  </fieldset>

</form:form>

<%@ include file="includes/bottom.jsp" %>
