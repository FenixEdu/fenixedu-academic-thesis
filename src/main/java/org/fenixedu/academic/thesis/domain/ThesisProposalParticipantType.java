package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.commons.i18n.LocalizedString;

public class ThesisProposalParticipantType extends ThesisProposalParticipantType_Base {

    public final static Comparator<ThesisProposalParticipantType> COMPARATOR_BY_WEIGHT = new Comparator<ThesisProposalParticipantType>() {

	@Override
	public int compare(ThesisProposalParticipantType arg0, ThesisProposalParticipantType arg1) {
	    return arg1.getWeight() - arg0.getWeight();
	}
    };

    public ThesisProposalParticipantType() {
	super();
    }

    public ThesisProposalParticipantType(LocalizedString name, int weight) {
	super();
	setName(name);
	setWeight(weight);
	setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
	ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet().add(this);
    }

    public void delete() {
	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.getThesisProposalParticipantSet().clear();
	this.setThesisProposalsSystem(null);

	deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
	super.checkForDeletionBlockers(blockers);
	if (!getThesisProposalParticipantSet().isEmpty()) {
	    blockers.add("org.fenixedu.thesisProposals.domain.ThesisProposalParticipantType cannot be deleted");
	}
    }
}
