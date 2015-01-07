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
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.bennu.core.domain.User;

import com.google.common.collect.Sets;

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

    @Override
    public Set<StudentThesisCandidacy> getStudentThesisCandidacySet() {
	return super.getStudentThesisCandidacySet();
    }

    public ThesisProposalsConfiguration getSingleThesisProposalsConfiguration() {
	return (ThesisProposalsConfiguration) getThesisConfigurationSet().toArray()[0];
    }

    public ThesisProposal(String title, String observations, String requirements, String goals, String localization,
	    List<ThesisProposalParticipant> participants, Set<ThesisProposalsConfiguration> configurations)
	    throws MaxNumberThesisProposalsException, OutOfProposalPeriodException {

	for (ThesisProposalsConfiguration thesisProposalsConfiguration : configurations) {
	    if (!thesisProposalsConfiguration.getProposalPeriod().containsNow()) {
		throw new OutOfProposalPeriodException();
	    }
	}

	for (ThesisProposalParticipant participant : participants) {
	    for (ThesisProposalsConfiguration configuration : configurations) {
		if (configuration.getMaxThesisProposalsByUser() != -1
			&& participant.getUser().getThesisProposalParticipantSet().size() >= configuration
				.getMaxThesisProposalsByUser()) {
		    throw new MaxNumberThesisProposalsException(participant);
		}
	    }
	}

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
	    blockers.add("org.fenixedu.thesisProposals.domain.ThesisProposal cannot be deleted");
	}
    }

    public List<ThesisProposalParticipant> getSortedParticipants() {

	List<ThesisProposalParticipant> sortedParticipants = this.getThesisProposalParticipantSet().stream()
		.collect(Collectors.toList());
	Collections.sort(sortedParticipants, ThesisProposalParticipant.COMPARATOR_BY_WEIGHT);

	return sortedParticipants;
    }

    public static Set<ThesisProposal> readCurrentByParticipant(User user) {

	return ThesisProposalsSystem
		.getInstance()
		.getThesisProposalsSet()
		.stream()
		.filter(proposal -> proposal
			.getThesisConfigurationSet()
			.stream()
			.anyMatch(
				configuration -> configuration.getExecutionDegree().getExecutionYear()
				.isAfterOrEquals(ExecutionYear.readCurrentExecutionYear())))
				.filter(proposal -> !Sets.intersection(proposal.getThesisProposalParticipantSet(),
					user.getThesisProposalParticipantSet()).isEmpty()).collect(Collectors.toSet());
    }

    public Set<ExecutionDegree> getExecutionDegreeSet() {
	return this.getThesisConfigurationSet().stream().map(config -> config.getExecutionDegree()).collect(Collectors.toSet());
    }
}
