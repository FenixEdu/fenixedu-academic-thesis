package org.fenixedu.academic.domain;

import java.util.Optional;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;

public class DegreeHelper {
    public static void setCanManageThesis(Degree degree, Group group) {
	degree.setThesisManager(group.toPersistentGroup());
    }

    public static Group getCanManageThesis(Degree degree) {
	return Optional.ofNullable(degree.getThesisManager()).orElse(NobodyGroup.get().toPersistentGroup()).toGroup();
    }
}
