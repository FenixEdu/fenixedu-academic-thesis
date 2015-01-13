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

<c:if test="${!empty deleteException}">
<p class="text-danger"><spring:message code="error.thesisProposal.delete"/></p>
</c:if>

<c:if test="${!empty editOutOfProposalPeriodException}">
<p class="text-danger"><spring:message code="error.thesisProposal.edit.outOfProposalPeriodException"/></p>
</c:if>

<c:if test="${!empty createOutOfProposalPeriodException}">
<p class="text-danger"><spring:message code="error.thesisProposal.create.outOfProposalPeriodException"/></p>
</c:if>

<c:if test="${!empty cannotEditUsedThesisProposalsException}">
<p class="text-danger"><spring:message code="error.thesisProposal.cannotEditUsedThesisProposalsException"/></p>
</c:if>

<c:if test="${!empty createMaxNumberThesisProposalsException}">
<p class="text-danger"><spring:message code="error.thesisProposal.maxNumberThesisProposalsException"/></p>
</c:if>



<div class="well">
   <p>
   <spring:message code="label.proposals.well"/>
   </p>
</div>

<p>
   <div class="row">
      <div class="col-sm-8">
      <form role="form" method="GET" action="${pageContext.request.contextPath}/proposals/create" class="form-horizontal" id="thesisProposalCreateForm">
         <button type="submit" class="btn btn-primary"><spring:message code="button.create"/></button>
         </form>
      </div>
      <div class="col-sm-4">
         <c:if test="${!empty executionYearsList}">
         <select id="executionYearSelect" class="form-control">
            <option value="NONE" label="<spring:message code='label.executionYear.select'/>"/>
            <c:forEach items="${executionYearsList}" var="executionYear">
            <option value="${executionYear.year}" label="${executionYear.year}"/>
         </c:forEach>
      </select>
   </c:if>
</div>
</div>
</p>

<div class="table-responsive">
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
               <spring:message code='label.participants'/>
            </th>
            <th></th>
         </tr>
      </thead>
      <tbody>
         <c:forEach items="${thesisProposalsList}" var="thesisProposal">
         <tr>
						<td>${thesisProposal.identifier}</td>
            <td>${thesisProposal.title}</td>
            <td>
               <c:forEach items="${thesisProposal.getSortedParticipants()}" var="participant">
                           <div>${participant.user.name} <small>as</small> <b>${participant.thesisProposalParticipantType.name.content}</b></div>
               </c:forEach>
         </td>
            <td>
               <form:form method="GET" action="${pageContext.request.contextPath}/proposals/edit/${thesisProposal.externalId}">
                <div class="btn-group btn-group-xs">
               <button type="submit" class="btn btn-default" id="editButton">
                  <spring:message code='button.edit'/>
               </button>

                     <c:set var="result" scope="session" value=''/>
                     <c:forEach items="${thesisProposal.executionDegreeSet}" var="executionDegree" varStatus="i">
                        <c:set var="result" scope="session" value="${result}${executionDegree.degree.sigla}" />
                        <c:if test="${i.index != thesisProposal.executionDegreeSet.size() - 1}">
                        <c:set var="result" scope="session" value="${result}, " />
                        </c:if>
                     </c:forEach>

               <input type='button' class='detailsButton btn btn-default' data-observations="${thesisProposal.observations}" data-requirements="${thesisProposal.requirements}" data-goals="${thesisProposal.goals}" data-localization="${thesisProposal.localization}" data-degrees="${result}" value='<spring:message code="button.details"/>' data-thesis="${thesisProposal.externalId}">
               </div>
               </form:form>
            </td>
         </tr>
         </c:forEach>
      </tbody>
   </table>
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
