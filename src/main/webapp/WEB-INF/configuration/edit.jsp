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
  <h1>
    <spring:message code="title.configuration.management"/>
    <h2><spring:message code="title.thesisProposalsConfiguration.edit"/> - ${command.executionDegree.degree.sigla} ${command.executionDegree.executionYear.year}</h2>
  </h1>
</div>

<div class="row">
  <form:form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/edit" class="form-horizontal" commandname="thesisProposalsConfigurationBean" id="thesisProposalCreateForm">
  ${csrf.field()}
  <c:if test="${!empty editException}">
  <p class="text-danger"><spring:message code="error.thesisProposal.configuration.create.used"/></p>
</c:if>

<c:if test="${!empty illegalArgumentException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.create.interval"/></p>
</c:if>

<c:if test="${!empty deleteException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.delete.used"/></p>
</c:if>

<c:if test="${!empty overlappingIntervalsException}">
<p class="text-danger"><spring:message code="error.thesisProposal.configuration.interval.overlapping"/></p>
</c:if>

<spring:message code='label.proposalPeriod.start' var="proposalPeriodStart"/>
<spring:message code='label.proposalPeriod.end' var="proposalPeriodEnd"/>
<spring:message code='label.candidacyPeriod.start' var="candidacyPeriodStart"/>
<spring:message code='label.candidacyPeriod.end' var="candidacyPeriodEnd"/>
<spring:message code='label.executionDegree' var="executionDegree"/>
<spring:message code='label.maxThesisCandidaciesByStudent' var="maxThesisCandidaciesByStudent"/>
<spring:message code='label.maxThesisProposalsByUser' var="maxThesisProposalsByUser"/>

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

<div class="form-group hidden">
  <input type="hidden" class="form-control" id="executionDegree" path="executionDegree" name="executionDegree" placeholder="${executionDegree}" required="required" value="${command.executionDegree}"/>
</div>

<div class="form-group">
  <form:label for="maxThesisCandidaciesByStudent" path="maxThesisCandidaciesByStudent" class="col-sm-4 control-label"><spring:message code='label.maxThesisCandidaciesByStudent.create'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="-1" class="form-control" id="maxThesisCandidaciesByStudent" path="maxThesisCandidaciesByStudent" placeholder="${maxThesisCandidaciesByStudent}" required="required"/>
  </div>
</div>

<div class="form-group">
  <form:label for="maxThesisProposalsByUser" path="maxThesisProposalsByUser" class="col-sm-4 control-label"><spring:message code='label.maxThesisProposalsByUser.create'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="-1" class="form-control" id="maxThesisProposalsByUser" path="maxThesisProposalsByUser" placeholder="${maxThesisProposalsByUser}" required="required"/>
  </div>
</div>

<div class="form-group">
  <form:label for="minECTS1stCycle" path="minECTS1stCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.first.cyle'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="0" class="form-control" id="minECTS1stCycle" path="minECTS1stCycle" name="minECTS1stCycle" required="required" value="${command.minECTS1stCycle}"/>
  </div>
</div>

<div class="form-group">
  <form:label for="minECTS2ndCycle" path="minECTS2ndCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.second.cyle'/></form:label>
  <div class="col-sm-8">
    <form:input type="number" min="0" class="form-control" id="minECTS2ndCycle" path="minECTS2ndCycle" name="minECTS2ndCycle" required="required" value="${command.minECTS2ndCycle}"/>
  </div>
</div>


<div class="form-group">
  <form:input type="hidden" class="form-control" id="ExternalId" placeholder="ExternalId" path="externalId" required="required"/>
</div>

<div class="form-group">
  <div class="col-sm-offset-4 col-sm-8">
    <button type="submit" class="btn btn-default" id="submitButton"><spring:message code='button.save'/></button>
    <button type="button" class="btn btn-danger" id="deleteButton"><spring:message code="button.delete"/></button>
  </form>
</div>
</div>

</form:form>
</div>
<form method="POST" action="${pageContext.request.contextPath}/configuration/delete/${command.externalId}" id="deleteForm"></form>

<script type="text/javascript">
$("#deleteButton").on("click", function(){ $("#deleteForm").submit(); })
</script>
