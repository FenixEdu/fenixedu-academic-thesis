package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.thesis.domain.ThesisProposal;

public class CannotEditUsedThesisProposalsException extends Exception {

    private ThesisProposal thesisProposal;

    public CannotEditUsedThesisProposalsException(ThesisProposal thesisProposal) {
	this.thesisProposal = thesisProposal;
    }

    public ThesisProposal getThesisProposal() {
	return thesisProposal;
    }

    public void setThesisProposal(ThesisProposal thesisProposal) {
	this.thesisProposal = thesisProposal;
    }

}
