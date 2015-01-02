package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;

public class ThesisProposalParticipant extends ThesisProposalParticipant_Base {

    public final static Comparator<ThesisProposalParticipant> COMPARATOR_BY_WEIGHT = new Comparator<ThesisProposalParticipant>() {

	@Override
	public int compare(ThesisProposalParticipant arg0, ThesisProposalParticipant arg1) {
	    return arg1.getThesisProposalParticipantType().getWeight() - arg0.getThesisProposalParticipantType().getWeight();
	}
    };

    public ThesisProposalParticipant(User user, ThesisProposalParticipantType participantType) {
	super();
	setThesisProposalParticipantType(participantType);
	setUser(user);
    }

    public void delete() {

	DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

	this.getThesisProposalParticipantType().getThesisProposalParticipantSet().remove(this);
	this.setThesisProposalParticipantType(null);

	this.setUser(null);
	this.setThesisProposal(null);

	deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
	super.checkForDeletionBlockers(blockers);
	if (!getThesisProposal().getStudentThesisCandidacySet().isEmpty()) {
	    blockers.add("org.fenixedu.thesisProposals.domain.ThesisProposalParticipant cannot be deleted");
	}
    }
}
