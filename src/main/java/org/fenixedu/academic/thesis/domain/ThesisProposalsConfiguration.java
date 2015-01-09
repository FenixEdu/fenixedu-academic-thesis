package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.Interval;

public class ThesisProposalsConfiguration extends ThesisProposalsConfiguration_Base {

    static final public Comparator<ThesisProposalsConfiguration> COMPARATOR_BY_YEAR_AND_EXECUTION_DEGREE = new Comparator<ThesisProposalsConfiguration>() {
	@Override
	public int compare(ThesisProposalsConfiguration o1, ThesisProposalsConfiguration o2) {

	    int yearComp = o1.getExecutionDegree().getExecutionYear().compareTo(o2.getExecutionDegree().getExecutionYear());

	    return yearComp != 0 ? yearComp : o1.getExecutionDegree().getDegreeName()
		    .compareTo(o2.getExecutionDegree().getDegreeName());
	}
    };

    public ThesisProposalsConfiguration(Interval proposalPeriod, Interval candidacyPeriod, ExecutionDegree executionDegree,
	    int maxThesisCandidaciesByStudent, int maxThesisProposalsByUser) {
	super();
	setProposalPeriod(proposalPeriod);
	setCandidacyPeriod(candidacyPeriod);
	setExecutionDegree(executionDegree);
	setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
	setMaxThesisCandidaciesByStudent(maxThesisCandidaciesByStudent);
	setMaxThesisProposalsByUser(maxThesisProposalsByUser);
    }

    public ThesisProposalsConfiguration() {
	super();
    }

    public void delete() {

	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.setExecutionDegree(null);
	this.setThesisProposalsSystem(null);

	deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
	super.checkForDeletionBlockers(blockers);
	if (getExecutionDegree() != null && !getThesisProposalSet().isEmpty()) {
	    blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.configurations.cant.delete"));
	}
    }

    public boolean isEquivalent(ThesisProposalsConfiguration configuration) {
	return getMaxThesisCandidaciesByStudent() == configuration.getMaxThesisCandidaciesByStudent()
		&& getMaxThesisProposalsByUser() == configuration.getMaxThesisProposalsByUser()
		&& getProposalPeriod().isEqual(configuration.getProposalPeriod())
		&& getCandidacyPeriod().isEqual(configuration.getCandidacyPeriod());
    }

    @Override
    public String toString() {
	return getProposalPeriod() + " , " + getCandidacyPeriod() + " w/ " + getMaxThesisProposalsByUser() + " , "
		+ getMaxThesisProposalsByUser();

    }
}
