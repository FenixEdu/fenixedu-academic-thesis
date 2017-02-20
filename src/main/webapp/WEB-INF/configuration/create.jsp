<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Academic Thesis.

    FenixEdu Academic Thesis is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic Thesis is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic Thesis.  If not, see <http://www.gnu.org/licenses/>.

--%>
<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

${ portal.toolkit() }

<div class="page-header">
  <h1><spring:message code="title.configuration.management"/></h1>
  <h2><spring:message code="title.thesisProposalsConfiguration.create"/></h2>
</div>

<spring:message code='label.executionDegree.select' var='executionDegreeSelect'/>

<c:if test="${!empty createException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.create.used"/></p>
</c:if>

<c:if test="${!empty illegalArgumentException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.create.interval"/></p>
</c:if>

<c:if test="${!empty overlappingIntervalsException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.interval.overlapping"/></p>
</c:if>

<c:if test="${!empty unselectedExecutionDegreeException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.create.unselected.executionDegree"/></p>
</c:if>

<div class="row">
  <form:form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/create" class="form-horizontal" commandname="thesisProposalsConfigurationBean" id="thesisProposalCreateForm">
    ${csrf.field()}
  <div class="form-group">
    <label for="proposalPeriodEnd" class="col-sm-4 control-label"><spring:message code='label.proposalPeriod'/></label>
    <div class="col-sm-4">
      <label for="proposalPeriodStart"  class="col-sm-2 control-label"><spring:message code='label.start'/></label>
      <input type="text" bennu-datetime no-seconds name="proposalPeriodStart" class="form-control" id="proposalPeriodStart" placeholder="<spring:message code='label.start'/>" required="required" value='${command.proposalPeriodStart}'/>
    </div>
    <div class="col-sm-4">
      <label for="proposalPeriodEnd" path="proposalPeriodEnd" class="col-sm-2 control-label"><spring:message code='label.end'/></label>
      <input type="text" bennu-datetime no-seconds name="proposalPeriodEnd" class="form-control" id="proposalPeriodEnd" placeholder="<spring:message code='label.start'/>" required="required" value='${command.proposalPeriodEnd}'/>
    </div>
  </div>

  <div class="form-group">
    <label for="candidacyPeriodStart" class="col-sm-4 control-label"><spring:message code='label.candidacies'/></label>
    <div class="col-sm-4">
      <label for="candidacyPeriodStart"  class="col-sm-2 control-label"><spring:message code='label.start'/></label>
      <input type="text" bennu-datetime no-seconds name="candidacyPeriodStart" class="form-control" id="candidacyPeriodStart" placeholder="<spring:message code='label.start'/>" required="required" value='${command.candidacyPeriodStart}'/>
    </div>
    <div class="col-sm-4">
      <label for="candidacyPeriodEnd" path="candidacyPeriodEnd" class="col-sm-2 control-label"><spring:message code='label.end'/></label>
      <input type="text" bennu-datetime no-seconds name="candidacyPeriodEnd" class="form-control" id="candidacyPeriodEnd" placeholder="<spring:message code='label.start'/>" required="required" value='${command.candidacyPeriodEnd}'/>
    </div>
  </div>

  <div class="form-group">
    <label class="col-sm-4 control-label"><spring:message code='label.executionDegree.select'/></label>
    <div class="col-sm-8">

      <select id="executionYearSelect" class="form-control">
        <option value="NONE" label="<spring:message code='label.executionYear.select'/>"><spring:message code='label.executionYear.select'/></option>
        <c:forEach items="${executionYearsList}" var="executionYear">

        <c:if test="${command.executionDegree.executionYear != executionYear}">
        <option value="${executionYear.externalId}" label="${executionYear.year}">${executionYear.year}</option>
      </c:if>

      <c:if test="${command.executionDegree.executionYear == executionYear}">
      <option selected value="${executionYear.externalId}" label="${executionYear.year}">${executionYear.year}</option>
    </c:if>

  </c:forEach>
</select>

</div>
</div>

<div class="form-group">
  <label for="executionDegree" class="col-sm-4 control-label"></label>
  <div class="col-sm-8">

    <c:if test="${command.executionDegree != null}">
    <select name="executionDegree" class="form-control" id="executionDegreeSelect">
      <option value="NONE" label="<spring:message code='label.executionDegree.select'/>" id="executionDegreeDefaultOption"><spring:message code='label.executionDegree.select'/></option>
      <c:forEach items="${executionDegreeList}" var="executionDegree">
      <option value="${executionDegree.externalId}" label="${executionDegree.presentationName}" data-execution-year="${executionDegree.executionYear.year}">${executionDegree.presentationName}</option>
    </c:forEach>
  </select>
</c:if>

<c:if test="${command.executionDegree == null}">
<select disabled="disabled" name="executionDegree" class="form-control" id="executionDegreeSelect">
  <option value="NONE" label="<spring:message code='label.executionDegree.select'/>" id="executionDegreeDefaultOption"><spring:message code='label.executionDegree.select'/></option>
  <c:forEach items="${executionDegreeList}" var="executionDegree">
  <option value="${executionDegree.externalId}" label="${executionDegree.presentationName}" data-execution-year="${executionDegree.executionYear.year}">${executionDegree.presentationName}</option>
</c:forEach>
</select>
</c:if>
</div>
</div>


<div class="form-group">
  <form:label for="maxThesisCandidaciesByStudent" path="maxThesisCandidaciesByStudent" class="col-sm-4 control-label"><spring:message code='label.maxThesisCandidaciesByStudent.create'/></form:label>
  <div class="col-sm-8">
    <c:set var="maxCandidacy" value="-1"/>
    <c:if test="${command.maxThesisCandidaciesByStudent != null}">
    <c:set var="maxCandidacy" value="${command.maxThesisCandidaciesByStudent}"/>
  </c:if>
  <form:input type="number" class="form-control" id="maxThesisCandidaciesByStudent" path="maxThesisCandidaciesByStudent" placeholder="<spring:message code='label.maxThesisCandidaciesByStudent'/>" required="required" value="${maxCandidacy}"/>
  <div class="help-block"><spring:message code='label.maxThesisCandidaciesByStudent.create.help-message'/></div>
</div>
</div>


<div class="form-group">
  <form:label for="maxThesisProposalsByUser" path="maxThesisProposalsByUser" class="col-sm-4 control-label"><spring:message code='label.maxThesisProposalsByUser.create'/></form:label>
  <div class="col-sm-8">
    <c:set var="maxUser" value="-1"/>
    <c:if test="${command.maxThesisProposalsByUser != null}">
    <c:set var="maxUser" value="${command.maxThesisProposalsByUser}"/>
  </c:if>
  <form:input type="number" class="form-control" id="maxThesisProposalsByUser" path="maxThesisProposalsByUser" placeholder="<spring:message code='label.maxThesisProposalsByUser'/>" required="required" value="${maxUser}"/>
  <div class="help-block"><spring:message code='label.maxThesisProposalsByUser.create.help-message'/></div>
</div>
</div>


<div class="form-group">
  <form:label for="minECTS1stCycle" path="minECTS1stCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.first.cyle'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="0" class="form-control" id="minECTS1stCycle" path="minECTS1stCycle" name="minECTS1stCycle" required="required" value="0"/>
  </div>
</div>

<div class="form-group">
  <form:label for="minECTS2ndCycle" path="minECTS2ndCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.second.cyle'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="0" class="form-control" id="minECTS2ndCycle" path="minECTS2ndCycle" name="minECTS2ndCycle" required="required" value="0"/>
  </div>
</div>


<div class="form-group">
  <div class="col-sm-offset-4 col-sm-8">
    <button type="submit" class="btn btn-primary" id="submitButton" disabled=true><spring:message code="button.create"/></button>
  </div>
</div>

</form:form>
</div>

<script type="text/javascript">
function populateExecutionDegrees(){
  $("#executionDegreeSelect").empty()
  $("#executionDegreeSelect").append($("<option>" + "<spring:message code='label.executionDegree.select'/>" + "</option>").attr("value", "NONE").attr("label", "<spring:message code='label.executionDegree.select'/>").attr("id", "executionDegreeDefaultOption"));

  var year = $("#executionYearSelect").val();

  if(year === "NONE") {
    $('#executionDegreeSelect').attr("disabled","disabled")
  }
  else {
    $('#executionDegreeSelect').removeAttr("disabled");

    $.get("${pageContext.request.contextPath}/configuration/execution-year/" + year + "/execution-degrees", function(response) {

      response.forEach(function(elem) {
        if("${command.executionDegree.externalId}" == elem.externalId) {
          $("#executionDegreeSelect").append($("<option>"+ elem.name +"</option>").attr("value", elem.externalId).attr("label", elem.name).attr("selected", true));
        }
        else {
        $("#executionDegreeSelect").append($("<option>" + elem.name + "</option>").attr("value", elem.externalId).attr("label", elem.name));
        }
      });
    });
  }
}


$("#executionYearSelect").change(populateExecutionDegrees);

$("#executionDegreeSelect").change(function(){
  var selected = $("#executionDegreeSelect").val() != "NONE";

  if(selected) {
    $("#submitButton").attr("disabled", false);
  }
  else {
    $("#submitButton").attr("disabled", true);
  }
});


</script>
