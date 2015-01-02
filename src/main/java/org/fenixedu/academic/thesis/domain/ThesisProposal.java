package org.fenixedu.academic.thesis.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberThesisProposalsException;
import org.fenixedu.academic.thesis.domain.exception.OutOfProposalPeriodException;
import org.fenixedu.bennu.core.domain.User;

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

    public final static Comparator<ThesisProposal> COMPARATOR_BY_TITLE = new Comparator<ThesisProposal>() {

	@Override
	public int compare(ThesisProposal arg0, ThesisProposal arg1) {
	    return arg1.getTitle().compareToIgnoreCase(arg0.getTitle());
	}
    };

    @Override
    public Set<StudentThesisCandidacy> getStudentThesisCandidacySet() {
	return super.getStudentThesisCandidacySet();
    }

    public ExecutionDegree getSingleExecutionDegree() {
	return (ExecutionDegree) getExecutionDegreeSet().toArray()[0];
    }

    public ThesisProposal(String title, String observations, String requirements, String goals, String localization,
	    List<ThesisProposalParticipant> participants, Set<ExecutionDegree> executionDegrees)
		    throws MaxNumberThesisProposalsException, OutOfProposalPeriodException {

	ArrayList<ThesisProposalsConfiguration> thesisProposalsConfigurations = new ArrayList<ThesisProposalsConfiguration>();
	executionDegrees.stream().forEach((ExecutionDegree e) -> {
	    thesisProposalsConfigurations.add(e.getThesisProposalsConfiguration());
	});

	for (ThesisProposalsConfiguration thesisProposalsConfiguration : thesisProposalsConfigurations) {
	    if (!thesisProposalsConfiguration.getProposalPeriod().containsNow()) {
		throw new OutOfProposalPeriodException();
	    }
	}

	for (ThesisProposalParticipant participant : participants) {
	    if (thesisProposalsConfigurations.get(0).getMaxThesisProposalsByUser() != -1
		    && participant.getUser().getThesisProposalParticipantSet().size() >= thesisProposalsConfigurations.get(0)
			    .getMaxThesisProposalsByUser()) {
		throw new MaxNumberThesisProposalsException(participant);
	    }
	}

	setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
	setTitle(title);
	setObservations(observations);
	setRequirements(requirements);
	setGoals(goals);
	setLocalization(localization);
	getThesisProposalParticipantSet().addAll(participants);
	getExecutionDegreeSet().addAll((executionDegrees));
    }

    public int getNumberOfStudentCandidacies() {
	return getStudentThesisCandidacySet().size();
    }

    public void delete() {

	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.getExecutionDegreeSet().clear();
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
		|| !getSingleExecutionDegree().getThesisProposalsConfiguration().getProposalPeriod().containsNow()) {
	    blockers.add("org.fenixedu.thesisProposals.domain.ThesisProposal cannot be deleted");
	}
    }

    public List<ThesisProposalParticipant> getSortedParticipants() {

	List<ThesisProposalParticipant> sortedParticipants = this.getThesisProposalParticipantSet().stream()
		.collect(Collectors.toList());
	Collections.sort(sortedParticipants, ThesisProposalParticipant.COMPARATOR_BY_WEIGHT);

	return sortedParticipants;
    }

    public static Set<ThesisProposal> readByExecutionDegrees(List<ExecutionDegree> executionDegrees) {

	Set<ThesisProposal> thesisProposals = new HashSet<ThesisProposal>();

	executionDegrees.forEach((ExecutionDegree executionDegree) -> thesisProposals.addAll(executionDegree
		.getThesisProposalSet()));

	return thesisProposals;
    }

    public static Set<ThesisProposal> readByParticipant(User user) {
	Set<ThesisProposal> thesisProposals = new HashSet<ThesisProposal>();

	ThesisProposalsSystem.getInstance().getThesisProposalsSet().forEach((ThesisProposal thesisProposal) -> {
	    for (ThesisProposalParticipant participant : thesisProposal.getThesisProposalParticipantSet()) {
		if (participant.getUser().equals(user)) {
		    thesisProposals.add(thesisProposal);
		}
	    }
	});

	return thesisProposals;
    }

    public static List<ExecutionDegree> getThesisExecutionDegrees() {

	List<ExecutionDegree> executionDegreeList = ExecutionDegree
		.getAllByExecutionYear(ExecutionYear.readCurrentExecutionYear()).stream()
		.filter((ExecutionDegree executionDegree) -> {
		    return executionDegree.getDegree().getCycleTypes().contains(CycleType.SECOND_CYCLE);
		}).collect(Collectors.toList());
	Collections.sort(executionDegreeList,
		ExecutionDegree.EXECUTION_DEGREE_COMPARATORY_BY_DEGREE_TYPE_AND_NAME_AND_EXECUTION_YEAR);

	return executionDegreeList;
    }
}
