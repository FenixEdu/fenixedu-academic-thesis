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
									<th>
										<spring:message code='label.thesis.id' />
									</th>
									<th>
										<spring:message code='label.title' />
									</th>
									<th>
										<spring:message code='label.participants' />
									</th>
									<th>
										<spring:message code='label.student.candidacy.accepted'/>
									</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
								<c:set var="anyAccepted" scope="session" value="false"/>
								<c:forEach items="${node.value}" var="candidacy">
									<tr class="studentThesisCandidacyRow  ${(candidacy.acceptedByAdvisor && !anyAccepted) ? 'thesis-selected' : '' } sortableRow" data-studentThesisCandidacy-id="${candidacy.externalId}" data-preference="${candidacy.preferenceNumber}">

										<c:if test="${!(candidacy.acceptedByAdvisor && !anyAccepted)}" >
											<td>${candidacy.thesisProposal.identifier}</td>
										</c:if>
										<c:if test="${candidacy.acceptedByAdvisor && !anyAccepted}" >
											<c:set var="anyAccepted" scope="session" value="true"/>
											<td>${candidacy.thesisProposal.identifier}<span class="badge">	<spring:message code='label.proposal.attributed'/></span></td>
										</c:if>
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
						<td>
							<c:if test="${candidacy.acceptedByAdvisor}">
								<spring:message code='label.yes'/>
							</c:if>
							<c:if test="${!candidacy.acceptedByAdvisor}">
								<spring:message code='label.no'/>
							</c:if>
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

									<input type='button' class='detailsButton btn btn-default' data-observations='<c:out value="${candidacy.thesisProposal.observations}"/>'
																																						 data-requirements='<c:out value="${candidacy.thesisProposal.requirements}"/>'
																																						 data-goals='<c:out value="${candidacy.thesisProposal.goals}"/>'
																																						 data-localization='<c:out value="${candidacy.thesisProposal.localization}"/>' 
																																						 data-degrees="${result}" value='<spring:message code="button.details"/>'
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
</style>

<script type="text/javascript">
jQuery(document).ready(function(){
	jQuery('.detailsButton').on('click', function(event) {
		$("#details" + $(this).data("thesis")).toggle('show');
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

</div>

<div role="tabpanel" class="tab-pane" id="profile">

	<div class="well">
		<p>
			<spring:message code="label.candidacies.proposals.well" />
		</p>
	</div>

	<c:if test="${!(proposalsSize > 0)}">
		<div class="alert alert-warning" role="alert">
			<p>
				<spring:message code='label.student.proposals.empty'/>
			</p>
		</p>
	</div>
</c:if>
	<c:if test="${proposalsSize > 0}">
	<div class="table-responsive">
		<table class="table" id="candidaciesTable">
			<thead>
				<tr>
					<th>
						<spring:message code='label.thesis.id' />
					</th>
					<th>
						<spring:message code='label.title' />
					</th>
					<th>
						<spring:message code='label.participants' />
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${proposalsByReg}" var="node">

				<c:forEach items="${node.value}" var="proposal">

				<tr>
					<td>${proposal.identifier}</td>
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
							<input type='button' class='detailsButton btn btn-default' data-observations='<c:out value="${proposal.observations}"/>' data-requirements='<c:out value="${proposal.requirements}"/>' data-goals='<c:out value="${proposal.goals}"/>' data-localization='<c:out value="${proposal.localization}"/>' data-degrees="${result}" value='<spring:message code="button.details"/>' data-thesis="${proposal.externalId}">
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
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.observations'/></label>
					<div class="col-sm-10">
						<div class="information observations"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.requirements'/></label>
					<div class="col-sm-10">
						<div class="information requirements"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.goals'/></label>
					<div class="col-sm-10">
						<div class="information goals"></div>
					</div>
				</div>

				<div class="form-group">
					<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.localization'/></label>
					<div class="col-sm-10">
						<div class="information localization"></div>
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
