<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

${portal.toolkit()}

<div class="page-header">
	<h1><spring:message code="title.thesisProposalsParticipantsType.management"/></h1>
	<h2><spring:message code="title.thesisProposalsParticipantsType.edit"/>: ${command.name.content}</h2>
</div>

<div class="row">
	<form role="form" method="POST" action="${pageContext.request.contextPath}/configuration/editParticipantType" class="form-horizontal" commandname="participantTypeBean" id="participantTypeBean">
		<div class="form-group">
			<spring:message code='label.participantType.name' var="participantTypeName"/>
			<div class="form-group">
				<label for="name" path="name" class="col-sm-2 control-label"><spring:message code='label.participantType.name'/></label>
				<div class="col-sm-10">
					<input  type="text" class="form-control" name="name" id="local" path="local" required="required" bennu-localized-string value='${command.name.json()}'/>
				</div>
			</div>
			<div class="form-group">
				<input type="hidden" class="form-control" id="Weight" name="weight" placeholder="Weight" path="weight" required="required" value="${command.weight}" />
			</div>
			<div class="form-group">
				<input type="hidden" class="form-control" name="externalId" id="ExternalId" placeholder="ExternalId" path="externalId" required="required" value="${command.externalId}"/>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-3 col-sm-8">
					<button type="submit" class="btn btn-default" id="submitButton"><spring:message code='button.save'/></button>
					<button type="button" class="btn btn-danger" id="deleteButton"><spring:message code='button.delete'/></button>
				</div>
			</div>
		</div>
	</form>

	<form method="POST" action="${pageContext.request.contextPath}/configuration/deleteParticipantType/${participantType.externalId}" id="deleteForm">
	</form>

	<script type="text/javascript">
		$("#deleteButton").on("click", function(){ $("#deleteForm").submit(); })
	</script>
</div>
