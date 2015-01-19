<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<div class="page-header">
	<h1><spring:message code="title.thesisCandidacy.management"/></h1>
</div>

<div class="well">
	<p><spring:message code="label.candidacies.well"/> - ${thesisProposal.title}</p>
</div>

<c:if test="${!empty outOfCandidacyPeriodException}">
<p class="text-danger"><spring:message code="error.thesisProposal.candidacy.confirm.outOfCandidacyPeriodException"/></p>
</c:if>

<c:if test="${!empty candidaciesList}">
<table class="table">
	<colgroup>
		<col></col>
		<col></col>
	</colgroup>
	<thead>
		<tr>
			<th>
				<spring:message code="label.candidate" />
			</th>
			<th>
				<spring:message code="label.candidacy.preference" />
			</th>
			<th>
				<spring:message code="label.candidacy.timestamp" />
			</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${candidaciesList}" var="studentThesisCandidacy">
		<tr>
			<td>
				<a href="${fr:checksumLink(pageContext.request, '/teacher/viewStudentCurriculum.do?method=prepare&registrationOID='.concat(studentThesisCandidacy.registration.externalId))}">
					${studentThesisCandidacy.registration.student.name}</a>
				</td>
				<td> ${studentThesisCandidacy.preferenceNumber + 1}</td>
				<td>${studentThesisCandidacy.timestamp.toString('dd-MM-YYY HH:mm')}</td>
				<td>
					<c:if test="${!studentThesisCandidacy.acceptedByAdvisor}">
					<form:form role="form" method="POST" action="${pageContext.request.contextPath}/proposals/accept/${studentThesisCandidacy.externalId}" class="form-horizontal">

					<c:if test="${bestAccepted.get(studentThesisCandidacy.registration.externalId) < studentThesisCandidacy.preferenceNumber}">
					<button type="submit" class="btn btn-default acceptButton" disabled="true">
						<spring:message code='button.candidacy.accept' />
					</button>
					<p>You can't accept this student since it has already been accepted in a more prefereble proposal</p>
				</c:if>
				<c:if test="${!(bestAccepted.get(studentThesisCandidacy.registration.externalId) < studentThesisCandidacy.preferenceNumber)}">
				<button type="submit" class="btn btn-default acceptButton">
					<spring:message code='button.candidacy.accept' />
				</button>
			</c:if>

		</form:form>
	</c:if>

	<c:if test="${studentThesisCandidacy.acceptedByAdvisor}">
	<form:form role="form" method="POST" action="${pageContext.request.contextPath}/proposals/reject/${studentThesisCandidacy.externalId}" class="form-horizontal">
	<button type="submit" class="btn btn-default removeButton">
		<spring:message code='button.candidacy.reject' />
	</button>
</form:form>
</c:if>
</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
<c:if test="${empty candidaciesList}">
<div class="alert alert-warning" role="alert">
<spring:message code='label.proposal.candidacies.empty' />
</div>
</c:if>
