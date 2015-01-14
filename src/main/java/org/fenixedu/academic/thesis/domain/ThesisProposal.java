package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;

public class ThesisProposal extends ThesisProposal_Base {

    public final static Comparator<ThesisProposal> COMPARATOR_BY_NUMBER_OF_CANDIDACIES = new Comparator<ThesisProposal>() {

	@Override
	public int compare(ThesisProposal arg0, ThesisProposal arg1) {
	    if (arg1.getStudentThesisCandidacySet().size() < arg0.getStudentThesisCandidacySet().size()) {
		return -1;
	    } else {
		return 1;
	    }
	}
    };
    public static final String SIGNAL_CREATED = "fenixedu.academic.thesis.thesisProposal.created";

    @Override
    public Set<StudentThesisCandidacy> getStudentThesisCandidacySet() {
	return super.getStudentThesisCandidacySet();
    }

    public ThesisProposalsConfiguration getSingleThesisProposalsConfiguration() {
	return (ThesisProposalsConfiguration) getThesisConfigurationSet().toArray()[0];
    }

    public ThesisProposal(String title, String observations, String requirements, String goals, String localization,
	    List<ThesisProposalParticipant> participants, Set<ThesisProposalsConfiguration> configurations) {

	setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
	setIdentifier(ThesisProposalsSystem.getInstance().generateProposalIdentifier());
	setTitle(title);
	setObservations(observations);
	setRequirements(requirements);
	setGoals(goals);
	setLocalization(localization);
	getThesisProposalParticipantSet().addAll(participants);
	getThesisConfigurationSet().addAll(configurations);
    }

    public int getNumberOfStudentCandidacies() {
	return getStudentThesisCandidacySet().size();
    }

    public void delete() {

	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.getThesisConfigurationSet().clear();
	this.getStudentThesisCandidacySet().clear();

	for (ThesisProposalParticipant thesisProposalParticipant : getThesisProposalParticipantSet()) {
	    thesisProposalParticipant.delete();
	}

	this.getThesisProposalParticipantSet().clear();

	this.setThesisProposalsSystem(null);

	deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
	super.checkForDeletionBlockers(blockers);

	if (!getStudentThesisCandidacySet().isEmpty()
		|| !getSingleThesisProposalsConfiguration().getProposalPeriod().containsNow()) {
	    blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.proposals.cant.delete"));
	}
    }

    public List<ThesisProposalParticipant> getSortedParticipants() {

	List<ThesisProposalParticipant> sortedParticipants = this.getThesisProposalParticipantSet().stream()
		.collect(Collectors.toList());
	Collections.sort(sortedParticipants, ThesisProposalParticipant.COMPARATOR_BY_WEIGHT);

	return sortedParticipants;
    }

    public static Set<ThesisProposal> readCurrentByParticipant(User user) {

	return user
		.getThesisProposalParticipantSet()
		.stream()
		.map(participant -> participant.getThesisProposal())
		.filter(proposal -> proposal
			.getThesisConfigurationSet()
			.stream()
			.anyMatch(
				configuration -> configuration.getExecutionDegree().getExecutionYear()
					.isAfterOrEquals(ExecutionYear.readCurrentExecutionYear()))).collect(Collectors.toSet());
    }

    public Set<ExecutionDegree> getExecutionDegreeSet() {
	return this.getThesisConfigurationSet().stream().map(config -> config.getExecutionDegree()).collect(Collectors.toSet());
    }

    @Override
    public String getIdentifier() {
	return super.getIdentifier();
    }
}
