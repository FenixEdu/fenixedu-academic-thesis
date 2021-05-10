<%@ page import="org.fenixedu.academic.thesis.domain.ThesisProposal" %>
<%@ page import="java.util.List" %>
<%@ page import="org.fenixedu.academic.domain.ExecutionDegree" %><%--

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


<spring:url var="datatablesUrl" value="/js/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/js/dataTables/media/js/jquery.dataTables.bootstrap.min.js"/>
<spring:url var="datatablesCssUrl" value="/css/dataTables/dataTables.bootstrap.min.css"/>

<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<link rel="stylesheet" href="${datatablesCssUrl}">

<script type="text/javascript">
	$(document).ready(function() {
		$('#proposalsTable').DataTable( {
			"paging": false,
			"order": [[ 4, "desc" ]],
			"columnDefs": [{ "orderable": false, "targets": [6] }]
		} );
	});
</script>

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

<table id="proposalsTable" class="table table-bordered table-hover">
	<thead>
		<tr>
			<th><spring:message code='label.thesis.id'/></th>
			<th><spring:message code='label.title'/></th>
			<th><spring:message code='label.executionDegree'/></th>
			<th><spring:message code='label.participants'/></th>
			<th><spring:message code='label.number.of.candidacies'/></th>
			<th><spring:message code='label.proposal.status'/></th>
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
						<button class='detailsButton btn btn-default' data-id="<c:out escapeXml="true" value="${thesisProposal.externalId}"/>">
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
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.isForFirstCycle'/></label>
					<div class="col-sm-10">
						<div class="information forFirstCycle">
							<span id="forFirstCycleYes"><spring:message code='label.isForFirstCycle.yes'/></span>
							<span id="forFirstCycleNo"><spring:message code='label.isForFirstCycle.no'/></span>
						</div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.goals'/></label>
					<div class="col-sm-10">
						<div id="goals" class="information goals"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<div id="requirements" class="information requirements"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<div id="localization" class="information localization"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.isCapstone'/></label>
					<div class="col-sm-10">
						<div class="information capstone">
							<span id="capstoneYes"><spring:message code="label.yes"/></span>
							<span id="capstoneNo"><spring:message code="label.no"/></span>
						</div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.numberStudents'/></label>
					<div class="col-sm-10">
						<div class="information numberStudents">
							Min: <span id="minStudents"> </span>
							Max: <span id="mxStudents"> </span>
						</div>
					</div>
				</div>

				<div id="externalInstitutionBlock" class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.externalInstitution'/></label>
					<div class="col-sm-10">
						<div id="externalInstitution" class="information externalInstitution"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.observations'/></label>
					<div class="col-sm-10">
						<div id="observations" class="information observations"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.executionDegrees'/></label>
					<div class="col-sm-10">
						<div class="information degrees">
							<ul id="degrees">
							</ul>
						</div>
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
		var tid = e.data('id');

		<% for (final ThesisProposal thesisProposal : (List<ThesisProposal>) request.getAttribute("proposals")) { %>
			if (tid == '<%= thesisProposal.getExternalId() %>') {
				<% if (thesisProposal.getIsForFirstCycle()) { %>
				$("#forFirstCycleYes").show();
				$("#forFirstCycleNo").hide();
				<% } else { %>
				$("#forFirstCycleNo").show();
				$("#forFirstCycleYes").hide();
				<% } %>
				$("#goals").html('<%= thesisProposal.getGoals().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "<br/>") %>');
				$("#requirements").html('<%= thesisProposal.getRequirements().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "<br/>") %>');
				$("#localization").html('<%= thesisProposal.getLocalization().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "<br/>") %>');
				<% if (thesisProposal.getExternalColaboration()) { %>
					$("#externalInstitution").html('<%= thesisProposal.getExternalInstitution().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "<br/>") %>');
					$("#externalInstitutionBlock").show();
				<% } else { %>
					$("#externalInstitutionBlock").hide();
				<% } %>
				$("#observations").html('<%= thesisProposal.getObservations().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "<br/>") %>');
				$("#degrees").empty();
				<% for (final ExecutionDegree executionDegree : thesisProposal.getExecutionDegreeSet()) { %>
					$("#degrees").append('<li><%= executionDegree.getDegreeCurricularPlan().getDegree().getSigla() %></li>');
				<% } %>
				<% if (thesisProposal.getIsCapstone()) { %>
					$("#capstoneYes").show();
					$("#capstoneNo").hide();
				<% } else { %>
					$("#capstoneNo").show();
					$("#capstoneYes").hide();
				<% } %>
				$("#minStudents").html('<%= thesisProposal.getMinStudents() %>');
				$("#mxStudents").html('<%= thesisProposal.getMaxStudents() %>');
			}
		<% } %>

		$('#view').modal('show');
	});
})
</script>
