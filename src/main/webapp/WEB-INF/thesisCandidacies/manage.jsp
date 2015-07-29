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
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<div class="page-header">
	<h1><spring:message code="title.thesisCandidacy.management"/></h1>
</div>

<div class="well">
	<p><spring:message code="label.candidacies.well"/> : <b>${thesisProposal.identifier} - ${thesisProposal.title}</b></p>
</div>

<c:if test="${!empty error}">
<p class="text-danger"><spring:message code="error.thesisProposal.${error}"/></p>
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
					<c:if test="${studentThesisCandidacy.acceptedByAdvisor}">
						<span class="badge"><spring:message code='label.proposal.attributed'/></span>
					</c:if>
					<c:if test="${bestAccepted.get(studentThesisCandidacy.registration.externalId).preferenceNumber < studentThesisCandidacy.preferenceNumber}">
						<p><spring:message code='label.candidate.already.accepted' arguments="${bestAccepted.get(studentThesisCandidacy.registration.externalId).thesisProposal.identifier};${bestAccepted.get(studentThesisCandidacy.registration.externalId).preferenceNumber};${bestAccepted.get(studentThesisCandidacy.registration.externalId).thesisProposal.getSortedParticipants().get(0).user.name}" argumentSeparator=";"/></p>
					</c:if>
				</td>
				<td> ${studentThesisCandidacy.preferenceNumber}</td>
				<td>${studentThesisCandidacy.timestamp.toString('dd-MM-YYY HH:mm')}</td>
				<td>
					<c:if test="${!studentThesisCandidacy.acceptedByAdvisor}">
					<form:form role="form" method="POST" action="${pageContext.request.contextPath}/${action}/${studentThesisCandidacy.externalId}" class="form-horizontal">
					${csrf.field()}
						<c:choose>
				<c:when test="${not empty coordinatorManagement}">
					<button type="submit" class="btn btn-default acceptButton">
						<spring:message code='button.candidacy.accept' />
					</button>
				</c:when>
				<c:otherwise>
					<c:if test="${bestAccepted.get(studentThesisCandidacy.registration.externalId).preferenceNumber < studentThesisCandidacy.preferenceNumber}">
					<button type="submit" class="btn btn-default acceptButton" disabled="true">
						<spring:message code='button.candidacy.accept' />
					</button>
					<p><spring:message code='label.candidacy.unacceptable'/></p>
				</c:if>
				<c:if test="${!(bestAccepted.get(studentThesisCandidacy.registration.externalId).preferenceNumber < studentThesisCandidacy.preferenceNumber)}">
				<button type="submit" class="btn btn-default acceptButton">
					<spring:message code='button.candidacy.accept' />
				</button>
			</c:if>
			</c:otherwise>
			</c:choose>
		</form:form>
	</c:if>
	<c:if test="${studentThesisCandidacy.acceptedByAdvisor}">
	<form:form role="form" method="POST" action="${pageContext.request.contextPath}/${baseAction}/reject/${studentThesisCandidacy.externalId}" class="form-horizontal">
		${csrf.field()}
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
