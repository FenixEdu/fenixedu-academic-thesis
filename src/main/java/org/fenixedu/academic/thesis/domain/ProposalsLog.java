package org.fenixedu.academic.thesis.domain;

import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.DateTime;

public class ProposalsLog extends ProposalsLog_Base {

    public ProposalsLog(ThesisProposal proposal, StudentThesisCandidacy candidacy, String action) {
        super();
        setAuthor(Authenticate.getUser());
        setAction(action);
        setTimestamp(new DateTime());

        setThesisProposal(proposal != null ? proposal.getIdentifier() + " - " + proposal.getTitle() + " ("
                + proposal.getExternalId() + ")" : null);
        setCandidacy(candidacy != null ? candidacy.getRegistration().getStudent().getNumber() + " @ " + candidacy.getTimestamp()
                + " (" + candidacy.getExternalId() + ")" : null);
    }
}
