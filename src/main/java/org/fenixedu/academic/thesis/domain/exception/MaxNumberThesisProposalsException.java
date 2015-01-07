package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;

public class MaxNumberThesisProposalsException extends DomainException {

    private static final long serialVersionUID = 4565172166760489278L;
    private final ThesisProposalParticipant participant;

    public ThesisProposalParticipant getParticipant() {
	return participant;
    }

    public MaxNumberThesisProposalsException(ThesisProposalParticipant participant) {
	this.participant = participant;
    }

}
