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
	<p><spring:message code="label.proposals.well"/></p>
	<c:if test="${not empty executionYears}">
		<form role="form" method="GET" action="${pageContext.request.contextPath}/${baseAction}" class="form" id="thesisConfigForm">
			${csrf.field()}
			<div class="form-group">
				<label for="executionYear"><spring:message code="label.configuration"/></label>
				<select name="executionYear" id="executionYear" class="form-control">
					<c:forEach items="${executionYears}" var="year">
						<option <c:if test="${year.externalId eq executionYear.externalId}">selected="selected"</c:if> value="${year.externalId}" label='${year.qualifiedName}'> ${year.qualifiedName} </option>
					</c:forEach>
				</select>
			</div>
		</form>
	</c:if>
	<c:if test="${not empty configurations}">
		<p><spring:message code="label.periods"/></p>
		<div class="row">
			<div class="col-lg-5 ">
				<table class="table table-condensed">
					<thead>
						<tr>
							<th><spring:message code="label.executionDegrees"/></th>
							<th><spring:message code="label.proposalPeriod"/></th>
							<th><spring:message code="label.candidacyPeriod"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="configuration" items="${configurations}">
							<tr>
								<td>${configuration.presentationName}</td>
								<td>${configuration.proposalPeriod.start.toString('dd-MM-YYY')} <spring:message code="label.to"/> ${configuration.proposalPeriod.end.toString('dd-MM-YYY')}</td>
								<td>${configuration.candidacyPeriod.start.toString('dd-MM-YYY')} <spring:message code="label.to"/> ${configuration.candidacyPeriod.end.toString('dd-MM-YYY')}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
</div>

<c:if test="${not empty deleteException}">
	<p class="text-danger"><spring:message code="error.thesisProposal.delete"/></p>
</c:if>

<c:if test="${not empty error}">
	<p class="text-danger"><spring:message code="error.thesisProposal.${error}"/></p>
</c:if>

<c:url var="createUrl" value="/${baseAction}/create" />
<c:url var="transposeUrl" value="/${baseAction}/transpose" />
<a class="btn btn-default" href="${createUrl}"><spring:message code="button.create"/></a>
<a class="btn btn-default" href="${transposeUrl}"><spring:message code="button.transpose"/></a>

<hr />

<table class="table">
	<thead>
		<tr>
			<th>
				<spring:message code='label.thesis.id'/>
			</th>
			<th>
				<spring:message code='label.title'/>
			</th>
			<th>
				<spring:message code='label.executionDegree' />
			</th>
			<th>
				<spring:message code='label.participants'/>
			</th>
			<th>
				<spring:message code='label.number.of.candidacies'/>
			</th>
			<th>
				<spring:message code='label.proposal.status'/>
			</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${proposals}" var="thesisProposal">
			<c:set var="degreesLabels" value="${fn:join(service.getThesisProposalDegrees(thesisProposal), ',')}"/>
			<tr>
				<td>${thesisProposal.identifier}</td>
				<td>${thesisProposal.title}</td>
				<td>${degreesLabels}</td>
				<td>
					<c:forEach items="${thesisProposal.getSortedParticipants()}" var="participant">
						<div>${participant.name} (${participant.participationPercentage}%)
							<c:if test="${! empty participantLabelService}">
								<small>-</small> <b>${participantLabelService.getInstitutionRole(participant)}</b>
							</c:if>
						</div>
					</c:forEach>
				</td>
				<td>${thesisProposal.getStudentThesisCandidacySet().size()}
				<td>
					<c:if test="${thesisProposal.hidden}">
						<spring:message code='label.proposal.status.hidden'/>
					</c:if>
					<c:if test="${!thesisProposal.hidden}">
						<spring:message code='label.proposal.status.visible'/>
					</c:if>
				</td>
				<td width="15%">
					<c:url var="editUrl" value="/${baseAction}/edit/${thesisProposal.externalId}"/>
					<p></p>
					<div class="btn-group btn-group-xs">
						<c:if test="${thesisProposal.getSingleThesisProposalsConfiguration().getProposalPeriod().containsNow()}">
							<a href="${editUrl}" class="btn btn-default"><spring:message code="button.edit"/></a>
						</c:if>
						<button class='detailsButton btn btn-default' data-observations="<c:out escapeXml="true" value="${thesisProposal.observations}"/>" data-requirements="<c:out escapeXml="true" value="${thesisProposal.requirements}"/>" data-goals="<c:out escapeXml="true" value="${thesisProposal.goals}"/>" data-localization="<c:out value="${thesisProposal.localization}"/>" data-degrees="${degreesLabels}" value='<spring:message code="button.details"/>' data-thesis="${thesisProposal.externalId}">
							<spring:message code="label.details"/>
						</button>
						<c:if test="${thesisProposal.studentThesisCandidacy.size() > 0}">
							<c:url var="manageUrl" value="/${baseAction}/manage/${thesisProposal.externalId}"/>
							<a href="${manageUrl}" class="btn btn-default <c:if test="${service.canTeacherAcceptedCandidacy(thesisProposal)}">btn-warning</c:if>"><spring:message code="label.candidacies.manage"/></a>
						</c:if>
					</div>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<style>
form{
	display: inline
}
</style>

<!-- Modal -->
<div class="modal fade" id="view" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="form-horizontal modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close"><span class="sr-only"><spring:message code="button.close"/></span></button>
				<h3 class="modal-title"><spring:message code="label.details"/></h3>
				<small class="explanation"><spring:message code="label.modal.proposals.details"/></small>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.goals'/></label>
					<div class="col-sm-10">
						<div class="information goals"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<div class="information requirements"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<div class="information localization"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.observations'/></label>
					<div class="col-sm-10">
						<div class="information observations"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.executionDegrees'/></label>
					<div class="col-sm-10">
						<div class="information degrees"></div>
					</div>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code='button.close'/></button>
			</div>
		</div>
	</div>
</div>

<style type="text/css">
.information{
	margin-top: 7px;
	margin-left: 10px;
}
</style>

<script type="text/javascript">



$(".manageButton").on("click", function(){
	var id = $(this).data('thesis-proposal')
	$("#" + id).submit();
})


jQuery(document).ready(function(){
	jQuery('.detailsButton').on('click', function(event) {
		$("#details" + $(this).data("thesis")).toggle('show');

	});

	$("select[name=executionYear]").change(function() {
		$("#thesisConfigForm").submit();
	});
});

$(function(){
	$(".detailsButton").on("click", function(evt){
		var e = $(evt.target);

		['observations','requirements','goals','localization','degrees'].map(function(x){
			$("#view ." + x).html(e.data(x).replace(/\n/g, '<br/>'));
		});

		$('#view').modal('show');
	});
})
</script>
