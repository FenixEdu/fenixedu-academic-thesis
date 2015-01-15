package org.fenixedu.academic.thesis.domain.accessControl;

import java.util.Set;

import org.fenixedu.academic.domain.accessControl.FenixGroupStrategy;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

@GroupOperator(ThesisCreatorsGroup.THESIS_CREATORS)
public class ThesisCreatorsGroup extends FenixGroupStrategy {

    private static final long serialVersionUID = -6661148802972245890L;

    public static final String THESIS_CREATORS = "thesisCreators";

    @Override
    public Set<User> getMembers() {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().getMembers();
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().isMember(user);

    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().isMember(user, when);

    }

}
