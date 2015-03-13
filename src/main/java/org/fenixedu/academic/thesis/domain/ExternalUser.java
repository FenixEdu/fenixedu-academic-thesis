package org.fenixedu.academic.thesis.domain;

public class ExternalUser extends ExternalUser_Base {

    public ExternalUser(String name, String email) {
        super();
        setName(name);
        setEmail(email);
    }

    public void delete() {
        this.setThesisProposalParticipant(null);
        super.deleteDomainObject();
    }

}
