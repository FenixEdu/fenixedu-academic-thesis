package org.fenixedu.academic.thesis.domain.accessControl;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeHelper;
import org.fenixedu.academic.domain.accessControl.FenixGroupStrategy;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.joda.time.DateTime;

@GroupOperator(ThesisSystemManagerGroup.THESIS_SYSTEM_MANAGERS)
public class ThesisSystemManagerGroup extends FenixGroupStrategy {

    private static final long serialVersionUID = -2075610613209268241L;

    public static final String THESIS_SYSTEM_MANAGERS = "thesisSystemManagers";

    @Override
    public Set<User> getMembers() {
        return computeGroupForMembers().getMembers();
    }

    @Override
    public Set<User> getMembers(DateTime when) {
        return computeGroupForMembers().getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return computeGroupStreamForIsMember().anyMatch(g -> g.isMember(user));
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return computeGroupStreamForIsMember().anyMatch(g -> g.isMember(user, when));
    }

    private Group computeGroupForMembers() {
        return computeGroupStream().reduce(ThesisProposalsSystem.getInstance().getCanManageThesisGroup(), Group::or);
    }

    private Stream<Group> computeGroupStreamForIsMember() {
        return Stream.concat(Stream.of(ThesisProposalsSystem.getInstance().getCanManageThesisGroup()), computeGroupStream());
    }

    private Stream<Group> computeGroupStream() {
        return Bennu.getInstance().getDegreesSet().stream().map(degree -> DegreeHelper.getCanManageThesis(degree))
                .filter(Objects::nonNull);
    }

}
