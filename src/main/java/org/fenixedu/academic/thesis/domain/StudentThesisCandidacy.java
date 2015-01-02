package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.exception.MaxNumberStudentThesisCandidacies;
import org.fenixedu.academic.thesis.domain.exception.OutOfCandidacyPeriodException;
import org.joda.time.DateTime;

public class StudentThesisCandidacy extends StudentThesisCandidacy_Base {

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_PREFERENCE_NUMBER = new Comparator<StudentThesisCandidacy>() {

	@Override
	public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
	    return arg0.getPreferenceNumber() - arg1.getPreferenceNumber();
	}
    };

    public StudentThesisCandidacy(Registration registration, Integer preferenceNumber, ThesisProposal thesisProposal)
	    throws MaxNumberStudentThesisCandidacies, OutOfCandidacyPeriodException {

	ThesisProposalsConfiguration thesisProposalsConfiguration = thesisProposal.getSingleExecutionDegree()
		.getThesisProposalsConfiguration();

	if (!thesisProposalsConfiguration.getCandidacyPeriod().containsNow()) {
	    throw new OutOfCandidacyPeriodException();
	} else {
	    if (thesisProposalsConfiguration.getMaxThesisProposalsByUser() != -1
		    && registration.getStudentThesisCandidacySet().size() >= thesisProposalsConfiguration
		    .getMaxThesisProposalsByUser()) {
		throw new MaxNumberStudentThesisCandidacies(registration.getStudent());
	    } else {
		setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
		setPreferenceNumber(preferenceNumber);
		setAcceptedByAdvisor(false);
		setThesisProposal(thesisProposal);
	    }
	}
    }

    public void delete() {

	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.setThesisProposal(null);
	this.setThesisProposalsSystem(null);
	this.setRegistration(null);

	deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
	super.checkForDeletionBlockers(blockers);

	if (getAcceptedByAdvisor()
		|| !(getThesisProposal().getSingleExecutionDegree().getThesisProposalsConfiguration().getCandidacyPeriod()
			.contains(DateTime.now()))) {
	    blockers.add("org.fenixedu.thesisProposals.domain.ThesisProposal cannot be deleted");
	}
    }
}
