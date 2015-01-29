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


${portal.toolkit()}

<div class="page-header">
	<h1><spring:message code="title.thesisProposal.management"/></h1>
</div>

<div class="well">
	<p>
		<spring:message code='label.candidates.admin.well'/>
	</p>
</div>

	<h3><spring:message code="label.available.candidacies" arguments="${configuration.executionDegree.degree.presentationName}"/></h3>
	
	<a href="${pageContext.request.contextPath}/admin-proposals?configuration=${configuration.externalId}"><spring:message code="label.back"/></a>
	
	<c:if test="${empty registrations}">
		<spring:message code="label.candidacies.empty"/>
	</c:if>
	<c:if test="${!empty registrations}">
	<div class="table-responsive">
		<table class="table table-condensed table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="label.candidate"/></th>
					<th><spring:message code='label.proposal'/></th>
					<th><spring:message code='label.student.candidacy.accepted'/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${registrations}" var="registration">
						<td rowspan="${registration.value.size()}">
							${registration.key.student.person.name} (${registration.key.student.person.username})
						</td>
						<c:forEach items="${registration.value}" var="candidacy" varStatus="loop">
							<c:if test="${loop.index == 0}">
								<c:set var="anyAccepted" value="false"/>
							</c:if>
								<td>
									<c:set var="title" value="${candidacy.preferenceNumber} ) ${candidacy.thesisProposal.identifier} - ${candidacy.thesisProposal.title}"/>
									<c:choose>
										<c:when test="${!anyAccepted && candidacy.acceptedByAdvisor}">
											<b>${title}</b>
											<c:set var="anyAccepted" value="true"/>
										</c:when>
										<c:otherwise>
											${title}
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:if test="${candidacy.acceptedByAdvisor}">
										<spring:message code='label.yes'/>
									</c:if>
									<c:if test="${!candidacy.acceptedByAdvisor}">
										<spring:message code='label.no'/>
									</c:if>
									</td>
								<td>
									<a href="${pageContext.request.contextPath}/admin-proposals/deleteCandidacy/${candidacy.externalId}?configuration=${configuration.externalId}"> <spring:message code="button.proposal.unapply"/> </a>
									<c:if test="${!candidacy.acceptedByAdvisor}">
									| <a href="${pageContext.request.contextPath}/admin-proposals/acceptCandidacy/${candidacy.externalId}?configuration=${configuration.externalId}"> <spring:message code="button.candidacy.accept"/>  </a>
								</c:if>
								</td>
							</tr>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
