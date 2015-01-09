package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

public class StudentThesisCandidacy extends StudentThesisCandidacy_Base {

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_PREFERENCE_NUMBER = new Comparator<StudentThesisCandidacy>() {

	@Override
	public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
	    return arg0.getPreferenceNumber() - arg1.getPreferenceNumber();
	}
    };

    public StudentThesisCandidacy(Registration registration, Integer preferenceNumber, ThesisProposal thesisProposal) {
	super();
	setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
	setPreferenceNumber(preferenceNumber);
	setAcceptedByAdvisor(false);
	setThesisProposal(thesisProposal);
	setRegistration(registration);
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
		|| !getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod().contains(DateTime.now())) {
	    blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.candidacies.cant.delete"));
	}
    }
}
