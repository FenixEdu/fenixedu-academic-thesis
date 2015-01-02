package org.fenixedu.academic.thesis.ui.bean;

import java.util.ArrayList;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ThesisProposalBean {

    private String title;
    private String observations;
    private String requirements;
    private String goals;
    private String localization;
    private Set<ExecutionDegree> executionDegrees;
    private Set<StudentThesisCandidacy> studentThesisCandidacy;
    private Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean;
    private String externalId;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getObservations() {
	return observations;
    }

    public void setObservations(String observations) {
	this.observations = observations;
    }

    public String getRequirements() {
	return requirements;
    }

    public void setRequirements(String requirements) {
	this.requirements = requirements;
    }

    public String getGoals() {
	return goals;
    }

    public void setGoals(String goals) {
	this.goals = goals;
    }

    public String getLocalization() {
	return localization;
    }

    public void setLocalization(String localization) {
	this.localization = localization;
    }

    public Set<ExecutionDegree> getExecutionDegrees() {
	return executionDegrees;
    }

    public void setExecutionDegree(Set<ExecutionDegree> executionDegrees) {
	this.executionDegrees = executionDegrees;
    }

    public Set<StudentThesisCandidacy> getStudentThesisCandidacy() {
	return studentThesisCandidacy;
    }

    public void setStudentThesisCandidacy(Set<StudentThesisCandidacy> studentThesisCandidacy) {
	this.studentThesisCandidacy = studentThesisCandidacy;
    }

    public Set<ThesisProposalParticipantBean> getThesisProposalParticipantsBean() {
	return thesisProposalParticipantsBean;
    }

    public void setThesisProposalParticipantsBean(Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean) {
	this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
    }

    public String getExternalId() {
	return externalId;
    }

    public void setExternalId(String externalId) {
	this.externalId = externalId;
    }

    public ThesisProposalBean(String title, String observations, String requirements, String goals, String localization,
	    Set<ExecutionDegree> executionDegrees, Set<StudentThesisCandidacy> studentThesisCandidacy,
	    Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean) {
	this.title = title;
	this.observations = observations;
	this.requirements = requirements;
	this.goals = goals;
	this.localization = localization;
	this.executionDegrees = executionDegrees;
	this.studentThesisCandidacy = studentThesisCandidacy;
	this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
    }

    public ThesisProposalBean(String title, String observations, String requirements, String goals, String localization,
	    Set<ExecutionDegree> executionDegrees, Set<StudentThesisCandidacy> studentThesisCandidacy,
	    Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean, String externalId) {
	this.title = title;
	this.observations = observations;
	this.requirements = requirements;
	this.goals = goals;
	this.localization = localization;
	this.executionDegrees = executionDegrees;
	this.studentThesisCandidacy = studentThesisCandidacy;
	this.thesisProposalParticipantsBean = thesisProposalParticipantsBean;
	this.externalId = externalId;
    }

    public ThesisProposalBean() {
    }

    public static class Builder {
	private final String title;
	private final String observations;
	private final String requirements;
	private final String goals;
	private final String localization;
	private final Set<ExecutionDegree> executionDegrees;
	private final Set<ThesisProposalParticipantBean> thesisProposalParticipantsBean;

	public Builder(ThesisProposalBean proposalBean) {
	    this.title = proposalBean.getTitle();
	    this.observations = proposalBean.getObservations();
	    this.requirements = proposalBean.getRequirements();
	    this.goals = proposalBean.getGoals();
	    this.localization = proposalBean.getLocalization();
	    this.executionDegrees = proposalBean.getExecutionDegrees();
	    this.thesisProposalParticipantsBean = proposalBean.getThesisProposalParticipantsBean();
	}

	@Atomic(mode = TxMode.WRITE)
	public ThesisProposal build() throws MaxNumberThesisProposalsException, OutOfProposalPeriodException {
	    ArrayList<ThesisProposalParticipant> participants = new ArrayList<ThesisProposalParticipant>();

	    for (ThesisProposalParticipantBean participantBean : thesisProposalParticipantsBean) {

		ThesisProposalParticipantType participantType = FenixFramework.getDomainObject(participantBean
			.getParticipantTypeExternalId());

		User username = FenixFramework.getDomainObject(participantBean.getUserExternalId());
		ThesisProposalParticipant participant = new ThesisProposalParticipant(username, participantType);

		participants.add(participant);
	    }

	    return new ThesisProposal(title, observations, requirements, goals, localization, participants, executionDegrees);
	}
    }

}
