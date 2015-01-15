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



<c:if test="${!empty outOfCandidacyPeriodException}">
<p class="text-danger"><spring:message code="error.thesisProposal.candidacy.confirm.outOfCandidacyPeriodException"/></p>
</c:if>

<c:if test="${!empty maxNumberStudentThesisCandidaciesException}">
<p class="text-danger"><spring:message code="error.thesisProposal.candidacy.create.maxNumberStudentThesisCandidacies"/></p>
</c:if>

<c:if test="${!empty nullPointerException}">
<p class="text-danger"><spring:message code="error.thesisProposal.candidacy.nullPointerException"/></p>
</c:if>

<c:if test="${!empty deleteException}">
<p class="text-danger"><spring:message code="error.thesisProposal.candidacy.remove.accepted"/></p>
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

			<form:form role="form" method="POST" action="${pageContext.request.contextPath}/studentCandidacies/updatePreferences" class="form-horizontal">
			<input type="hidden" name="json" id="json" />
			<button type="submit" class="btn btn-default" id="savePreferencesButton" style="display:none;"><spring:message code="button.preferences.save"/></button>
		</form:form>
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
					<c:forEach items="${studentThesisCandidacies}" var="studentThesisCandidacy">
					<tr class="studentThesisCandidacyRow sortableRow" data-studentThesisCandidacy-id="${studentThesisCandidacy.externalId}">
						<td>${studentThesisCandidacy.thesisProposal.identifier}</td>
						<td>${studentThesisCandidacy.thesisProposal.title}</td>
						<td>
							<c:forEach items="${studentThesisCandidacy.thesisProposal.getSortedParticipants()}" var="participant">
							<div>${participant.user.name} <small>as</small> <b>${participant.thesisProposalParticipantType.name.content}</b>
							</div>
						</c:forEach>
					</td>
					<td>
						<form:form method="GET" action="${pageContext.request.contextPath}/studentCandidacies/delete/${studentThesisCandidacy.externalId}">
						<div class="btn-group btn-group-xs">

							<button type="submit" class="btn btn-default" id="removeCandidacyButton"><spring:message code="button.proposal.unapply"/></button>

							<c:set var="result" scope="session" value='' />
							<c:forEach items="${studentThesisCandidacy.thesisProposal.executionDegreeSet}" var="executionDegree" varStatus="i">
							<c:set var="result" scope="session" value="${result}${executionDegree.degree.sigla}" />
							<c:if test="${i.index != studentThesisCandidacy.thesisProposal.executionDegreeSet.size() - 1}">
							<c:set var="result" scope="session" value="${result}, " />
						</c:if>
					</c:forEach>

					<input type='button' class='detailsButton btn btn-default' data-observations="${studentThesisCandidacy.thesisProposal.observations}" data-requirements="${studentThesisCandidacy.thesisProposal.requirements}" data-goals="${studentThesisCandidacy.thesisProposal.goals}" data-localization="${studentThesisCandidacy.thesisProposal.localization}" data-degrees="${result}" value='<spring:message code="button.details"/>' data-thesis="${studentThesisCandidacy.thesisProposal.externalId}">
				</div>
			</form:form>
		</td>
	</tr>
</c:forEach>
</tbody>
</table>
</div>

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
			$("#view ." + x).html(e.data(x));
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
				<c:forEach items="${proposals}" var="node">

				<c:forEach items="${node.value}" var="proposal">

				<tr>
					<td>${proposal.identifier}</td>
					<td>${proposal.title}</td>
					<td>
						<c:forEach items="${proposal.getSortedParticipants()}" var="participant">
						<div>${participant.user.name} <small>as</small> <b>${participant.thesisProposalParticipantType.name.content}</b>
						</div>
					</c:forEach>
				</td>
				<td>
					<form:form method="POST" action="${pageContext.request.contextPath}/studentCandidacies/candidate/${proposal.externalId}">
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
				<input type='button' class='detailsButton btn btn-default' data-observations="${proposal.observations}" data-requirements="${proposal.requirements}" data-goals="${proposal.goals}" data-localization="${proposal.localization}" data-degrees="${result}" value='<spring:message code="button.details"/>' data-thesis="${proposal.externalId}">
			</div>
		</form:form>
	</td>
</tr>
</c:forEach>
</c:forEach>
</tbody>
</table>
</div>
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
$(document).ready(function() {
	$("#candidaciesTable").tableDnD({
		onDrop:function(){
			$("#savePreferencesButton").show();
		}
	});
});
</script>

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
			"preference" : index
		});
	}

	$("#json").val(JSON.stringify(studentThesisCandidaciesJSON.studentCandidacies));
});
</script>
