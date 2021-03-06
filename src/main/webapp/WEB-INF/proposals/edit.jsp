<%@ page import="org.fenixedu.academic.thesis.domain.ThesisProposalsSystem" %><%--

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
	<h1>
		<spring:message code="title.thesisProposal.management"/>
		<small><spring:message code="title.thesisProposal.edit"/></small>
	</h1>
</div>

<form:form role="form" method="POST" action="${pageContext.request.contextPath}/${action}" class="form-horizontal" commandname="thesisProposalBean" id="thesisProposalBean">
	${csrf.field()}
	<input type="hidden" name="configuration" value="${configuration.externalId}"/>

<c:if test="${!empty error}">
<p class="text-danger"><spring:message code="error.thesisProposal.${error}"/></p>
</c:if>

<div class="alert alert-warning">
  <p>
    Colocar nome e email dos orientadores externos no campo de Observações.
  </p>
</div>

<spring:message code='label.title' var='title'/>
<spring:message code='label.observations' var='observations'/>
<spring:message code='label.hidden' var='hidden'/>
<spring:message code='label.requirements' var='requirements'/>
<spring:message code='label.goals' var='goals'/>
<spring:message code='label.localization' var='localization'/>
<spring:message code='label.externalColaboration' var='externalColaboration'/>
<spring:message code='label.externalInstitution' var='externalInstitution'/>
<spring:message code='label.isCapstone' var='isCapstone'/>
<spring:message code='label.isForFirstCycle' var='isForFirstCycle'/>
<spring:message code='label.numberStudents' var='numberStudents'/>
<spring:message code='label.executionDegrees' var='executionDegrees'/>
<spring:message code='label.participants' var='participants'/>
<spring:message code='label.participantType.select' var='selectParticipantType'/>
<spring:message code='label.userId' var='userId'/>
<spring:message code='label.thesisProposal.participant.add' var='addParticipant'/>
<spring:message code='label.thesisProposal.participant.remove' var='removeParticipant'/>
<spring:message code='button.save' var='saveButton'/>
<spring:message code='label.participant.percentage' var='percentage'/>
<spring:message code='label.participant.name' var='name'/>
<spring:message code='label.participant.email' var='email'/>
<spring:message code='label.participant.external' var='external'/>
<spring:message code='label.participant.external.add' var='addExternal'/>


<div class="form-group">
	<form:label for="thesisProposalIsForFirstCycle" path="forFirstCycle" class="col-sm-2 control-label">${isForFirstCycle}</form:label>
	<div class="col-sm-10">
		<form:radiobutton path = "forFirstCycle" value = "true" id="thesisProposalIsForFirstCycle" />
		<spring:message code='label.isForFirstCycle.yes'/>
		<span style="margin-left: 5px;">&nbsp;</span>
		<form:radiobutton path = "forFirstCycle" value = "false"  />
		<spring:message code='label.isForFirstCycle.no'/>
	</div>
</div>


<div class="form-group">
	<form:label for="thesisProposalTitle" path="title" class="col-sm-2 control-label">${title}</form:label>
	<div class="col-sm-10">
		<form:input type="text" class="form-control" id="thesisProposalTitle" path="title" placeholder="${title}" required="required"/>
	</div>
</div>

<input type="hidden" name="participantsJson" id="participantsJson"/>
<input type="hidden" name="externalsJson" id="externalsJson"/>

<div class="form-group row">
	<label class="col-sm-2 control-label">${participants}</label>
	<div class="col-sm-10">
		<div id="tableBody">
			<c:forEach var="participantBean" items="${command.thesisProposalParticipantsBean}">
				<c:if test="${!participantBean.isExternal()}">
				<div class="tableRow row form-group">
					<div class="col-sm-4">
						<input type="text" class="form-control" id="UserId" bennu-user-autocomplete  value="${participantBean.user.username}" required="required"/>
					</div>
					<c:if test="${participantTypeList.size() == 1}">
						<input type='hidden' id='selectParticipantType' value="${participantTypeList.iterator().next().externalId}"/>
					</c:if>
					<c:if test="${participantTypeList.size() != 1}">
						<div class="col-sm-3">
							<select id="selectParticipantType" class="form-control">
								<option value="">${selectParticipantType}</option>
								<c:forEach var="participantType" items="${participantTypeList}">
									<c:if test="${participantBean.participantTypeExternalId == participantType.externalId}">
										<option value="${participantType.externalId}" selected="selected">${participantType.name.content}</option>
									</c:if>
									<c:if test="${participantBean.participantTypeExternalId != participantType.externalId}">
										<option value="${participantType.externalId}">${participantType.name.content}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</c:if>
					<div class="col-sm-2">
						<div class="input-group">
							<input type="number" min="0" max="100" class="form-control" id="percentage" placeholder="${percentage}" required="required" value="${participantBean.percentage}"/>
							<div class="input-group-addon">%</div>
						</div>
					</div>
					<div class="col-sm-3">
						<a href="#" class="btn btn-default removeParticipant"><span class="glyphicon glyphicon-remove"></span> ${removeParticipant}</a>
					</div>
				</div>
		</c:if>
			</c:forEach>
		</div>
		<a class="btn btn-link" id="addParticipant">${addParticipant}</a>
	</div>
</div>

<div class="form-group row">
	<label class="col-sm-2 control-label">${external}</label>
	<div class="col-sm-10">
		<div id="tableExternalBody">
			<div class="row">
				<div class="col-sm-12">
					<c:forEach var="participantBean" items="${command.thesisProposalParticipantsBean}">
						<c:if test="${participantBean.isExternal()}">
							<div class="tableExternalRow row form-group">
								<div class="input-group">
									<div class="col-sm-2">
										<input type="text" class="form-control" id="name" placeholder="${name}" value="${participantBean.name}"/>
									</div>
									<div class="col-sm-2">
										<input type="text" class="form-control" id="email" placeholder="${email}" value="${participantBean.email}"/>
									</div>
									<c:if test="${participantTypeList.size() == 1}">
										<input type='hidden' id='selectParticipantType' value="${participantTypeList.iterator().next().externalId}"/>
									</c:if>
									<c:if test="${participantTypeList.size() != 1}">
										<div class="col-sm-2">
											<select id="selectParticipantType" class="form-control">
												<option value="">${selectParticipantType}</option>
												<c:forEach var="participantType" items="${participantTypeList}">
													<c:if test="${participantBean.participantTypeExternalId == participantType.externalId}">
														<option value="${participantType.externalId}" selected="selected">${participantType.name.content}</option>
													</c:if>
													<c:if test="${participantBean.participantTypeExternalId != participantType.externalId}">
														<option value="${participantType.externalId}">${participantType.name.content}</option>
													</c:if>
												</c:forEach>
											</select>
										</div>
									</c:if>
									<div class="col-sm-2">
										<div class="input-group">
											<input type="number" min="0" max="100" class="form-control" id="percentage" placeholder="${percentage}" required="required" value="${participantBean.percentage}"/>
											<div class="input-group-addon">%</div>
										</div>
									</div>
									<div class="col-sm-2">
										<a href="#" class="btn btn-default removeExternal"><span class="glyphicon glyphicon-remove"></span> ${removeParticipant}</a>
									</div>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</div>
			</div>
		</div>
		<a class="btn btn-link" id="addExternal">${addExternal}</a>
	</div>
</div>

<div class="form-group">
	<form:label for="thesisProposalGoals" path="goals" class="col-sm-2 control-label">${goals}</form:label>
	<div class="col-sm-10">
		<form:textarea rows="5" class="form-control" id="thesisProposalGoals" path="goals" placeholder="${goals}"/>
	</div>
</div>

<div class="form-group">
	<form:label for="thesisProposalRequirements" path="requirements" class="col-sm-2 control-label">${requirements}</form:label>
	<div class="col-sm-10">
		<form:textarea rows="5" class="form-control" id="thesisProposalRequirements" path="requirements" placeholder="${requirements}"/>
	</div>
</div>

<div class="form-group">
	<form:label for="thesisProposalLocalization" path="localization" class="col-sm-2 control-label">${localization}</form:label>
	<div class="col-sm-10">
		<form:input type="text" class="form-control" id="thesisProposalLocalization" path="localization" placeholder="${localization}"/>
	</div>
</div>

	<div class="form-group row">
		<form:label for="thesisProposalIsCapstone" path="capstone" class="col-sm-2 control-label">${isCapstone}</form:label>
		<div class="col-sm-10">
			<form:radiobutton path = "capstone" value = "true" id="thesisProposalIsCapstone" />
			<spring:message code='label.yes'/>
			<form:radiobutton path = "capstone" value = "false"  />
			<spring:message code='label.no'/>
		</div>
	</div>

	<div class="form-group row">
		<form:label for="thesisProposalMinNumberStudents" path="minStudents" class="col-sm-2 control-label">${numberStudents}</form:label>

		<div class="col-sm-2">
			<div class="input-group">
				<div class="input-group-addon">min</div>
				<form:input path="minStudents" value="${minStudents}" name="minStudents" id="thesisProposalMinNumberStudents"/>
			</div>
		</div>
		<div class="col-sm-2">
			<div class="input-group">
				<div class="input-group-addon">max</div>
				<form:input path="maxStudents" value="${maxStudents}" name="maxStudents" id="thesisProposalMaxNumberStudents"/>
			</div>
		</div>
	</div>

	<div class="form-group row">
		<form:label for="thesisProposalWithExternalColaboration" path="externalColaboration" class="col-sm-2 control-label">${externalColaboration}</form:label>
		<div class="col-sm-10">
			<form:radiobutton path = "externalColaboration" value = "true" id="thesisProposalWithExternalColaboration" onClick="checkboxListener(this); showExternalBlock();"/>
			<spring:message code='label.yes'/>
			<form:radiobutton path = "externalColaboration" value = "false" id="thesisProposalWithExternalColaborationNo" onClick="checkboxListener(this); hideExternalBlock();"/>
			<spring:message code='label.no'/>
		</div>
	</div>

	<div id="externalBlock" <c:if test="${!command.externalColaboration}">style="display: none;"</c:if> >
		<div class="form-group row">
			<form:label for="thesisProposalExternalInstitution" path="externalInstitution" class="col-sm-2 control-label">${externalInstitution}</form:label>
			<div class="col-sm-10">
				<form:input type="text" class="form-control" id="thesisProposalExternalInstitution" path="externalInstitution" placeholder="${externalInstitution}"/>
			</div>
		</div>

		<div class="form-group row">
			<form:label for="thesisProposalAcceptExternalColaborationTerms" path="acceptExternalColaborationTerms" class="col-sm-2 control-label"> </form:label>
			<div class="col-sm-10">
				<form:checkbox path="acceptExternalColaborationTerms" id="thesisProposalAcceptExternalColaborationTerms" onClick="checkboxListener(this)"/>
				<% if (ThesisProposalsSystem.getInstance().getAcceptExternalColaborationTerms() != null) { %>
				<%= ThesisProposalsSystem.getInstance().getAcceptExternalColaborationTerms().getContent() %>
				<% } %>
			</div>
		</div>
	</div>

	<div class="form-group row">
		<form:label for="thesisProposalAcceptEthicsAndDataProtection" path="acceptEthicsAndDataProtection" class="col-sm-2 control-label"> </form:label>
		<div class="col-sm-10">
			<form:checkbox path="acceptEthicsAndDataProtection" id="thesisProposalAcceptEthicsAndDataProtection" onClick="checkboxListener(this)" />
			<% if (ThesisProposalsSystem.getInstance().getAcceptEthicsAndDataProtection() != null) { %>
			<%= ThesisProposalsSystem.getInstance().getAcceptEthicsAndDataProtection().getContent() %>
			<% } %>
		</div>
	</div>

	<div class="form-group">
	<form:label for="thesisProposalObservations" path="observations" class="col-sm-2 control-label">${observations}</form:label>
	<div class="col-sm-10">
		<form:textarea rows="5" class="form-control" id="thesisProposalObservations" path="observations" placeholder="${observations}"/>
	</div>
</div>

<div class="form-group">
	<form:input type="hidden" class="form-control" id="ExternalId" placeholder="ExternalId" path="externalId" required="required"/>
</div>

<div class="form-group">
	<label class="col-sm-2 control-label">${executionDegrees}</label>
	<div class="col-sm-10" id="configurationsSelect">
		<c:forEach items="${configurations}" var="configuration">
			<div class="checkbox">
				<label>
					<c:choose>
						<c:when test="${not empty adminEdit && adminEdit}">
							<form:hidden path="thesisProposalsConfigurations" value="${configuration.externalId}" name="thesisProposalsConfigurations"/>${configuration.executionDegree.presentationName}
						</c:when>
						<c:otherwise>
							<form:checkbox path="thesisProposalsConfigurations" value="${configuration.externalId}" name="thesisProposalsConfigurations"/>${configuration.executionDegree.presentationName}
						</c:otherwise>
					</c:choose>
				</label>
			</div>
	</c:forEach>
</div>
</div>

	<div class="col-sm-offset-3 col-sm-8">
	<div class="col-sm-2"><button type="submit" class="btn btn-default" id="submitButton">${saveButton}</button></div>
	<div class="col-sm-2"><button type="button" class="btn btn-danger" id="deleteButton" onclick="confirmDelete()"><spring:message code='button.delete'/></button></div>
</div>

</div>
</form:form>

<form method="POST" action="${pageContext.request.contextPath}/${baseAction}/delete/${command.externalId}" id="deleteForm">
${csrf.field()}
</form>


<script type="text/html" id="participantRowTemplate">
	<div class="tableRow row form-group">
		<div class="col-sm-4">
			<input type="text" class="form-control" id="UserId" bennu-user-autocomplete  value="${participantBean.user.username}" required="required"/>
		</div>
		<c:if test="${participantTypeList.size() == 1}">
		<input type='hidden' id='selectParticipantType' value="${participantTypeList.iterator().next().externalId}"/>
	</c:if>
	<c:if test="${participantTypeList.size() != 1}">
	<div class="col-sm-3">
		<select id="selectParticipantType" class="form-control">
			<option value="">${selectParticipantType}</option>
			<c:forEach var="participantType" items="${participantTypeList}">
			<c:if test="${participantBean.participantTypeExternalId == participantType.externalId}">
			<option value="${participantType.externalId}" selected="selected">${participantType.name.content}</option>
		</c:if>
		<c:if test="${participantBean.participantTypeExternalId != participantType.externalId}">
		<option value="${participantType.externalId}">${participantType.name.content}</option>
	</c:if>
</c:forEach>
</select>
</div>
</c:if>
<div class="col-sm-2">
	<div class="input-group">
		<input type="number" min="0" max="100" class="form-control" id="percentage" placeholder="${percentage}" required="required" value="${participantBean.percentage}"/>
		<div class="input-group-addon">%</div>
	</div>
</div>
<div class="col-sm-3">
	<a href="#" class="btn btn-default removeParticipant"><span class="glyphicon glyphicon-remove"></span> ${removeParticipant}</a>
</div>
</div>
</script>

<script type="text/html" id="externalRowTemplate">
	<div class="tableExternalRow row form-group">
		<div class="input-group">
			<div class="col-sm-2">
				<input type="text" class="form-control" id="name" placeholder="${name}"/>
			</div>
			<div class="col-sm-2">
				<input type="text" class="form-control" id="email" placeholder="${email}"/>
			</div>
			<c:if test="${participantTypeList.size() == 1}">
			<input type='hidden' id='selectParticipantType' value="${participantTypeList.iterator().next().externalId}"/>
		</c:if>
		<c:if test="${participantTypeList.size() != 1}">
		<div class="col-sm-2">
			<select id="selectParticipantType" class="form-control">
				<option value="">${selectParticipantType}</option>
				<c:forEach var="participantType" items="${participantTypeList}">
				<c:if test="${participantBean.participantTypeExternalId == participantType.externalId}">
				<option value="${participantType.externalId}" selected="selected">${participantType.name.content}</option>
			</c:if>
			<c:if test="${participantBean.participantTypeExternalId != participantType.externalId}">
			<option value="${participantType.externalId}">${participantType.name.content}</option>
		</c:if>
	</c:forEach>
</select>
</div>
</c:if>
<div class="col-sm-2">
	<div class="input-group">
		<input type="number" min="0" max="100" class="form-control" id="percentage" placeholder="${percentage}" required="required" value="${participantBean.percentage}"/>
		<div class="input-group-addon">%</div>
	</div>
</div>
<div class="col-sm-2">
	<a href="#" class="btn btn-default removeExternal"><span class="glyphicon glyphicon-remove"></span> ${removeParticipant}</a>
</div>
</div>
</div>
</script>


<script type="text/javascript">
var onRemoveParticipant = function(e) {
	$(this).closest(".tableRow").remove();
};

$("#addParticipant").on("click", function(e) {
	var addedRow = $("#tableBody").append($("#participantRowTemplate").html());
	$(".removeParticipant", addedRow).on("click", onRemoveParticipant);
});

$(".removeParticipant").on("click", onRemoveParticipant);

var onRemoveExternal = function(e) {
	$(this).closest(".tableExternalRow").remove();
};

$("#addExternal").on("click", function(e) {
	var addedRow = $("#tableExternalBody").append($("#externalRowTemplate").html());
	$(".removeExternal", addedRow).on("click", onRemoveExternal);
});

$(".removeExternal").on("click", onRemoveExternal);

$("#submitButton").on("click", function(e) {

	var participantsJSON = {
		participants: []
	};

	var participants = $("#tableBody").find(".tableRow");

	for (index=0; index < participants.length; index++) {
		participant = participants.eq(index)
		user = participant.find("#UserId").val()
		participantType = participant.find("#selectParticipantType").val()
		percentage = participant.find("#percentage").val()

		participantsJSON.participants.push({
			"userId" : user,
			"userType" : participantType,
			"percentage" : percentage
		});
	}

	$("#participantsJson").val(JSON.stringify(participantsJSON.participants));

	var externalsJSON = {
		externals: []
	};
	var externals = $("#tableExternalBody").find(".tableExternalRow");
	for (index=0; index < externals.length; index++) {
		external = externals.eq(index)

		name = external.find("#name").val()
		email = external.find("#email").val()
		participantType = external.find("#selectParticipantType").val()
		percentage = external.find("#percentage").val()

		externalsJSON.externals.push({
			"name" : name,
			"email" : email,
			"userType" : participantType,
			"percentage" : percentage
		});
	}
	$("#externalsJson").val(JSON.stringify(externalsJSON.externals));
});

function checkboxListener(e) {
	if(($("#configurationsSelect").find(":checked").size() > 0  || ${adminEdit})
			&& $("#thesisProposalAcceptEthicsAndDataProtection").is(":checked")
			&& ($("#thesisProposalAcceptExternalColaborationTerms").is(":checked")
					|| $("#thesisProposalWithExternalColaborationNo").is(":checked"))) {
		$("#submitButton").attr("disabled", false);
	}
	else {
		$("#submitButton").attr("disabled", true);
	}
}

function showExternalBlock() {
	document.getElementById("externalBlock").style.display = "block";
}

function hideExternalBlock() {
	document.getElementById("externalBlock").style.display = "none";
}

$(document).ready(function() {
	checkboxListener(null);
	<c:if test="${empty command.thesisProposalParticipantsBean}">
	$("#addParticipant").click();
	</c:if>
	document.getElementById('thesisProposalMinNumberStudents').type = 'number';
	document.getElementById('thesisProposalMinNumberStudents').min = '1';
	document.getElementById('thesisProposalMaxNumberStudents').type = 'number';
	document.getElementById('thesisProposalMaxNumberStudents').min = '1';
});

function confirmDelete() {
    var x;
    if (confirm("<spring:message code='delete.confirm'/>") == true) {
        $("#deleteForm").submit();
    } 
}

</script>
