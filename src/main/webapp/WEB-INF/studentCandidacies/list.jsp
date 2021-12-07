<%@ page import="org.fenixedu.academic.thesis.domain.ThesisProposal" %>
<%@ page import="java.util.List" %>
<%@ page import="org.fenixedu.academic.domain.ExecutionDegree" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration" %>
<%@ page import="org.fenixedu.academic.thesis.domain.StudentThesisCandidacy" %><%--

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

<spring:url var="datatablesUrl" value="/js/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/js/dataTables/media/js/jquery.dataTables.bootstrap.min.js"/>
<spring:url var="datatablesCssUrl" value="/css/dataTables/dataTables.bootstrap.min.css"/>

<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<link rel="stylesheet" href="${datatablesCssUrl}">


<style>
	tfoot {
		display: table-header-group;
	}
	.dataTables_filter input {
		width: 400px !important;
	}

	.modal pre {
		white-space: pre-wrap;
	}
</style>

<script type="text/javascript">
	$(document).ready(function() {
		var oTable = $('.existingCandidaciesTable').DataTable( {
			"pageLength": 50,
			"paging": true,
			"order": [[ 0, "asc" ]],
			"columnDefs": [
					{ "orderable": false, "targets": [6] },
			],
			initComplete: function () {
				this.api().columns([1,4,5]).every( function () {
					var column = this;
					var select = $('<select><option value="">----</option></select>')
							.appendTo( $(column.footer()).empty() )
							.on( 'change', function () {
								var val = $.fn.dataTable.util.escapeRegex(
										$(this).val()
								);
								column
										.search( val ? '^'+val+'$' : '', true, false )
										.draw();
							} );

					column.data().unique().sort().each( function ( d, j ) {
						var val = $('<div/>').html(d).text();
						if(column.search() === '^'+val+'$'){
							select.append( '<option value="'+val+'" selected="selected">'+val+'</option>' )
						} else {
							select.append( '<option value="'+val+'">'+val+'</option>' )
						}
					} );
				} );
				$('.dataTables_filter input').unbind();
				$('.dataTables_filter input').bind('keyup', function(e) {
					if(e.keyCode == 13) {
						oTable.search( this.value ).draw();
					}
				});
				$('.dataTables_filter input')
						.attr('data-toggle', 'tooltip')
						.attr('data-placement', 'top')
						.attr('title', 'Press Enter to update the table')
						.tooltip();

			}
		} );
		// $('.dataTables_filter').remove();
	});
</script>


<script src="${pageContext.request.contextPath}/js/jquery.tablednd.js" type="text/javascript"></script>

<style type="text/css">
.tDnD_whileDrag{
	background:#f3f3f3;
}

.sortableRow td:hover{
	cursor: pointer;
}
</style>

<div class="page-header">
	<h1><spring:message code="title.studentThesisCandidacy.management"/></h1>
</div>

<c:if test="${! empty error}">
	<p class="text-danger"><spring:message code="error.thesisProposal.${error}"/></p>
</c:if>

<c:if test="${! empty deleteException}">
	<p class="text-danger"><spring:message code="error.thesisProposal.OutOfCandidacyPeriodException"/></p>
</c:if>

<c:if test="${! empty domainException}">
	<p class="text-danger">${domainException}</p>
</c:if>

<c:if test="${!empty suggestedConfigs}">
<div class="alert alert-info">
		<c:forEach items="${suggestedConfigs}" var="config">
	<p>
	<spring:message code="label.thesis.candidacy.info" arguments="${config.executionDegree.degree.sigla},${config.candidacyPeriod.start.toString('dd-MM-YYY HH:mm')},${config.candidacyPeriod.end.toString('dd-MM-YYY HH:mm')}"/>
	</p>
	</c:forEach>
</div>
</c:if>

<div role="tabpanel">
	<!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab"><spring:message code="label.student.candidacies.manage"/></a></li>
		<li role="presentation"><a href="#profile" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="label.student.candidacies.proposals"/></a></li>
	</ul>

	<!-- Tab panes -->
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane active" id="home">

			<div class="well">
				<p>
					<spring:message code="label.candidacies.student.well"/>
				</p>
				<p>
					<spring:message code="label.dragAndDrop.hint" />
					<spring:message code="label.dragAndDrop.increasing.hint" />
				</p>
			</div>

			<c:if test="${candidaciesSize <= 0}">
					<div class="alert alert-warning" role="alert">
						<p>
							<spring:message code='label.student.candidacies.empty'/>
						</p>
					</p>
				</div>
			</c:if>
			<c:if test="${candidaciesSize > 0}">
			<div class="alert alert-warning">
				<p>
					<spring:message code="label.thesis.candidacy.temporary.info"/>
				</p>
			</div>
				<form:form role="form" method="POST" action="${pageContext.request.contextPath}/studentCandidacies/updatePreferences" class="form-horizontal">
					${csrf.field()}
					<input type="hidden" name="json" id="json" />
					<button type="submit" class="btn btn-default" id="savePreferencesButton" style="display:none;"><spring:message code="button.preferences.save"/></button>
				</form:form>

				<c:forEach items="${candidaciesByConfig}" var="node">
					<c:if test="${!empty node.value}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">${node.key.presentationName}</h3>
							</div>
							<div class="panel-body">
					<div class="table-responsive">
						<table class="table" id="${(node.key.candidacyPeriod.containsNow()) ? 'candidaciesTable' : ''}">
							<thead>
								<tr>
									<th><spring:message code='label.thesis.id' /></th>
									<th><spring:message code='label.type' /></th>
									<th><spring:message code='label.title' /></th>
									<th><spring:message code='label.participants' /></th>
									<th><spring:message code='label.thesisProposal.applicationCount' /></th>
									<th><spring:message code='label.student.candidacy.accepted'/></th>
									<th><spring:message code='label.thesisProposal.status' /></th>
									<th></th>
								</tr>
							</thead>
							<tbody>
								<c:set var="anyAccepted" scope="session" value="false"/>
								<c:set var="anyAcceptedProposal" scope="session" value="false"/>
								<c:forEach items="${node.value}" var="candidacy">
									<tr class="studentThesisCandidacyRow  ${(candidacy.acceptedByAdvisor && !anyAccepted) ? 'thesis-selected' : '' } sortableRow" data-studentThesisCandidacy-id="${candidacy.externalId}" data-preference="${candidacy.preferenceNumber}">

										<c:if test="${!(candidacy.acceptedByAdvisor && !anyAccepted)}" >
											<td>${candidacy.thesisProposal.identifier}</td>
										</c:if>
										<c:if test="${candidacy.acceptedByAdvisor && !anyAccepted}" >
											<c:set var="anyAccepted" scope="session" value="true"/>
											<td>${candidacy.thesisProposal.identifier}<span class="badge">	<spring:message code='label.proposal.attributed'/></span></td>
										</c:if>
										<td>
											<c:if test="${candidacy.thesisProposal.isForFirstCycle}">
												<span id="forFirstCycleYes"><spring:message code='label.isForFirstCycle.yes'/></span>
											</c:if>
											<c:if test="${!candidacy.thesisProposal.isForFirstCycle}">
												<span id="forFirstCycleNo"><spring:message code='label.isForFirstCycle.no'/></span>
											</c:if>
										</td>
							<td>${candidacy.thesisProposal.title}</td>
							<td>
								<c:forEach items="${candidacy.thesisProposal.getSortedParticipants()}" var="participant">
									<div>${participant.name} (${participant.participationPercentage}%)
										<c:if test="${! empty participantLabelService}">
											<small>-</small> <b>${participantLabelService.getInstitutionRole(participant)}</b>
										</c:if>
									</div>
							</c:forEach>
						</td>
						<td>${applicationCountByProposalConfig[candidacy.thesisProposal]}</td>
						<td>
							<c:if test="${candidacy.acceptedByAdvisor}">
								<spring:message code='label.yes'/>
							</c:if>
							<c:if test="${!candidacy.acceptedByAdvisor}">
								<spring:message code='label.no'/>
							</c:if>
						</td>
						<td>
							<c:choose>
								<c:when test="${candidacy.acceptedByAdvisor && !anyAcceptedProposal}">
									<c:set var="anyAcceptedProposal" scope="session" value="true"/>
									<span class="label label-warning"><spring:message code='label.thesisProposal.assigned' /></span>
								</c:when>
								<c:otherwise>
									<span class="label label-success"><spring:message code='label.thesisProposal.unassigned' /></span>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<form:form method="GET" action="${pageContext.request.contextPath}/studentCandidacies/delete/${candidacy.externalId}">
								<div class="btn-group btn-group-xs">

									<button type="submit" class="btn btn-default" id="removeCandidacyButton"><spring:message code="button.proposal.unapply"/></button>

									<c:set var="result" scope="session" value='' />
									<c:forEach items="${candidacy.thesisProposal.executionDegreeSet}" var="executionDegree" varStatus="i">
										<c:set var="result" scope="session" value="${result}${executionDegree.degree.sigla}" />
										<c:if test="${i.index != candidacy.thesisProposal.executionDegreeSet.size() - 1}">
											<c:set var="result" scope="session" value="${result}, " />
										</c:if>
									</c:forEach>

									<input type='button' class='detailsButton btn btn-default'
										   value='<spring:message code="button.details"/>'
										   data-thesis="${candidacy.thesisProposal.externalId}">
								</div>
							</form:form>
						</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
	</c:if>
				</c:forEach>


<style media="screen">
	.thesis-selected{
		font-weight:bold !important;
	}
</style>

<script type="text/javascript">
$(document).ready(function() {
	$("#candidaciesTable").tableDnD({
		onDrop:function(){
			$("#savePreferencesButton").show();
		}
	});
});
</script>

</c:if>

<style type="text/css">
.information{
	margin-top: 7px;
	margin-left: 10px;
}

.existingCandidaciesTable tr > td:nth-child(5){
	text-align: center;
}
</style>

<script type="text/javascript">
function escapeHtml(unsafe) {
	return unsafe
			.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;")
			.replace(/"/g, "&quot;")
			.replace(/'/g, "&#039;");
}

$(function(){
	$(".table tbody").on("click", ".detailsButton", function(evt){
		var e = $(evt.target);
		var tid = e.data('thesis');

		<% for (final Set<ThesisProposal> thesisProposals : ((Map<Registration, Set<ThesisProposal>>) request.getAttribute("proposalsByReg")).values()) { %>
		<% for (final ThesisProposal thesisProposal : thesisProposals) { %>
		if (tid == '<%= thesisProposal.getExternalId() %>') {
			<% if (thesisProposal.getIsForFirstCycle()) { %>
			$("#forFirstCycleYes").show();
			$("#forFirstCycleNo").hide();
			<% } else { %>
			$("#forFirstCycleNo").show();
			$("#forFirstCycleYes").hide();
			<% } %>
			$("#goals").html(escapeHtml('<%= thesisProposal.getGoals().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#requirements").html(escapeHtml('<%= thesisProposal.getRequirements().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#localization").html(escapeHtml('<%= thesisProposal.getLocalization().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			<% if (thesisProposal.getExternalColaboration()) { %>
			$("#externalInstitution").html(escapeHtml('<%= thesisProposal.getExternalInstitution().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#externalInstitutionBlock").show();
			<% } else { %>
			$("#externalInstitutionBlock").hide();
			<% } %>
			$("#observations").html(escapeHtml('<%= thesisProposal.getObservations().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
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
		<% } %>

		<% for (final List<StudentThesisCandidacy> thesisCandidacies : ((Map<ThesisProposalsConfiguration, List<StudentThesisCandidacy>>) request.getAttribute("candidaciesByConfig")).values()) { %>
		<% for (final StudentThesisCandidacy thesisCandidacy : thesisCandidacies) { %>
		<% ThesisProposal thesisProposal = thesisCandidacy.getThesisProposal(); %>
		if (tid == '<%= thesisProposal.getExternalId() %>') {
			<% if (thesisProposal.getIsForFirstCycle()) { %>
			$("#forFirstCycleYes").show();
			$("#forFirstCycleNo").hide();
			<% } else { %>
			$("#forFirstCycleNo").show();
			$("#forFirstCycleYes").hide();
			<% } %>
			$("#goals").html(escapeHtml('<%= thesisProposal.getGoals().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#requirements").html(escapeHtml('<%= thesisProposal.getRequirements().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#localization").html(escapeHtml('<%= thesisProposal.getLocalization().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			<% if (thesisProposal.getExternalColaboration()) { %>
			$("#externalInstitution").html(escapeHtml('<%= thesisProposal.getExternalInstitution().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
			$("#externalInstitutionBlock").show();
			<% } else { %>
			$("#externalInstitutionBlock").hide();
			<% } %>
			$("#observations").html(escapeHtml('<%= thesisProposal.getObservations().replace('\r', ' ').replace('\'', '`').replaceAll("\n", "\\\\n") %>'));
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
		<% } %>

		$('#view').modal('show');
	});
})
</script>

</div>

		<div role="tabpanel" class="tab-pane" id="profile">

			<div class="well">
				<p><spring:message code="label.candidacies.proposals.well" /></p>
			</div>

			<c:if test="${!(proposalsSize > 0)}">
				<div class="alert alert-warning" role="alert">
					<p><spring:message code='label.student.proposals.empty'/></p>
				</div>
			</c:if>
			<c:if test="${proposalsSize > 0}">
			<div class="table-responsive">
				<table id="candidaciesTable" class="table table-bordered table-hover existingCandidaciesTable">
					<thead>
						<tr>
							<th><spring:message code='label.thesis.id' /></th>
							<th><spring:message code='label.type' /></th>
							<th><spring:message code='label.title' /></th>
							<th><spring:message code='label.participants' /></th>
							<th><spring:message code='label.thesisProposal.applicationCount' /></th>
							<th><spring:message code='label.thesisProposal.status' /></th>
							<th></th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<th style="width: 10%"></th>
							<th style="width: 10%"></th>
							<th style="width: 30%"></th>
							<th></th>
							<th style="width: 10%"></th>
							<th style="width: 10%"></th>
							<th></th>
						</tr>
					</tfoot>
					<tbody>
						<c:forEach items="${proposalsByReg}" var="node">
						<c:forEach items="${node.value}" var="proposal">

						<tr>
							<td>${proposal.identifier}</td>
							<td>
								<c:if test="${proposal.isForFirstCycle}">
									<span id="forFirstCycleYes"><spring:message code='label.isForFirstCycle.yes'/></span>
								</c:if>
								<c:if test="${!proposal.isForFirstCycle}">
									<span id="forFirstCycleNo"><spring:message code='label.isForFirstCycle.no'/></span>
								</c:if>
							</td>
							<td>${proposal.title}</td>
							<td>
								<c:forEach items="${proposal.getSortedParticipants()}" var="participant">
									<div>${participant.name} (${participant.participationPercentage}%)
										<c:if test="${! empty participantLabelService}">
											<small>-</small> <b>${participantLabelService.getInstitutionRole(participant)}</b>
										</c:if>
									</div>
							</c:forEach>
						</td>
						<td>${applicationCountByProposalReg[proposal]}</td>
						<td>
							<c:choose>
								<c:when test="${acceptedProposals.contains(proposal)}">
									<span class="label label-warning"><spring:message code='label.thesisProposal.assigned' /></span>
								</c:when>
								<c:otherwise>
									<span class="label label-success"><spring:message code='label.thesisProposal.unassigned' /></span>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<form:form method="POST" action="${pageContext.request.contextPath}/studentCandidacies/candidate/${proposal.externalId}">
								${csrf.field()}
								<div class="btn-group btn-group-xs">
									<button type="submit" class="btn btn-default" id="applyButton"><spring:message code="button.proposal.apply"/></button>

									<input type="hidden" name="registration" value="${node.key.externalId}">

									<c:set var="result" scope="session" value='' />
									<c:forEach items="${proposal.executionDegreeSet}" var="executionDegree" varStatus="i">
										<c:set var="result" scope="session" value="${result}${executionDegree.degree.sigla}" />
										<c:if test="${i.index != proposal.executionDegreeSet.size() - 1}">
											<c:set var="result" scope="session" value="${result}, " />
										</c:if>
									</c:forEach>
									<input type='button' class='detailsButton btn btn-default'
										   value='<spring:message code="button.details"/>'
										   data-thesis="${proposal.externalId}">
								</div>
							</form:form>
						</td>
						</tr>
						</c:forEach>
						</c:forEach>
					</tbody>
				</table>
			</div>
			</c:if>
		</div>


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
						<pre id="goals" class="information goals"></pre>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<pre id="requirements" class="information requirements"></pre>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<pre id="localization" class="information localization"></pre>
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
						<pre id="observations" class="information observations"></pre>
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



<script type="text/javascript">
$("#savePreferencesButton").on("click", function(e) {
	var rows = $("#candidaciesTable").find("tbody").find(".studentThesisCandidacyRow")

	studentThesisCandidaciesJSON = {
		studentCandidacies: []
	}

	for (index=0; index < rows.length; index++) {
		var row = rows.eq(index)
		var externalId = row.data("studentthesiscandidacy-id")

		studentThesisCandidaciesJSON.studentCandidacies.push({
			"externalId" : externalId,
			"preference" : index+1
		});
	}

	$("#json").val(JSON.stringify(studentThesisCandidaciesJSON.studentCandidacies));
});
</script>
