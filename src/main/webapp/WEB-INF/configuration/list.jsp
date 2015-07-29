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

<div role="tabpanel">
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab"><spring:message code="label.configuration.rules"/></a></li>
		<c:if test="${!empty isManager && isManager == true}">
		<li role="presentation"><a href="#profile" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="label.configuration.participant.types"/></a></li>
	</c:if>
</ul>

<div class="tab-content">
	<div role="tabpanel" class="tab-pane active" id="home">

		<div class="page-header">
			<h1><spring:message code="title.configuration.management"/></h1>
		</div>

		<div class="well">
			<p><spring:message code="label.configuration.well"/></p>
		</div>

		<c:if test="${!empty deleteException}">
		<p class="text-danger"><spring:message code="error.thesisProposal.configuration.delete.used"/></p>
	</c:if>

	<p>
		<div class="row">
			<div class="col-sm-8">
				<button type="submit" class="btn btn-primary" data-toggle="modal" data-target="#create"><spring:message code="button.create"/></button>
			</div>
			<div class="col-sm-4">
				<c:if test="${!empty executionYearsList}">
				<select id="executionYearSelect" class="form-control">
					<option value="NONE"> <spring:message code='label.executionYear.select'/> </option>
					<c:forEach items="${executionYearsList}" var="executionYear">
					<option value="${executionYear.year}">${executionYear.year}</option>
				</c:forEach>
			</select>
		</c:if>
	</div>
</div>
</p>

<c:if test="${empty configurationsList}">
	<div id='emptyConfigs'>
	<spring:message code='label.configuration.empty'/>
	</div>
</c:if>
<c:if test="${!empty configurationsList}">
	<div hidden id='emptyConfigs'>
		<spring:message code='label.configuration.empty'/>
	</div>

<div class="table-responsive">
	<table class="table">
		<colgroup>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
			<col></col>
		</colgroup>
		<thead>
			<tr>
				<tr class="tr-head">
					<th><spring:message code='label.executionDegree'/></th>
					<th><spring:message code='label.proposalPeriod.start'/></th>
					<th><spring:message code='label.proposalPeriod.end'/></th>
					<th><spring:message code='label.candidacyPeriod.start'/></th>
					<th><spring:message code='label.candidacyPeriod.end'/></th>
					<th><spring:message code='label.maxThesisCandidaciesByStudent'/></th>
					<th><spring:message code='label.maxThesisProposalsByUser'/></th>
					<th><spring:message code='label.min.ects.first.cyle'/></th>
					<th><spring:message code='label.min.ects.second.cyle'/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${configurationsList}" var="configuration">
				<tr data-execution-year="${configuration.executionDegree.executionYear.name}" data-configuration-id="${configuration.externalId}">
					<td>${configuration.executionDegree.degree.presentationName} - ${configuration.executionDegree.executionYear.year}</td>
					<td>${configuration.proposalPeriod.start.toString('dd-MM-YYY HH:mm')}</td>
					<td>${configuration.proposalPeriod.end.toString('dd-MM-YYY HH:mm')}</td>
					<td>${configuration.candidacyPeriod.start.toString('dd-MM-YYY HH:mm')}</td>
					<td>${configuration.candidacyPeriod.end.toString('dd-MM-YYY HH:mm')}</td>
					<td>
						<c:if test="${configuration.maxThesisCandidaciesByStudent != -1}">
						${configuration.maxThesisCandidaciesByStudent}
					</c:if>
					<c:if test="${configuration.maxThesisCandidaciesByStudent == -1}">
					<spring:message code='label.configuration.unlimited'/>
				</c:if>
						</td>
						<td>
							<c:if test="${configuration.maxThesisProposalsByUser != -1}">
							${configuration.maxThesisProposalsByUser}
						</c:if>
						<c:if test="${configuration.maxThesisProposalsByUser == -1}">
						<spring:message code='label.configuration.unlimited'/>
					</c:if>
				</td>
				<td>${configuration.minECTS1stCycle}</td>
				<td>${configuration.minECTS2ndCycle}</td>
				<td class=""><form:form method="GET" action="${pageContext.request.contextPath}/configuration/edit/${configuration.externalId}">
						<button type="submit" class="btn btn-xs btn-default" id="editButton"><spring:message code="button.edit"/></button>
					</form:form></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:if>


<script type="text/javascript">
$("#executionYearSelect").change(function() {
	var year = $("#executionYearSelect").val();

	$('tr').hide();
	$('#emptyConfigs').hide()

	if(year == "NONE") {
		$("tr").show();
	}
	else {
		var result = $("tr[data-execution-year= '" + year.toString() + "']");
		if(result.length > 0) {
			$(result).show();
			$(".tr-head").show();
		}
		else {
			$('#emptyConfigs').show()
		}
	}
});

$(".deleteConfiguration").on("click", function() {

	var externalId = $(this).closest("tr").data("configuration-id");

	$.post("delete/" + externalId , function(data,status) {});
});
</script>


<!-- Modal -->
<div class="modal fade" id="create" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/create" class="form-horizontal" id="thesisProposalCreateForm">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close"><span class="sr-only"><spring:message code="button.close"/></span></button>
					<h3 class="modal-title"><spring:message code="label.create"/></h3>
					<small class="explanation"><spring:message code="label.configuration.modal.create.explanation"/></small>
				</div>
				<div class="modal-body">
					<p class="text-danger" id="emptyDate" hidden><spring:message code="error.thesisProposal.configuration.date.empty"/></p>
					<div class="form-group">
						<label for="proposalPeriodEnd" class="col-sm-4 control-label"><spring:message code='label.proposalPeriod'/></label>
						<div class="col-sm-4">
							<label for="proposalPeriodStart"  class="control-label"><spring:message code='label.start'/></label>
							<input type="text" bennu-datetime no-seconds name="proposalPeriodStart" class="form-control" id="proposalPeriodStart" required="required" value='${thesisProposalsConfigurationBean.proposalPeriodStart}'/>
						</div>
						<div class="col-sm-4">
							<label for="proposalPeriodEnd" path="proposalPeriodEnd" class="control-label"><spring:message code='label.end'/></label>
							<input type="text" bennu-datetime no-seconds name="proposalPeriodEnd" class="form-control" id="proposalPeriodEnd" required="required" value='${thesisProposalsConfigurationBean.proposalPeriodEnd}'/>
						</div>
					</div>

					<div class="form-group">
						<label for="candidacyPeriodStart" class="col-sm-4 control-label"><spring:message code='label.candidacies'/></label>
						<div class="col-sm-4">
							<label for="candidacyPeriodStart"  class="control-label"><spring:message code='label.start'/></label>
							<input type="text" bennu-datetime no-seconds name="candidacyPeriodStart" class="form-control" id="candidacyPeriodStart"  required="required" value=''/>
						</div>
						<div class="col-sm-4">
							<label for="candidacyPeriodEnd" path="candidacyPeriodEnd" class="control-label"><spring:message code='label.end'/></label>
							<input type="text" bennu-datetime no-seconds name="candidacyPeriodEnd" class="form-control" id="candidacyPeriodEnd" required="required" value=''/>
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-4 control-label"><spring:message code='label.executionDegree'/></label>
						<div class="col-sm-8">
							<select id="executionYearSelect2" class="form-control">
								<option value="NONE" label="<spring:message code='label.executionYear.select'/>"> <spring:message code='label.executionYear.select'/> </option>
								<c:forEach items="${executionYearsList}" var="executionYear">
								<option value="${executionYear.externalId}" label="${executionYear.year}">${executionYear.year}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="form-group">
					<label for="executionDegree"class="col-sm-4 control-label"></label>
					<div class="col-sm-8">
						<select disabled="disabled" name="executionDegree" class="form-control" id="executionDegreeSelect">
							<option value="NONE" label="<spring:message code='label.executionDegree.select'/>" id="executionDegreeDefaultOption"> <spring:message code='label.executionDegree.select'/> </option>
						</select>
					</div>
				</div>

				<div class="form-group">
					<label for="maxThesisCandidaciesByStudent" class="col-sm-4 control-label"><spring:message code='label.maxThesisCandidaciesByStudent.create'/></label>
					<div class="col-sm-8">
						<input type="number" min="-1" class="form-control" id="maxThesisCandidaciesByStudent" name="maxThesisCandidaciesByStudent" placeholder="<spring:message code='label.maxThesisCandidaciesByStudent'/>" required="required" value="-1"/>
						<div class="help-block"><spring:message code='label.maxThesisCandidaciesByStudent.create.help-message'/></div>
					</div>
				</div>

				<div class="form-group">
					<label for="maxThesisProposalsByUser" class="col-sm-4 control-label"><spring:message code='label.maxThesisProposalsByUser.create'/></label>
					<div class="col-sm-8">
						<input type="number" min="-1" class="form-control" id="maxThesisProposalsByUser" name="maxThesisProposalsByUser" placeholder="<spring:message code='label.maxThesisProposalsByUser'/>" required="required" value="-1"/>
						<div class="help-block"><spring:message code='label.maxThesisProposalsByUser.create.help-message'/></div>
					</div>
				</div>

				<div class="form-group">
					<label for="minECTS1stCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.first.cyle'/></label>
					<div class="col-sm-8">
						<input type="number" min="0" class="form-control" id="minECTS1stCycle" name="minECTS1stCycle" required="required" value="0"/>
					</div>
				</div>

				<div class="form-group">
					<label for="minECTS2ndCycle" class="col-sm-4 control-label"><spring:message code='label.min.ects.second.cyle'/></label>
					<div class="col-sm-8">
						<input type="number" min="0" class="form-control" id="minECTS2ndCycle" name="minECTS2ndCycle" required="required" value="0"/>
					</div>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.close"/></button>
				<button type="submit" class="btn btn-primary" id="submitButton" disabled=true><spring:message code="button.create"/></button>
			</div>
		</div>
	</div>
</form>
</div>

<script type="text/javascript">
$("#executionYearSelect2").change(function() {

	$("#executionDegreeSelect").empty()
	$("#executionDegreeSelect").append($("<option></option>").attr("value", "NONE").attr("label", "<spring:message code='label.executionDegree.select'/>").attr("id", "executionDegreeDefaultOption"));

	var year = $("#executionYearSelect2").val();

	if(year === "NONE") {
		$('#executionDegreeSelect').attr("disabled","disabled")
	}
	else {
		$.get("${pageContext.request.contextPath}/configuration/execution-year/" + year + "/execution-degrees", function(response) {

			$('#executionDegreeSelect').removeAttr("disabled");

			response.forEach(function(elem) {
				$("#executionDegreeSelect").append($("<option>" + elem.name + "</option>").attr("value", elem.externalId).attr("label", elem.name));
			});
		});
	}
})

$("#submitButton").on("click", function(){
	var dates = $(".bennu-datetime-input");

	$("#emptyDate").attr("hidden")

	for (var i = dates.length - 1; i >= 0; i--) {
		if(dates[i].value.length < 1) {
			$("#emptyDate").removeAttr("hidden", "hidden");
			break;
		}
	};
})


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
</div>


<c:if test="${!empty isManager && isManager == true}">
<div role="tabpanel" class="tab-pane" id="profile">

<script src="${pageContext.request.contextPath}/js/jquery.tablednd.js" type="text/javascript"></script>

<style type="text/css">
.tDnD_whileDrag{
	background:#f3f3f3;
}
</style>

<div class="page-header">
	<h1><spring:message code="title.thesisProposalsParticipantsType.management"/></h1>
</div>

<c:if test="${!empty deleteException}">
<p class="text-danger"><spring:message code="error.thesisProposal.participantType.delete.used"/></p>
</c:if>
<c:if test="${!empty editException}">
<p class="text-danger"><spring:message code="error.thesisProposal.participantType.edit.used"/></p>
</c:if>

<div class="well">
	<p>
		<spring:message code="label.dragAndDrop.hint"/>
		<spring:message code="label.dragAndDrop.increasing.hint"/>
	</p>
</div>

<p>
	<div class="row">
		<div class="col-sm-8">
			<form:form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/updateWeights" class="form-horizontal">
				${csrf.field()}
				<button type="button"  class="btn btn-primary" data-toggle="modal" data-target="#createParticipantType"><spring:message code="button.create"/></button>
				<input type="hidden" name="json" id="json"/>
				<button type="submit" class="btn btn-default" style="display:none;" id="saveButton"><spring:message code="button.order.save"/> </button>
			</form:form>
	</div>
	<div class="col-sm-4">
	</div>
</div>
</p>

<div class="table-responsive">
	<table class="table" id="table">
		<colgroup>
			<col></col>
			<col></col>
		</colgroup>
		<thead>
			<tr>
				<th><spring:message code='label.participantType.name'/></th>
				<th><spring:message code='label.participantType.inactive'/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${participantTypeList}" var="participantType">
			<div class="sortable">
				<tr data-participantType-id="${participantType.externalId}" class="sortableRow">
					<td>${participantType.name.content}</td>
					<c:if test="${participantType.inactive}">
						<td>
							<spring:message code='label.participantType.inactive.true'/>
						</td>
						<td>
								<a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/configuration/activateParticipantType/${participantType.externalId}">
									<spring:message code='label.participantType.inactive.activate'/>
								</a>
								<a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/configuration/editParticipantType/${participantType.externalId}">
									<spring:message code='button.edit'/>
								</a>
						</td>
					</c:if>
					<c:if test="${!participantType.inactive}">
						<td>
							<spring:message code='label.participantType.inactive.false'/>
						</td>
						<td>
							<a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/configuration/inactivateParticipantType/${participantType.externalId}">
								<spring:message code='label.participantType.inactive.inactivate'/>
							</a>
							<a class="btn btn-xs btn-default" href="${pageContext.request.contextPath}/configuration/editParticipantType/${participantType.externalId}">
								<spring:message code='button.edit'/>
							</a>
						</td>
					</c:if>
			</tr>
		</div>
	</c:forEach>
</tbody>
</table>
</div>

<!-- Modal -->
<div class="modal fade" id="createParticipantType" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/createParticipantType" class="form-horizontal" commandname="participantTypeBean" id="participantTypeBean">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close"><span class="sr-only"><spring:message code="button.close"/></span></button>
					<h3 class="modal-title"><spring:message code="label.create"/></h3>
					<small class="explanation"><spring:message code="label.participantType.modal.create.explanation"/></small>
				</div>
				<div class="modal-body">
					<spring:message code='label.participantType.name' var="participantTypeName"/>
					<div class="form-group">
						<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.participantType.name'/></label>
						<div class="col-sm-10">
							<input  type="text" class="form-control" name="name" id="local" path="local" required="required" bennu-localized-string/>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-10">
							<input type="hidden" class="form-control" name="weight" id="Weight" placeholder="Weight" path="weight" required="required" value="${participantTypeList.size() + 1}"/>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-primary"><spring:message code="button.create"/></button>
				</div>
			</div>
		</div>
	</form>
</div>

<script type="text/javascript">
$(document).ready(function() {
	$("#table").tableDnD({
		onDrop:function(){
			$("#saveButton").show();
		}
	});
});
</script>

<script type="text/javascript">
$("#saveButton").on("click", function(e) {
	var rows = $("#table").find("tbody").find("tr")
	participantTypesJSON = {
		participantTypes: []
	}
	for (index=0; index < rows.length; index++) {
		var row = rows.eq(index)
		var externalId = row.data("participanttype-id")
		participantTypesJSON.participantTypes.push({
			"externalId" : externalId,
			"weight" : (rows.length - index)
		});
	}
	$("#json").val(JSON.stringify(participantTypesJSON.participantTypes));
});
</script>

<style type="text/css">
.sortableRow td:hover{
	cursor: pointer;
}
</style>

</div>
</c:if>
</div>

<style>
.modal-backdrop{z-index: 900}.modal{z-index: 1000;}
</style>
